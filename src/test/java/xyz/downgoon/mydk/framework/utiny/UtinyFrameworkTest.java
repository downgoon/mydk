package xyz.downgoon.mydk.framework.utiny;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import xyz.downgoon.mydk.framework.CommandHandler;

public class UtinyFrameworkTest extends UtinyFilterChainTest {

	protected UtinyFramework framework;

	protected CommandHandler echoHandler;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		framework = new UtinyFramework();
		echoHandler = new EchoHandler(tracer);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testDoStackStopable() {
		UtinyCommand command = new UtinyCommand("/index.html");
		UtinyHandlerStack stack = new UtinyHandlerStack(filterChain, echoHandler);

		authFilter.setStop(true); // stop on auth

		framework.doStack(command, stack);

		Assert.assertEquals(5, tracer.size());
		Assert.assertEquals(logFilter.getName() + " prefix", tracer.get(0));
		Assert.assertEquals(timerFilter.getName() + " prefix", tracer.get(1));
		Assert.assertEquals(authFilter.getName() + " prefix", tracer.get(2));
		Assert.assertEquals(timerFilter.getName() + " postfix", tracer.get(3));
		Assert.assertEquals(logFilter.getName() + " postfix", tracer.get(4));

	}

	@Test
	public void testDoStackNonstop() {
		UtinyCommand command = new UtinyCommand("/index.html");
		UtinyHandlerStack stack = new UtinyHandlerStack(filterChain, echoHandler);

		authFilter.setStop(false); // no stop

		framework.doStack(command, stack);

		Assert.assertEquals(9, tracer.size());
		int i = 0;
		Assert.assertEquals(logFilter.getName() + " prefix", tracer.get(i++));
		Assert.assertEquals(timerFilter.getName() + " prefix", tracer.get(i++));
		Assert.assertEquals(authFilter.getName() + " prefix", tracer.get(i++));
		Assert.assertEquals(secretFilter.getName() + " prefix", tracer.get(i++));

		Assert.assertEquals("echo exec", tracer.get(i++));

		Assert.assertNotNull(((EchoHandler) echoHandler).getEcho());

		Assert.assertEquals(secretFilter.getName() + " postfix", tracer.get(i++));
		Assert.assertEquals(authFilter.getName() + " postfix", tracer.get(i++));
		Assert.assertEquals(timerFilter.getName() + " postfix", tracer.get(i++));
		Assert.assertEquals(logFilter.getName() + " postfix", tracer.get(i++));

	}
	
	@Test
	public void testNullFilter() {
		UtinyCommand command = new UtinyCommand("/index.html");
		UtinyHandlerStack stack = new UtinyHandlerStack(null, echoHandler);

		framework.doStack(command, stack);

		Assert.assertEquals(1, tracer.size());
		int i = 0;
		Assert.assertEquals("echo exec", tracer.get(i++));
		Assert.assertNotNull(((EchoHandler) echoHandler).getEcho());
	}
	
	@Test
	public void testNullHandler() {
		UtinyCommand command = new UtinyCommand("/index.html");
		UtinyHandlerStack stack = new UtinyHandlerStack(filterChain, null);

		authFilter.setStop(false); // no stop

		framework.doStack(command, stack);

		Assert.assertEquals(8, tracer.size());
		int i = 0;
		Assert.assertEquals(logFilter.getName() + " prefix", tracer.get(i++));
		Assert.assertEquals(timerFilter.getName() + " prefix", tracer.get(i++));
		Assert.assertEquals(authFilter.getName() + " prefix", tracer.get(i++));
		Assert.assertEquals(secretFilter.getName() + " prefix", tracer.get(i++));

		Assert.assertNull(((EchoHandler) echoHandler).getEcho());

		Assert.assertEquals(secretFilter.getName() + " postfix", tracer.get(i++));
		Assert.assertEquals(authFilter.getName() + " postfix", tracer.get(i++));
		Assert.assertEquals(timerFilter.getName() + " postfix", tracer.get(i++));
		Assert.assertEquals(logFilter.getName() + " postfix", tracer.get(i++));
	}
	
	@Test
	public void testNullBoth() {
		UtinyCommand command = new UtinyCommand("/index.html");
		UtinyHandlerStack stack = new UtinyHandlerStack(null, null);

		framework.doStack(command, stack);

		Assert.assertEquals(0, tracer.size());
		Assert.assertNull(((EchoHandler) echoHandler).getEcho());
	}

	@Test
	public void testDoMatch() {
		// match all
		Assert.assertFalse(framework.doMatch("", "/index.html"));
		Assert.assertFalse(framework.doMatch("/", "/index.html"));
		Assert.assertTrue(framework.doMatch("/*", "/index.html"));
		Assert.assertTrue(framework.doMatch("/**", "/index.html"));
		Assert.assertFalse(framework.doMatch("/*", "/users/index.html"));
		Assert.assertTrue(framework.doMatch("/**", "/users/index.html"));
		
		// match root
		Assert.assertTrue(framework.doMatch("/", "/"));
		Assert.assertFalse(framework.doMatch("/", ""));
		Assert.assertTrue(framework.doMatch("/?", "/"));

		// TODO
		
		Assert.assertTrue(framework.doMatch("/?", "/"));
		// Assert.assertTrue(framework.doMatch("/?", ""));
		
		Assert.assertTrue(framework.doMatch("\\/?", ""));
		// Assert.assertTrue(framework.doMatch("\\/?", "/"));
		
		
		// Don't forget last /
		Assert.assertTrue(framework.doMatch("/users", "/users"));
		Assert.assertFalse(framework.doMatch("/users", "/users/"));
		
		Assert.assertFalse(framework.doMatch("/users/", "/users"));
		Assert.assertTrue(framework.doMatch("/users/", "/users/"));
		
		Assert.assertTrue(framework.doMatch("/users/**", "/users"));
		Assert.assertTrue(framework.doMatch("/users/**", "/users/"));
		Assert.assertTrue(framework.doMatch("/users/**", "/users/index.html"));
		
		Assert.assertTrue(framework.doMatch("/users/?", "/users"));
		Assert.assertTrue(framework.doMatch("/users/?", "/users/"));
		Assert.assertFalse(framework.doMatch("/users/?", "/users/index.html"));
	}
	
	
	@Test
	public void testExecUntilHandler() {
		System.out.println("testExecUntilHandler ...");
		AtomicBoolean notFoundCalled = new AtomicBoolean(false);
		framework.setNotFoundHandler(cmd -> {
			notFoundCalled.set(true);
		});
		
		framework.location("/**",  (CommandHandler) null, logFilter);
		// framework.location("/fastapi/**", (CommandHandler) null, timerFilter);
		framework.location("/fastapi/**", timerFilter);
		framework.location("/fastapi/echo", echoHandler);
		
		UtinyCommand command = new UtinyCommand("/fastapi/echo");
		
		framework.exec(command);
		
		Assert.assertEquals(5, tracer.size());
		int i = 0;
		Assert.assertEquals(logFilter.getName() + " prefix", tracer.get(i++));
		Assert.assertEquals(timerFilter.getName() + " prefix", tracer.get(i++));

		Assert.assertEquals("echo exec", tracer.get(i++));
		Assert.assertNotNull(((EchoHandler) echoHandler).getEcho());

		Assert.assertEquals(timerFilter.getName() + " postfix", tracer.get(i++));
		Assert.assertEquals(logFilter.getName() + " postfix", tracer.get(i++));
		
		
		System.out.println("not found: " + notFoundCalled);
		Assert.assertFalse(notFoundCalled.get());
		System.out.println("testExecUntilHandler ... Fin");
	}
}
