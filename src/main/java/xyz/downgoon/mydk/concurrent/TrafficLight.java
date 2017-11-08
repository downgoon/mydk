package xyz.downgoon.mydk.concurrent;

/**
 * red/green light: thread will be blocked when red light is on until the light
 * turns to green.
 */
public class TrafficLight implements BooleanSignal {

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

	@Override
	public void setRed() {
		red = true;
	}

	@Override
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
	@Override
	public void waitGreen() throws InterruptedException {
		if (red) {
			synchronized (greenLight) {
				while (red) { // double checking
					greenLight.wait();
				}
			}
		} // end if
	}

	@Override
	public boolean isGreen() {
		return !red;
	}

}
