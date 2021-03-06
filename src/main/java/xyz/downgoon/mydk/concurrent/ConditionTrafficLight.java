package xyz.downgoon.mydk.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * another implementation of {@link SyncNotifyTrafficLight } with {@link Condition } on JDK5
 *
 * @see SyncNotifyTrafficLight
 */
public class ConditionTrafficLight implements TrafficLight {

    private static final boolean RED_ON = false;
    private static final boolean GREEN_ON = true;

    private final Lock lock = new ReentrantLock();

    private final AtomicBoolean greenColor;

    private final Condition greenLight = lock.newCondition();

    /**
     * green light is off on default
     */
    public ConditionTrafficLight() {
        this(RED_ON);
    }

    /**
     * @param isGreen green light is on
     */
    public ConditionTrafficLight(boolean isGreen) {
        this.greenColor = new AtomicBoolean(isGreen);
    }

    @Override
    public void turnRed() {
        greenColor.set(false);
    }

    @Override
    public void turnGreen() throws InterruptedException {
        boolean isRedBefore = greenColor.compareAndSet(RED_ON, GREEN_ON);
        if (isRedBefore) {
            lock.lock();
            try { // signal all on changed to Green from Red
                greenLight.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void waitGreen() throws InterruptedException {
        waitGreen(-1L);
    }

    @Override
    public boolean waitGreen(long timeout) throws InterruptedException {
        if (greenColor.get()) {
            return true; // do nothing
        }

        lock.lock();
        try {
            if (timeout >= 0) {
                return greenLight.await(timeout, TimeUnit.MICROSECONDS);
            } else {
                greenLight.await();
                return true;
            }

        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isGreen() {
        return greenColor.get();
    }

}
