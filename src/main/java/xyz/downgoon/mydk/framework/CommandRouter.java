package xyz.downgoon.mydk.framework;

import java.util.List;

public interface CommandRouter {

	public List<CommandHandler> dispatch(Command command);
	
}
