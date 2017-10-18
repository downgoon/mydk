package xyz.downgoon.mydk.framework;

public interface CommandFilter {

	public void doFilter(Command command, CommandContext context, CommandFilterChain chain) throws CommandException;
	
}
