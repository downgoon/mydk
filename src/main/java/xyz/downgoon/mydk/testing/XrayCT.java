package xyz.downgoon.mydk.testing;

import xyz.downgoon.mydk.concurrent.BooleanSignal;
import xyz.downgoon.mydk.concurrent.ConditionTrafficLight;
import xyz.downgoon.mydk.util.NonThreadSafeOrderedHash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * White-Box Unified Concurrent Testing Framework
 *
 * @author downgoon@qq.com
 * @date 2019/05/08
 */

public class XrayCT implements Xray {

    private String xrayName;

    private volatile ImmutableDotSequence dotSeqInited = null;

    private List<String> dotIds = new ArrayList<>();

    private List<String> logTracer = new CopyOnWriteArrayList<String>();

    XrayCT(String xrayName) {
        this.xrayName = xrayName;
        logTracer.add(String.format("log tracer for [%s]\r\n", xrayName));
    }


    @Override
    public void dot(String dotName) {
        if (dotSeqInited == null) {
            return;
        }
        String dotId = ImmutableDotSequence.toDotId(dotName);
        BooleanSignal preLight = dotSeqInited.before(dotId);
        if (preLight == null) {
            return;
        }

        try {
            preLight.waitGreen();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logTracer.add(dotId + "\r\n");

        BooleanSignal postLight = dotSeqInited.after(dotId);
        if (postLight == null) {
            return;
        }
        try {
            postLight.setGreen();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public XrayCT seq(String threadName, String dotName) {
        dotIds.add(ImmutableDotSequence.toDotId(threadName, dotName));
        return this;
    }

    /**
     * start concurrent testing
     *
     * @param runnable    runnable job to be executed on multi-threads
     * @param threadNames multi-threads size and their names
     */
    public XrayCT start(Runnable runnable, String... threadNames) {
        if (dotIds.size() > 0) {
            if (dotSeqInited != null) {
                throw new IllegalStateException(String.format("XrayCT [%s] already started", xrayName));
            }
            while (dotSeqInited == null) {
                dotSeqInited = new ImmutableDotSequence(dotIds);
            }
            try {
                dotSeqInited.head().setGreen();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (String threadName : threadNames
                ) {
            Thread thread = new Thread(runnable, threadName);
            thread.start();
        }
        return this;
    }

    public void await(Consumer<List<String>> callback) {
        if (dotSeqInited != null && dotSeqInited.size() > 0) {
            try {
                dotSeqInited.tail().waitGreen();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (callback != null) {
            callback.accept(logTracer);
        }
    }

    public void await() {
        await(null);
    }


    public void startAndAwait(Runnable runnable, String[] threadNames, Consumer<List<String>> callback) {
        start(runnable, threadNames).await(callback);
    }


    @Override
    public String toString() {
        return xrayName;
    }

    /**
     * a thread-safe ordered-hash sequence
     */
    private static class ImmutableDotSequence {

        private NonThreadSafeOrderedHash<String, BooleanSignal> seq = new NonThreadSafeOrderedHash<>();

        /**
         * {@link ImmutableDotSequence} is thread-safe, though its underlying dependency class {@link NonThreadSafeOrderedHash} is not.
         *
         * @param dotIds dotId list rather than dotName
         */
        ImmutableDotSequence(List<String> dotIds) {
            for (String dotId : dotIds
                    ) {
                seq.add(dotId, new ConditionTrafficLight());
            }
        }

        /**
         * dot-id is the combination of thread-name and dot-name
         */
        public static String toDotId(String threadName, String dotName) {
            return threadName + "#" + dotName;
        }

        /**
         * return dot-id for the currently running thread on dot-name
         */
        public static String toDotId(String dotName) {
            return toDotId(Thread.currentThread().getName(), dotName);
        }


        public int size() {
            return seq.size();
        }

        public BooleanSignal head() {
            return seq.getHead();
        }

        public BooleanSignal tail() {
            return seq.getTail();
        }

        /**
         * @param dotId dotId not dotName
         */
        public BooleanSignal before(String dotId) {
            return seq.getBefore(dotId);
        }

        /**
         * @param dotId dotId not dotName
         */
        public BooleanSignal after(String dotId) {
            return seq.getAfter(dotId);
        }

    }


}
