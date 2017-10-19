package xyz.downgoon.mydk.framework;

public interface CommandExceptionHandler {

	public void onException(Command command, Exception exception);
}
