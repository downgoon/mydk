package xyz.downgoon.mydk.framework.utiny;

import java.util.List;

import xyz.downgoon.mydk.framework.Command;
import xyz.downgoon.mydk.framework.CommandException;
import xyz.downgoon.mydk.framework.CommandFilter;
import xyz.downgoon.mydk.framework.CommandFilterChain;

public class NamedTraceStopableFilter implements CommandFilter {
	
	private String name;
	
	private boolean stop;
	
	private String prefixTrace;
	
	private String postfixTrace;
	
	private List<String> tracer;
	
	public NamedTraceStopableFilter(String name, boolean stop, List<String> tracer) {
		super();
		this.name = name;
		this.stop = stop;
		this.tracer = tracer;
	}

	@Override
	public void doFilter(Command command, CommandFilterChain chain) throws CommandException {
		prefixTrace = String.format("[%s] prefix on %s", name, command.path());
		System.out.println(prefixTrace);
		tracer.add(name + " prefix");
		if (! stop) {
			chain.doFilter(command);
			postfixTrace = String.format("[%s] postfix on %s", name, command.path());
			System.out.println(postfixTrace);
			tracer.add(name + " postfix");
		}
	}

	public String getName() {
		return name;
	}

	public boolean isStop() {
		return stop;
	}
	

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public String getPrefixTrace() {
		return prefixTrace;
	}

	public String getPostfixTrace() {
		return postfixTrace;
	}
	
}
