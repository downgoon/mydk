package xyz.downgoon.mydk.process;

import java.util.concurrent.TimeUnit;

/**
 * @title ForkFuture
 * @description TODO 
 * @author liwei39
 * @date 2014-7-3
 * @version 1.0
 */
public interface ForkFuture {

    public abstract String readLineStdout() throws InterruptedException;

    public abstract String readLineStderr() throws InterruptedException;

    public abstract String readFullyStdout() throws InterruptedException;

    public abstract String readFullyStderr() throws InterruptedException;

    /**
     * 截止方法调用时，子进程是否产生过标准输出
     * @return  子进程截止方法调用时，产生过标准输出，则返回TRUE；否则，返回FALSE。
     * */
    public abstract boolean hasStdout();

    /**
     * 截止方法调用时，子进程是否产生过错误输出。
     * @return  子进程截止方法调用时，产生过错误输出，则返回TRUE；否则，返回FALSE。
     * */
    public abstract boolean hasStderr();

    /**
     * 子进程是否已经结束
     * */
    public abstract boolean isTerminated();

    /**
     * 子进程是否已经结束，并返回结束ExitValue
     * @return 返回NULL，表示子进程尚未结束；否则，返回ExitValue
     * */
    public abstract Integer hasTerminated();

    public abstract int awaitTerminated(long timeout, TimeUnit unit) throws ForkTimeoutException, InterruptedException;

}