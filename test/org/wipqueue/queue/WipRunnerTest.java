package org.wipqueue.queue;

import org.junit.Test;
import org.wipqueue.utils.SynchronisationPoint;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * copyright Tiest Vilee 2012
 * Date: 02/04/2012
 * Time: 10:42
 */
public class WipRunnerTest {

    WipRunner runner = new WipRunner() {
        @Override
        protected void doThisRepeatedly() {
            repeatCount.incrementAndGet(); // makes sure this method is called numerous times
        }
    };

    WipRunner slowRunner = new WipRunner() {
        @Override
        protected void doThisRepeatedly() {
            repeatCount.incrementAndGet();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            repeatCount.incrementAndGet();
        }
    };
    private AtomicInteger repeatCount = new AtomicInteger(0);
    private SynchronisationPoint startRunner = new SynchronisationPoint();
    private Exception testerException;
    private boolean gotToEnd;

    @Test
    public void shouldRunUntilStopped() throws Exception {
        Thread runnerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    startRunner.await();
                    Thread.sleep(10);
                    runner.run();
                    gotToEnd = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        Thread testerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    assertThat(runner.isStarted(), is(false));
                    startRunner.reached();
                    runner.waitUntilStarted();
                    assertThat(runner.isStarted(), is(true));
                    assertThat(runner.isFinished(), is(false));

                    while(repeatCount.get() == 0) {
                        Thread.sleep(1);
                    }

                    runner.stop();
                    runner.waitUntilFinished();
                    assertThat(runner.isFinished(), is(true));
                } catch (Exception e) {
                    testerException = e;
                }
            }
        });

        runnerThread.start();
        testerThread.start();

        runnerThread.join();
        testerThread.join();

        if(testerException != null) {
            throw testerException;
        }

        assertThat(gotToEnd, is(true));
        assertThat(repeatCount.get(), is(greaterThan(0)));
    }

    @Test
    public void shouldRunUntilKilled() throws Exception {
        Thread runnerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    startRunner.await();
                    Thread.sleep(10);
                    runner.run();
                    gotToEnd = true;
                } catch (InterruptedException e) {
                    testerException = e;
                }
            }
        });
        Thread testerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    startRunner.reached();
                    runner.waitUntilStarted();

                    while(repeatCount.get() == 0) {
                        Thread.sleep(1);
                    }

                    runner.kill();
                    assertThat(runner.isFinished(), is(true));
                } catch (Exception e) {
                    testerException = e;
                }
            }
        });

        runnerThread.start();
        testerThread.start();

        runnerThread.join();
        testerThread.join();

        if(testerException != null) {
            throw testerException;
        }

        assertThat(gotToEnd, is(false));
        assertThat(repeatCount.get(), is(greaterThan(0)));
    }

}
