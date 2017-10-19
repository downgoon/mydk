package xyz.downgoon.mydk.framework.impl;

import java.util.ArrayList;
import java.util.List;

import xyz.downgoon.mydk.framework.Command;
import xyz.downgoon.mydk.framework.CommandContext;
import xyz.downgoon.mydk.framework.CommandException;
import xyz.downgoon.mydk.framework.CommandFilter;
import xyz.downgoon.mydk.framework.CommandFilterChain;
import xyz.downgoon.mydk.framework.CommandHandler;

public class AutonomousFilterChain implements CommandFilterChain {
	
	private List<CommandFilter> filters = new ArrayList<CommandFilter>();
	
	/* status indexing point to the next node in the chain to be executed */
	private int nextIndex = 0; 

	@Override
	public void doFilter(Command command, CommandContext context) throws CommandException {
		if (nextIndex < filters.size()) {
			CommandFilter filter = filters.get(nextIndex);
			nextIndex ++;
			filter.doFilter(command, context, this);
			
		} else {
			CommandHandler handler = CommandContext.getHandler();
			handler.exec(command, context);
		}
		
	}
	
	public void appendFilter(CommandFilter filter) {
		filters.add(filter);
	}
	
	public int size() {
		return filters.size();
	}
	
	public int nextIndex() {
		return nextIndex;
	}

	public boolean hasRemaining() {
		return nextIndex < filters.size();
	}

}
