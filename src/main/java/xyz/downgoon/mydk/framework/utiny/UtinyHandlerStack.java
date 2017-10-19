package xyz.downgoon.mydk.framework.utiny;

import xyz.downgoon.mydk.framework.CommandFilterChain;
import xyz.downgoon.mydk.framework.CommandHandler;

class UtinyHandlerStack {
	
	private CommandFilterChain filterChain = null;
	
	private CommandHandler handler;

	public UtinyHandlerStack(CommandFilterChain filterChain, CommandHandler handler) {
		super();
		this.filterChain = filterChain;
		this.handler = handler;
	}

	/**
	 * if not config, may be null
	 * */
	public CommandFilterChain getFilterChain() {
		return filterChain;
	}

	/**
	 * if not config, may be null
	 * */
	public CommandHandler getHandler() {
		return handler;
	}
	
	
}
