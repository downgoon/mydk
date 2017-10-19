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
	protected Map<String, UtinyHandlerStack> locationHandlerMapping = new LinkedHashMap<>();

	@Override
	public void exec(Command command) {
		// List<CommandHandler> handlers = router.dispatch(command);

		// First Win Match
		boolean handlerMatched = false;
		UtinyFilterChain filtersMerged = null; // until meet handler
		
		for (Map.Entry<String, UtinyHandlerStack> e :  locationHandlerMapping.entrySet()) {
			String locationPattern = e.getKey();
			UtinyHandlerStack locationStack = e.getValue();
			
			if (!handlerMatched && doMatch(locationPattern, command.path())) {

				if (locationStack.getHandler() != null) {
					handlerMatched = true;
					if (filtersMerged == null) {
						doStack(command, locationStack);
					} else {
						doStack(command, new UtinyHandlerStack(filtersMerged, locationStack.getHandler()));
					}

				} else {
					if (filtersMerged == null) {
						filtersMerged = new UtinyFilterChain();
					}
					filtersMerged.appendFilterChain((UtinyFilterChain) locationStack.getFilterChain());
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
	public CommandFramework location(String location, CommandHandler handler, CommandFilter... filters) {
		UtinyFilterChain filterChain = null;
		if (filters != null && filters.length > 0) {
			filterChain = new UtinyFilterChain();
			for (CommandFilter filter : filters) {
				filterChain.appendFilter(filter);
			}
		}
		locationHandlerMapping.put(location, new UtinyHandlerStack(filterChain, handler));
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
