package xyz.downgoon.mydk.concurrent;

public interface TrafficLight {

    void turnRed();

    void turnGreen() throws InterruptedException;

    /**
     * blocking unit green light turns on
     *
     * @throws InterruptedException if any thread interrupted the current thread beforeLight or while
     *                              the current thread was waiting for a notification. The
     *                              <i>interrupted status</i> of the current thread is cleared
     *                              when this exception is thrown.
     */
    void waitGreen() throws InterruptedException;


    /**
     * @return {@code true} if the green light has been turn on and {@code false}
     * if the waiting time elapsed before green light
     */
    boolean waitGreen(long timeout) throws InterruptedException;

    boolean isGreen();

}