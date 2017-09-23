package xyz.downgoon.mydk.concurrent;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WatchdogTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPauseResume() throws Exception {

		AtomicLong watchCount = new AtomicLong(0);

		Watchdog.WatchJob watchJob = new Watchdog.WatchJob() {
			@Override
			public void watch(long times, String name) {
				watchCount.incrementAndGet();
			}
		};

		Watchdog watchdog = new Watchdog(watchJob, 10L).start();
		Thread.sleep(50L);
		System.out.println("watch count: " + watchCount.get());
		Assert.assertTrue(watchCount.get() > 1);
		watchdog.pause();
		long countAfterPause = watchCount.get();
		Thread.sleep(50L);
		Assert.assertEquals(countAfterPause, watchCount.get());
		watchdog.resume();
		Thread.sleep(50L);
		System.out.println("watch count after resume: " + watchCount.get());
		Assert.assertTrue(watchCount.get() > countAfterPause);
		Thread.sleep(50L);
		watchdog.stop();
		System.out.println("status after stop: " + watchdog.toString());
		
	};

}
