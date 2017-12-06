package xyz.downgoon.mydk.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @title TagThreadFactory
 * @description 可命名的ThreadFactory 
 * @author liwei39
 * @date 2014-7-3
 * @version 1.0
 */
public class TagThreadFactory implements ThreadFactory {

    private String tag;

    //TODO tag 重名判断 
    public TagThreadFactory(String tag) {
        super();
        this.tag = tag;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "Tag-" + poolNumber.getAndIncrement() + "-" + tag + "-";
    }

    static final AtomicInteger poolNumber = new AtomicInteger(1);
    final ThreadGroup group;
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String namePrefix;

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

    public String getTag() {
        return tag;
    }

}
