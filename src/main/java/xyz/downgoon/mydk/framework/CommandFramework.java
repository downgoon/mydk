package xyz.downgoon.mydk.framework;

public interface CommandFramework {

	public void exec(Command command);
	
	public CommandFramework location(String location, CommandHandler handler, CommandFilter ... filters);
	
}
