package xyz.downgoon.mydk.framework;

public interface CommandFilterChain {

	public void doFilter(Command command) throws CommandException;
	
}
