package xyz.downgoon.mydk.process;

import java.io.File;
import java.io.IOException;

/**
 * @title ProcessFork
 * @description Runtime.exec() 封装，支持：子进程输出读取和等待超时 
 * @author liwei39
 * @date 2014-7-2
 * @version 1.0
 */
public class ProcessFork {

    private final File workingDir;

    private final String[] workingEnv;

    private final String threadTag;

    private StreamGobblerListener defaultGobblerListener = new StreamGobblerLogger();

    public ProcessFork(File workingDir) {
        this(workingDir, ProcessFork.class.getSimpleName());
    }

    public ProcessFork(File workingDir, String threadTag) {
        this(workingDir, null, threadTag);
    }

    public ProcessFork(File workingDir, String[] workingEnv, String threadTag) {
        super();
        this.workingDir = workingDir;
        this.workingEnv = workingEnv;
        this.threadTag = threadTag;
    }

    public ForkFuture fork(String command, boolean isDebugMode) throws IOException {
        if (isDebugMode) {
            return fork(command, defaultGobblerListener);
        } else {
            return fork(command, null);
        }
    }

    public ForkFuture fork(String command) throws IOException {
        return fork(command, false);
    }

    public ForkFuture fork(String command, StreamGobblerListener gobblerListener) throws IOException {
        Process process = Runtime.getRuntime().exec(command, workingEnv, workingDir);
        ForkFuture future = new ForkFutureLuxury(process, threadTag, gobblerListener);
        return future;
    }

}
