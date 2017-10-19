package xyz.downgoon.mydk.concurrent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ThreadContextTest {

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
	public void testThreadLocal() throws Exception {
		ThreadLocal<String> localRequest = new ThreadLocal<>();
		localRequest.set("/local?code=1234");
		
		InheritableThreadLocal<String> inheritableRequest = new InheritableThreadLocal<>();
		inheritableRequest.set("/inheritab?code=5678");

		
		System.out.println("local request in main: " + localRequest.get());
		System.out.println("inheritable request in main: " + inheritableRequest.get());

		Thread sub = new Thread(() -> {
			System.out.println("local request in sub thread: " + localRequest.get());
			System.out.println("inheritable request in sub thread: " + inheritableRequest.get());
			
			inheritableRequest.set(inheritableRequest.get() + "&memo=modify-in-sub");
			System.out.println("inheritable request in sub thread: " + inheritableRequest.get());
			
		});
		sub.start();
		
		sub.join();
		System.out.println("inheritable request again in main: " + inheritableRequest.get());
	}

	
	@Test
	public void testInheritable() throws Exception {
		ThreadContext.put("code", "parent-123");
		ThreadContext.put("another", 123456L);

		System.out.println("get in parent thread: " + ThreadContext.get("code"));
		Assert.assertEquals("parent-123", ThreadContext.get("code"));

		Thread subThread = new Thread(() -> {
			System.out.println("get in child thread: " + ThreadContext.get("code"));
			Assert.assertEquals("parent-123", ThreadContext.get("code"));
			
			ThreadContext.put("code", "child-456");
			System.out.println("get in child thread after modify: " + ThreadContext.get("code"));
			Assert.assertEquals("child-456", ThreadContext.get("code"));
			
		});
		subThread.start();
		
		subThread.join();
		
		System.out.println("get in parent thread after modify only in child: " + ThreadContext.get("code"));
		Assert.assertEquals("parent-123", ThreadContext.get("code"));
		
		ThreadContext.remove("code");
		Assert.assertNull(ThreadContext.get("code"));
		Assert.assertNotNull(ThreadContext.get("another"));
		
		ThreadContext.clear();
		Assert.assertNull(ThreadContext.get("another"));
		
	}
}
