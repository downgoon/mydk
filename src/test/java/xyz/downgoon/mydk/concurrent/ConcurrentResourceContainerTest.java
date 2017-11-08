package xyz.downgoon.mydk.concurrent;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConcurrentResourceContainerTest {

	private static class ConnectionResource {

		private String threadName;

		public ConnectionResource(String threadName) {
			this.threadName = threadName;
		}

		@Override
		public String toString() {
			return threadName;
		}

		public void close() {
			// do nothing
		}
		
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	final Thread[] threads = new Thread[3];
	final CountDownLatch finishLatch = new CountDownLatch(3);
	
	// volatile ConnectionResource[] cr = new ConnectionResource[3];
	@SuppressWarnings("unchecked")
	AtomicReference<ConnectionResource>[] cr = new AtomicReference[3];

	final Exception[] exceptions = new Exception[3];
	

	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < cr.length; i++) {
			cr[i] = new AtomicReference<>();
		}
	}

	@Test
	public void testBuild() throws Exception {

		final AtomicInteger buildCountBeforeSleep = new AtomicInteger(0);
		final AtomicInteger buildCountAfterSleep = new AtomicInteger(0);
		final CopyOnWriteArrayList<String> resourceNames = new CopyOnWriteArrayList<>();
		final Random random = new Random();

		final ConcurrentResourceContainer<ConnectionResource> container = new ConcurrentResourceContainer<>(

				new ResourceLifecycle<ConnectionResource>() {

					@Override
					public ConnectionResource buildResource(String name) throws Exception {
						buildCountBeforeSleep.incrementAndGet();
						resourceNames.add(name);
						ConnectionResource resource = new ConnectionResource(Thread.currentThread().getName());
						Thread.sleep(random.nextInt(700)); // assume creating a TCP connection costs 10 ms
						buildCountAfterSleep.incrementAndGet();
						return resource;
					}

					@Override
					public void destoryResource(String name, ConnectionResource resource) throws Exception {
						resource.close(); // close the TCP connection
					}

				}

		);

		threads[0] = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// cr[0] = container.addResource("master");
					cr[0].set(container.addResource("master"));
				} catch (Exception e) {
					exceptions[0] = e;
				} finally {
					finishLatch.countDown();
				}
			}
		}, "t-master-0");

		threads[1] = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// cr[1] = container.addResource("master");
					cr[1].set(container.addResource("master"));
				} catch (Exception e) {
					exceptions[1] = e;
				} finally {
					finishLatch.countDown();
				}
			}
		}, "t-master-1");

		threads[2] = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// cr[2] = container.addResource("slave");
					cr[2].set(container.addResource("slave"));
				} catch (Exception e) {
					exceptions[2] = e;
				} finally {
					finishLatch.countDown();
				}
			}
		}, "t-slave-2");

		for (int i = 0; i < 3; i++) {
			threads[i].start();
		}

		CountDownLatch additionalLatch = new CountDownLatch(3);
		for (int i = 0; i < 3; i++) {
			new Thread(() -> {
				try {
					container.addResource("master");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					additionalLatch.countDown();
				}
			}, "additional-master-" + i).start();
		}
		
		finishLatch.await(); // wait the 3 threads ending
		additionalLatch.await();

		System.out.println("resource names: " + resourceNames);
		// only create 'master' and 'slave' objects
		Assert.assertEquals(2, buildCountBeforeSleep.get()); // NOT 3
		Assert.assertEquals(2, buildCountAfterSleep.get()); // NOT 3
		
		
		/*
		 * cr[i] modified in other threads, its new value may not be seen in
		 * main thread, if not 'volatile'
		 */

		Assert.assertTrue(cr[0].get() == cr[1].get());
		Assert.assertFalse(cr[0].get() == cr[2].get());

		for (int i = 0; i < 3; i++) {
			Assert.assertNull(exceptions[i]);
		}

		boolean inMaster = "t-master-0".equals(cr[0].get().toString()) || "t-master-1".equals(cr[0].get().toString());

		Assert.assertTrue(inMaster);
		Assert.assertEquals(cr[0].get().toString(), cr[1].get().toString());
		Assert.assertEquals("t-slave-2", cr[2].get().toString());
	}

}
