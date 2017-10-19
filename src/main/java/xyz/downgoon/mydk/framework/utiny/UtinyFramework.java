package xyz.downgoon.mydk.framework.utiny;

import java.util.LinkedHashMap;
import java.util.Map;

import xyz.downgoon.mydk.framework.Command;
import xyz.downgoon.mydk.framework.CommandException;
import xyz.downgoon.mydk.framework.CommandExceptionHandler;
import xyz.downgoon.mydk.framework.CommandFilter;
import xyz.downgoon.mydk.framework.CommandFilterChain;
import xyz.downgoon.mydk.framework.CommandFramework;
import xyz.downgoon.mydk.framework.CommandHandler;
import xyz.downgoon.mydk.framework.CommandNotFoundHandler;
import xyz.downgoon.mydk.util.AntPathMatcher;

public class UtinyFramework implements CommandFramework {

	// private CommandRouter router;

	private CommandNotFoundHandler notFoundHandler = new Utiny404Handler();

	private CommandExceptionHandler exceptionHandler = new Utiny500Handler();

	/**
	 * order matters
	 */
	protected Map<String, UtinyHandlerStack> pathMapping = new LinkedHashMap<>();

	@Override
	public void exec(Command command) {
		// List<CommandHandler> handlers = router.dispatch(command);

		// First Win Match
		boolean handlerMatched = false;
		UtinyFilterChain filtersMerged = null; // until meet handler
		
		for (Map.Entry<String, UtinyHandlerStack> e :  pathMapping.entrySet()) {
			String pathPattern = e.getKey();
			UtinyHandlerStack handlerStack = e.getValue();
			
			if (!handlerMatched && doMatch(pathPattern, command.path())) {

				if (handlerStack.getHandler() != null) {
					handlerMatched = true;
					if (filtersMerged == null) {
						doStack(command, handlerStack);
					} else {
						doStack(command, new UtinyHandlerStack(filtersMerged, handlerStack.getHandler()));
					}

				} else {
					if (filtersMerged == null) {
						filtersMerged = new UtinyFilterChain();
					}
					filtersMerged.appendFilterChain((UtinyFilterChain) handlerStack.getFilterChain());
				}

			}
		}
		
		if (!handlerMatched) {
			notFoundHandler.notFound(command);
		}
	}

	protected AntPathMatcher antPathMatcher = new AntPathMatcher();

	/**
	 * match the given <code>path</code> against the given <code>pattern</code>.
	 */
	protected boolean doMatch(String pattern, String path) {
		// return antPathMatcher.match(pattern, path);
		return antPathMatcher.matchStart(pattern, path);
	}

	/**
	 * execute one command in one stack consist of multiple heading filters and
	 * one tailing handler
	 * 
	 * @param command
	 *            command to be executed
	 * @param stack
	 *            tuple of several filters and one handler
	 */
	protected void doStack(Command command, UtinyHandlerStack stack) {
		CommandFilterChain filterChain = stack.getFilterChain();
		CommandHandler handler = stack.getHandler();

		UtinyContext.clear();
		try {
			if (filterChain != null) {
				UtinyContext.setHandler(handler);
				filterChain.doFilter(command);
			} else {
				if (handler != null) {
					handler.exec(command);
				}
			}

		} catch (CommandException ce) {
			exceptionHandler.onException(command, ce);

		} finally {
			UtinyContext.clear();
		}
	}

	@Override
	public CommandFramework path(String path, CommandHandler handler, CommandFilter... filters) {
		UtinyFilterChain filterChain = null;
		if (filters != null && filters.length > 0) {
			filterChain = new UtinyFilterChain();
			for (CommandFilter filter : filters) {
				filterChain.appendFilter(filter);
			}
		}
		pathMapping.put(path, new UtinyHandlerStack(filterChain, handler));
		return this;
	}

	public CommandNotFoundHandler getNotFoundHandler() {
		return notFoundHandler;
	}

	public void setNotFoundHandler(CommandNotFoundHandler notFoundHandler) {
		this.notFoundHandler = notFoundHandler;
	}

	public CommandExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(CommandExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

}
