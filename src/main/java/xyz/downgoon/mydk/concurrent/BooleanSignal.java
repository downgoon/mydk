package xyz.downgoon.mydk.concurrent;

public interface BooleanSignal {

	void setRed();

	void setGreen() throws InterruptedException;

	/**
	 * blocking unit green light turns on
	 * 
	 * @throws InterruptedException
	 *             if any thread interrupted the current thread before or while
	 *             the current thread was waiting for a notification. The
	 *             <i>interrupted status</i> of the current thread is cleared
	 *             when this exception is thrown.
	 */
	void waitGreen() throws InterruptedException;

	boolean isGreen();

}