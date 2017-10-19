package xyz.downgoon.mydk.framework.utiny;

import xyz.downgoon.mydk.concurrent.ThreadContext;
import xyz.downgoon.mydk.framework.CommandContext;
import xyz.downgoon.mydk.framework.CommandHandler;

public class UtinyContext extends ThreadContext implements CommandContext {

	private static final String KEY_HANDLER = CommandContext.class.getName() + "_" + "HANDLER";
	
	public static CommandHandler getHandler() {
		return (CommandHandler) get(KEY_HANDLER);
	}
	
	static void setHandler(CommandHandler handler) {
		put(KEY_HANDLER, handler);
	}

	
}
