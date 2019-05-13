package xyz.downgoon.mydk.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * a watchdog is a controllable thread who does the {@link WatchJob} at
 * intervals and takes orders such as {@link #suspend()}, {@link #resume()} or
 * {@link #stop()} from the caller. of course we can view its status at any time
 * by calling {@link #isJobDoing()}, {@link #isSleeping()} or
 * {@link #isSuspended()}.
 */
public class Watchdog {

	private volatile boolean keepWatching = true;

	private volatile boolean suspended = false;

	/** suspended disable notification */
	private final Object resumeSignal = new Object();

	private Thread thread;

	private final long intervalMS;

	/** call times of {@link WatchJob} */
	private AtomicLong times = new AtomicLong(0);

	private static final AtomicLong _INSTANCE_COUNT = new AtomicLong(0);

	private JobInterrupttedHandler jobInterrupttedHandler = null;

	private volatile boolean sleeping = false;

	private volatile boolean jobDoing = false;

	public Watchdog(WatchJob job) {
		this(job, 0L);
	}

	public Watchdog(WatchJob job, long intervalMS) {
		this(job, intervalMS, "watchdog-" + _INSTANCE_COUNT.getAndIncrement());
	}

	public Watchdog(WatchJob job, long intervalMS, String name) {
		this.intervalMS = intervalMS;
		this.thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (keepWatching) {
					try {

						// checking suspend toggle
						waitIfSuspended();

						// one round watch
						jobDoing = true;
						job.watch(times.incrementAndGet(), name);
						jobDoing = false;

						// sleeping beforeLight next round
						if (intervalMS > 0) {
							sleeping = true;
							Thread.sleep(intervalMS);
							sleeping = false;
						}

					} catch (InterruptedException e) {
						/*
						 * callback jobInterrupttedHandler only interrupted in
						 * job doing status
						 */
						if (jobDoing && jobInterrupttedHandler != null) {
							jobInterrupttedHandler.interrupt(e, times.get(), name);
						}
					}
				}
			}

		}, name);

	}

	private void waitIfSuspended() throws InterruptedException {
		synchronized (resumeSignal) {
			while (suspended) {
				resumeSignal.wait();
			}
		}
	}

	private void notifyIfSuspended() {
		synchronized (resumeSignal) {
			resumeSignal.notifyAll();
		}
	}

	public Watchdog start() {
		thread.start();
		return this;
	}

	public void stop() {
		this.keepWatching = false;
		notifyIfSuspended();
		thread.interrupt();
	}

	public void suspend() {
		if (!thread.isAlive()) {
			throw new IllegalStateException("can't suspend/pause when not alive");
		}
		this.suspended = true;
	}

	public void pause() {
		suspend();
	}

	public void resume() {
		if (!thread.isAlive()) {
			throw new IllegalStateException("can't resume when stopped");
		}
		this.suspended = false;
		notifyIfSuspended();
	}

	public boolean isAlive() {
		return thread.isAlive();
	}

	public boolean isSleeping() {
		return sleeping;
	}

	public boolean isJobDoing() {
		return jobDoing;
	}

	/**
	 * @return call times of {@link WatchJob}
	 */
	public long getJobTimes() {
		return times.get();
	}

	public boolean isSuspended() {
		return suspended;
	}

	public boolean isPaused() {
		return isSuspended();
	}

	/**
	 * @return sleeping interval millisecond between two calls of
	 *         {@link WatchJob}
	 */
	public long getIntervalMS() {
		return intervalMS;
	}

	/**
	 * @return underling thread name of watchdog
	 */
	public String getName() {
		return thread.getName();
	}

	@Override
	public String toString() {
		return "Watchdog [alive=" + thread.isAlive() + ", keepWatching=" + keepWatching + ", suspended=" + suspended
				+ ", sleeping=" + sleeping + ", jobDoing=" + jobDoing + ", times=" + times + "]";
	}

	public void setJobInterrupttedHandler(JobInterrupttedHandler jobInterrupttedHandler) {
		this.jobInterrupttedHandler = jobInterrupttedHandler;
	}

	public static interface WatchJob {
		public void watch(long times, String name);
	}

	public static interface JobInterrupttedHandler {
		public void interrupt(InterruptedException exception, long times, String name);
	}

}
