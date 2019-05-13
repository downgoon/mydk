package xyz.downgoon.mydk.concurrent;

import java.util.concurrent.CountDownLatch;

public class LatchTrafficLight implements TrafficLight {

    private volatile CountDownLatch greenLatch = new CountDownLatch(1);

    @Override
    public void setRed() {
        // CopyOnWrite
        // greenLatch = new CountDownLatch(1);
        throw new IllegalStateException("can't turn red");
    }

    @Override
    public void setGreen() throws InterruptedException {
        greenLatch.countDown();
    }

    @Override
    public void waitGreen() throws InterruptedException {
        greenLatch.await();
    }

    @Override
    public boolean isGreen() {
        return greenLatch.getCount() <= 0;
    }
}
