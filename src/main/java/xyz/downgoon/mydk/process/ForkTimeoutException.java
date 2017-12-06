package xyz.downgoon.mydk.process;

/**
 * @title ForkTimeoutException
 * @description 子进程超时异常 
 * @author liwei39
 * @date 2014-7-2
 * @version 1.0
 */
public class ForkTimeoutException extends Exception {

    private static final long serialVersionUID = 4070951690197410587L;

    private int exitValue;

    public ForkTimeoutException(int exitValue) {
        super();
        this.exitValue = exitValue;
    }

    public ForkTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForkTimeoutException(String message) {
        super(message);
    }

    public ForkTimeoutException(Throwable cause) {
        super(cause);
    }

    public int getExitValue() {
        return exitValue;
    }

}
