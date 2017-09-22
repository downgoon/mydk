package xyz.downgoon.concurrent;

/**
 * red/green light: thread will be blocked when red light is on until the light
 * turns to green.
 */
public class TrafficLight {

	/**
	 * red light is on
	 */
	private volatile boolean red = true;

	private Object greenLight = new Object();

	/**
	 * green light is off on default
	 */
	public TrafficLight() {
		this.red = true;
	}

	/**
	 * @param isGreen
	 *            green light is on
	 */
	public TrafficLight(boolean isGreen) {
		red = !isGreen;
	}

	public void setRed() {
		red = true;
	}

	public void setGreen() throws InterruptedException {
		red = false;
		synchronized (greenLight) {
			greenLight.notifyAll();
		}
	}

	/**
	 * blocking unit green light turns on
	 * 
	 * @throws InterruptedException
	 *             if any thread interrupted the current thread before or while
	 *             the current thread was waiting for a notification. The
	 *             <i>interrupted status</i> of the current thread is cleared
	 *             when this exception is thrown.
	 */
	public void waitGreen() throws InterruptedException {
		if (red) {
			synchronized (greenLight) {
				while (red) { // double checking
					greenLight.wait();
				}
			}
		} // end if
	}

	public boolean isGreen() {
		return !red;
	}

}
