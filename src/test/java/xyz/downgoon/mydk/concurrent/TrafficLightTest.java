package xyz.downgoon.mydk.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TrafficLightTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	protected TrafficLight createInstance() {
		return new SyncNotifyTrafficLight();
	}
	
	@Test
	public void testWaitGreen() throws InterruptedException {

		TrafficLight light = createInstance();
		Assert.assertFalse(light.isGreen());

		AtomicBoolean isBarrierPassed = new AtomicBoolean(false);

		CountDownLatch barrierStartedLatch = new CountDownLatch(1);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					barrierStartedLatch.countDown();
					light.waitGreen(); // blocking unit green light is on
					isBarrierPassed.set(true);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		barrierStartedLatch.await();
		Assert.assertFalse(isBarrierPassed.get());
		Thread.sleep(10L); // waitGreen() blocking
		Assert.assertFalse(isBarrierPassed.get());
		light.turnGreen();
		Assert.assertTrue(light.isGreen());
		Thread.sleep(1L); // wait isBarrierPassed.set(true) to be executed
		Assert.assertTrue(isBarrierPassed.get());

	}

}
