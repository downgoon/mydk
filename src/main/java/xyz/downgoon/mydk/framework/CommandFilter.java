package xyz.downgoon.mydk.framework;

public interface CommandFilter {

	public void doFilter(Command command, CommandFilterChain chain) throws CommandException;
	
}
