package xyz.downgoon.mydk.testing;

import xyz.downgoon.mydk.concurrent.ConcurrentCounter;
import xyz.downgoon.mydk.concurrent.Counter;
import xyz.downgoon.mydk.concurrent.LatchTrafficLight;
import xyz.downgoon.mydk.concurrent.TrafficLight;
import xyz.downgoon.mydk.util.ImmutableOrderedHash;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * White-Box Unified Concurrent Testing Framework
 *
 * @author downgoon@qq.com
 * @date 2019/05/08
 */

public class XrayCT implements Xray {

    private String xrayName;

    private volatile ImmutableOrderedHash<String, TrafficLight> dotLightsHashInited = null;

    /**
     * e.g. "T1#S1", "T1#S2", "T2#S1", "T2#S2"
     */
    private final List<String> dotIds = new ArrayList<>();

    /**
     * e.g. "S1", "S2"
     */
    private final Set<String> dotNames = new LinkedHashSet<>();


    /**
     * threadName -> number of steps which have been finished
     */
    private final Counter threadFinSteps = new ConcurrentCounter();

    private volatile TripleConsumer<Boolean, String, String> dotConsumer;

    private Logger LOG = Logger.getLogger(XrayCT.class.getName());

    XrayCT(String xrayName) {
        this.xrayName = xrayName;
    }


    @Override
    public void dot(String dotName) {
        if (dotLightsHashInited == null) {
            return;
        }

        String dotId = DotUtils.toDotId(dotName);
        System.out.println(String.format("dotcall %s", dotId));

        // await green light for previous dot
        AtomicBoolean isHead = new AtomicBoolean();
        TrafficLight preTrafficLight = dotLightsHashInited.getBefore(dotId, isHead);
        if (preTrafficLight == null && !isHead.get()) {
            // key not found, do nothing
            return;
        }
        if (preTrafficLight != null && isHead.get()) {
            // illegal state
            return;
        }

        if (preTrafficLight != null && !isHead.get()) {
            System.out.println(String.format("[%s] -- await green light: [%s]", dotId, preTrafficLight));
            try {
                preTrafficLight.waitGreen();
                System.out.println(String.format("[%s] -| got green light: [%s]", dotId, preTrafficLight));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
         * (preTrafficLight != null && !isHead.get()) || (preTrafficLight == null && isHead.get())
         * */
        // dot consumer callback
        if (dotConsumer != null) {
            threadFinSteps.increaseAndGet(Thread.currentThread().getName());
            dotConsumer.accept(true, Thread.currentThread().getName(), dotName);
        }


        // turn green light for next dot
        TrafficLight currTrafficLight = dotLightsHashInited.getValue(dotId);
        if (currTrafficLight == null) {
            return;
        }
        try {
            System.out.println(String.format("[%s] || turn green light: [%s]", dotId, currTrafficLight));
            currTrafficLight.turnGreen();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public XrayCT seqTS(String threadName, String dotName) {
        dotIds.add(DotUtils.toDotId(threadName, dotName));
        dotNames.add(dotName);
        return this;
    }


    /**
     * @param seqNotations a notation sequence of dotId, e.g. "T1#S1", "T2#S1", "T1#S2", "T2#S2".
     *                     a dotId is a combination of threadName and dotName conjunct with '#',
     *                     like 'T1#S2' indicating the 2nd step at the 1st thread
     */
    public XrayCT seq(String... seqNotations) {
        for (String dotId : seqNotations) {
            String[] pair = DotUtils.parseDotId(dotId);
            seqTS(pair[0], pair[1]);
        }
        return this;
    }

    /**
     * start concurrent testing
     *
     * @param concurrentTarget concurrentTarget job to be executed on multi-threads
     * @param threadNames      multi-threads size and their names
     */
    public XrayCT start(Runnable concurrentTarget, String... threadNames) {
        if (dotIds.size() > 0) {
            System.out.println("dotIds: " + dotIds);
            if (dotLightsHashInited != null) {
                throw new IllegalStateException(String.format("XrayCT [%s] already started", xrayName));
            }
            while (dotLightsHashInited == null) {
                dotLightsHashInited = DotUtils.dotLightsHash(dotIds);
            }

            System.out.println(">>>" + this.toString());

        }

        CountDownLatch readyLatch = new CountDownLatch(threadNames.length);
        for (String threadName : threadNames) {
            Thread thread = new Thread(() -> {
                try {
                    readyLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                concurrentTarget.run();
                hookThreadOnClose(Thread.currentThread().getName());

            }, threadName);

            thread.start();
            readyLatch.countDown();
        }
        return this;
    }


    /**
     *
     * */
    protected void hookThreadOnClose(String threadName) {
        if (threadFinSteps.getCount(threadName) < dotNames.size()) {

            for (String dotName : dotNames) {
                String dotId = DotUtils.toDotId(threadName, dotName);
                TrafficLight light = dotLightsHashInited.getValue(dotId);
                if (!light.isGreen()) {
                    try {
                        light.turnGreen();
                        dotConsumer.accept(false, threadName, dotName);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }


    /**
     * start concurrent testing
     *
     * @param concurrentTarget concurrentTarget job to be executed on multi-threads
     * @param threadNames      multi-threads size and their names
     * @param dotConsumer      dot consumer callback
     */
    public XrayCT start(Runnable concurrentTarget, String[] threadNames, TripleConsumer<Boolean, String, String> dotConsumer) {
        this.dotConsumer = dotConsumer;
        return start(concurrentTarget, threadNames);
    }

    /**
     * await the last dot to be finished and then callback
     */
    public void await(Consumer<String> callback) {
        if (dotLightsHashInited != null && dotLightsHashInited.size() > 0) {
            try {
                dotLightsHashInited.getTail().waitGreen();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (callback != null) {
            callback.accept(xrayName);
        }
    }

    public void await() {
        await(null);
    }


    @Override
    public String toString() {
        return xrayName + " => " + dotLightsHashInited;
    }


    private static class DotUtils {

        /**
         * generate dot lights hash for dot sequence
         */
        public static ImmutableOrderedHash<String, TrafficLight> dotLightsHash(List<String> dotIds) {
            LinkedHashMap<String, TrafficLight> hash = new LinkedHashMap<>();
            for (String dotId : dotIds) {
                hash.put(dotId, new TaggedTrafficLight(dotId));
            }

            return new ImmutableOrderedHash<String, TrafficLight>(hash);
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

        /**
         * split the dotId into threadName and dotName
         *
         * @param dotId a dotId is a combination of threadName and dotName conjunct with '#', like 'T1#S2' indicating the 2nd step at the 1st thread
         * @return the pair of threadName and dotName
         */
        public static String[] parseDotId(String dotId) {
            int idx = dotId.indexOf("#");
            if (idx == -1) {
                throw new IllegalArgumentException(String.format(
                        "a dotId is a combination of threadName and dotName conjunct with '#', " +
                                "like 'T1#S2' indicating the 2nd step at the 1st thread, rather than [%s]", dotId));
            }
            return new String[]{
                    dotId.substring(0, idx),
                    dotId.substring(idx + "#".length())
            };

        }

    }

    private static class TaggedTrafficLight extends LatchTrafficLight {
        private String tag;

        public TaggedTrafficLight(String tag) {
            this.tag = tag;
        }

        @Override
        public String toString() {
            return "TrafficLight{" + tag + '}';
        }

    }


}
