package xyz.downgoon.mydk.framework;

public interface CommandHandler {

	public void exec(Command command, CommandContext context) throws CommandException;

}
