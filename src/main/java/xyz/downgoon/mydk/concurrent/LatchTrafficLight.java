package xyz.downgoon.mydk.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LatchTrafficLight implements TrafficLight {

    private volatile CountDownLatch greenLatch = new CountDownLatch(1);

    @Override
    public void turnRed() {
        // CopyOnWrite
        // greenLatch = new CountDownLatch(1);
        throw new IllegalStateException("can't turn red");
    }

    @Override
    public void turnGreen() throws InterruptedException {
        greenLatch.countDown();
    }

    @Override
    public void waitGreen() throws InterruptedException {
        greenLatch.await();
    }

    @Override
    public boolean waitGreen(long timeout) throws InterruptedException {
        if (timeout >= 0) {
            return greenLatch.await(timeout, TimeUnit.MICROSECONDS);
        } else {
            greenLatch.await();
            return true;
        }

    }

    @Override
    public boolean isGreen() {
        return greenLatch.getCount() <= 0;
    }
}
