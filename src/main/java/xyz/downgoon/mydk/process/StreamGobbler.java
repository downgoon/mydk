package xyz.downgoon.mydk.process;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @title StreamGobbler
 * @description 尽全力不断读取流，用于读取子进程的标准输出和错误输出 
 * @author liwei39
 * @date 2014-7-2
 * @version 1.0
 */
public class StreamGobbler implements Runnable {

    private final BufferedReader reader;

    private BlockingQueue<StringLine> buffer;

    private String name;

    /**
     * 侦听器（可空）
     * */
    private final StreamGobblerListener listener;

    private AtomicInteger stringLineCount = new AtomicInteger(0);

    private AtomicBoolean stringLineEnded = new AtomicBoolean(false);

    private volatile IOException ioexception = null;

    public StreamGobbler(InputStream inputStream, String name) {
        this(inputStream, name, null);
    }

    public StreamGobbler(InputStream inputStream, String name, StreamGobblerListener listener) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        this.buffer = new LinkedBlockingQueue<StringLine>();
        this.name = name;
        this.listener = listener;
    }

    @Override
    public void run() {

        String lineTxt = null;
        try {
            while ((lineTxt = reader.readLine()) != null) {
                int lineNum = stringLineCount.incrementAndGet();
                if (lineNum == 1) {
                    if (listener != null) {
                        listener.onReadBegin(name);
                    }
                }
                buffer.offer(new StringLine(lineTxt));
                if (listener != null) {
                    listener.onReadLine(name, lineTxt, lineNum);
                }
            }
        } catch (IOException ioe) {
            ioexception = ioe;
            ioe.printStackTrace();//can't do anything
            if (listener != null) {
                listener.onReadException(name, ioe);
            }
        }
        buffer.offer(new StringLine(null));
        stringLineEnded.set(true);
        if (listener != null) {
            listener.onReadEnd(name, stringLineCount.get());
        }
    }

    /**
     * @return  返回NULL，表示EOF；非NULL，表示行文本；阻塞，表示当前没有可读取的行，但流并没有结束，需要等待后续行到达。
     * */
    public final String readFully() throws InterruptedException {
        /* 遇到EOF，无须再阻塞等待未来的输出流 */
        if (isStreamEnded() && buffer.isEmpty()) {
            return null;
        }
        StringBuilder fullText = new StringBuilder();
        StringLine sl = buffer.take();
        while (sl.isNotNull()) {
            fullText.append(sl.getLine()).append("\r\n");
            sl = buffer.take();
        }
        return fullText.toString();
    }

    /**
     * @return  流已启动
     * */
    public boolean isStreamBegan() {
        return stringLineCount.get() > 0;
    }

    /**
     * @return  流已结束
     * */
    public boolean isStreamEnded() {
        return stringLineEnded.get();
    }

    /**
     * @return  流异常
     * */
    public IOException hasIOException() {
        return ioexception;
    }

    /**
     * @return  返回NULL，表示EOF；非NULL，表示行文本；阻塞，表示当前没有可读取的行，但流并没有结束，需要等待后续行到达。
     * */
    public final String readLine() throws InterruptedException {
        /* 遇到EOF，无须再阻塞等待未来的输出流 */
        if (isStreamEnded() && buffer.isEmpty()) {
            return null;
        }
        StringLine sl = buffer.take();
        return sl.getLine();
    }

    public String getName() {
        return name;
    }

}
