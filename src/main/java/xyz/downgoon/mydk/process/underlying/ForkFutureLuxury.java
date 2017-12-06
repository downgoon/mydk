package xyz.downgoon.mydk.process.underlying;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.downgoon.mydk.concurrent.TagThreadFactory;
import xyz.downgoon.mydk.process.ForkFuture;
import xyz.downgoon.mydk.process.ForkTimeoutException;
import xyz.downgoon.mydk.process.PumperListener;

/**
 * @title ForkFuture
 * @description TODO 
 * @author liwei39
 * @date 2014-7-2
 * @version 1.0
 */
public final class ForkFutureLuxury implements ForkFuture {

    private static final Logger LOG = LoggerFactory.getLogger(ForkFutureLuxury.class);

    private Process process;

    private StreamPumper stdoutPumper;

    private StreamPumper stderrPumper;

    private final String threadTag;

    private final ThreadFactory threadFactory;

    public ForkFutureLuxury(Process process, String threadTag) {
        this(process, threadTag, null);
    }

    //TODO 比较消耗线程，性能较差，但API使用舒服，有待调整。
    public ForkFutureLuxury(Process process, String threadTag, PumperListener pumperListener) {
        super();
        this.process = process;
        this.threadTag = threadTag;
        this.threadFactory = new TagThreadFactory(threadTag);
        this.stdoutPumper = new StreamPumper(process.getInputStream(), "forkout", pumperListener);
        this.stderrPumper = new StreamPumper(process.getErrorStream(), "forkerr", pumperListener);
        pumperForkStream();
    }

    private void pumperForkStream() {
        ExecutorService executorService = Executors.newFixedThreadPool(2, threadFactory);//not reusable
        try {
            executorService.submit(stdoutPumper);
            executorService.submit(stderrPumper);
        } finally {
            executorService.shutdown();
        }
    }

    @Override
    public String readLineStdout() throws InterruptedException {
        return stdoutPumper.readLine();
    }

    @Override
    public String readLineStderr() throws InterruptedException {
        return stderrPumper.readLine();
    }

    @Override
    public String readFullyStdout() throws InterruptedException {
        return stdoutPumper.readFully();
    }

    @Override
    public String readFullyStderr() throws InterruptedException {
        return stderrPumper.readFully();
    }

    @Override
    public boolean hasStdout() {
        return stdoutPumper.isStreamBegan();
    }

    @Override
    public boolean hasStderr() {
        return stderrPumper.isStreamBegan();
    }

    @Override
    public boolean isTerminated() {
        return hasTerminated() != null;
    }

    /**
     * @return  返回NULL，表示未结束；非NULL，表示已结束，数值表示ExitValue
     * */
    @Override
    public Integer hasTerminated() {
        try {
            /* java.lang.IllegalThreadStateException: process has not exited */
            return process.exitValue();
        } catch (IllegalThreadStateException itse) {
            return null;//not exited
        } catch (Exception e) {
            return null;//not exited
        }
    }

    private final CountDownLatch awaitTimeoutLatch = new CountDownLatch(1);
    private final AtomicBoolean isTimeoutFlag = new AtomicBoolean(false);
    private final AtomicBoolean awaitDetectorTriggered = new AtomicBoolean(false);

    @Override
    public int awaitTerminated(final long timeout, final TimeUnit unit) throws InterruptedException,
            ForkTimeoutException {

        /* TimeoutDetector只触发一次，即使多线程调用时 */
        if (awaitDetectorTriggered.compareAndSet(false, true)) {
            if (!isTerminated()) {//子进程尚未结束，如果已结束，则不启动。
                LOG.debug("fork process timeout detector triggered ...");
                threadFactory.newThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            isTimeoutFlag.set(!awaitTimeoutLatch.await(timeout, unit));

                        } catch (InterruptedException ie) {
                            isTimeoutFlag.set(true);
                            ie.printStackTrace();//can't do anything

                        } finally {
                            if (isTimeoutFlag.get()) {//!isTerminated()
                                process.destroy();//destroy sub process on timeout event
                                LOG.warn("fork timeout detector destroy process");
                            }
                            //do nothing on normal termination
                        }
                    }

                }).start();
            }
        }

        try {
            LOG.debug("fork process waiting for termination ...");
            /* 子进程被destory，返回1；但返回1，并不一定意味着destory */
            int exitValue = process.waitFor();
            LOG.debug("fork process terminated with {}, timeout flag is {}", exitValue, isTimeoutFlag.get());
            if (isTimeoutFlag.get()) {
                throw new ForkTimeoutException(exitValue);
            }
            return exitValue;

        } finally {
            awaitTimeoutLatch.countDown();
        }

    }

    public String getThreadTag() {
        return threadTag;
    }

}
