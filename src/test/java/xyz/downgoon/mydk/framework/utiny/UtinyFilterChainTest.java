package xyz.downgoon.mydk.framework.utiny;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import xyz.downgoon.mydk.framework.CommandException;

public class UtinyFilterChainTest {

	protected List<String> tracer;

	protected NamedTraceStopableFilter logFilter;
	
	protected NamedTraceStopableFilter timerFilter;
	
	protected NamedTraceStopableFilter authFilter;
	
	protected NamedTraceStopableFilter secretFilter;
	
	protected UtinyFilterChain filterChain;
	
	@Before
	public void setUp() throws Exception {
		tracer = new ArrayList<>();
		logFilter = new NamedTraceStopableFilter("logFilter", false, tracer);
		timerFilter = new NamedTraceStopableFilter("timerFilter", false, tracer);
		authFilter = new NamedTraceStopableFilter("authFilter", true, tracer);
		secretFilter = new NamedTraceStopableFilter("secretFilter", false, tracer);
		
		filterChain = new UtinyFilterChain();
		filterChain.appendFilter(logFilter);
		filterChain.appendFilter(timerFilter);
		filterChain.appendFilter(authFilter);
		filterChain.appendFilter(secretFilter);
	}

	@After
	public void tearDown() throws Exception {
		tracer.clear();
	}

	
	@Test
	public void testChain() throws CommandException {
		UtinyCommand command = new UtinyCommand("/index.html");
		filterChain.doFilter(command);
		
		Assert.assertEquals(5, tracer.size());
		Assert.assertEquals(logFilter.getName() + " prefix", tracer.get(0));
		Assert.assertEquals(timerFilter.getName() + " prefix", tracer.get(1));
		Assert.assertEquals(authFilter.getName() + " prefix", tracer.get(2));
		Assert.assertEquals(timerFilter.getName() + " postfix", tracer.get(3));
		Assert.assertEquals(logFilter.getName() + " postfix", tracer.get(4));
		
	}

}
