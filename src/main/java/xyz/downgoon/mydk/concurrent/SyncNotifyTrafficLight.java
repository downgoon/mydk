package xyz.downgoon.mydk.concurrent;

/**
 * red/green light: thread will be blocked when red light is on until the light
 * turns to green.
 */
public class SyncNotifyTrafficLight implements TrafficLight {

    /**
     * red light is on
     */
    private volatile boolean red = true;

    private Object greenLight = new Object();

    /**
     * green light is off on default
     */
    public SyncNotifyTrafficLight() {
        this.red = true;
    }

    /**
     * @param isGreen green light is on
     */
    public SyncNotifyTrafficLight(boolean isGreen) {
        red = !isGreen;
    }

    @Override
    public void turnRed() {
        red = true;
    }

    @Override
    public void turnGreen() throws InterruptedException {
        red = false;
        synchronized (greenLight) {
            greenLight.notifyAll();
        }
    }

    /**
     * blocking unit green light turns on
     *
     * @throws InterruptedException if any thread interrupted the current thread beforeLight or while
     *                              the current thread was waiting for a notification. The
     *                              <i>interrupted status</i> of the current thread is cleared
     *                              when this exception is thrown.
     */
    @Override
    public void waitGreen() throws InterruptedException {
        waitGreen(-1L);
    }


    @Override
    public boolean waitGreen(long timeout) throws InterruptedException {
        if (red) {
            synchronized (greenLight) {
                while (red) { // double checking
                    if (timeout >= 0) {
                        greenLight.wait(timeout);
                        return true;
                    } else {
                        greenLight.wait();
                        return true;
                    }

                }
            }
        } // end if
        return true;
    }

    @Override
    public boolean isGreen() {
        return !red;
    }

}
