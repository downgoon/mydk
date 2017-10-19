package xyz.downgoon.mydk.framework.utiny;

import java.util.ArrayList;
import java.util.List;

import xyz.downgoon.mydk.framework.Command;
import xyz.downgoon.mydk.framework.CommandException;
import xyz.downgoon.mydk.framework.CommandFilter;
import xyz.downgoon.mydk.framework.CommandFilterChain;
import xyz.downgoon.mydk.framework.CommandHandler;

/**
 * thread-safe
 */
public class UtinyFilterChain implements CommandFilterChain {

	private List<CommandFilter> filters = new ArrayList<CommandFilter>();

	@Override
	public void doFilter(Command command) throws CommandException {
		int nextIndex = nextIndex();
		if (nextIndex < filters.size()) {
			CommandFilter filter = filters.get(nextIndex);
			moveIndex();

			/*
			 * recursive call may occur in different 'nextIndex', if
			 * 'chain.doFilter(command)' called in the filter implementation
			 */
			filter.doFilter(command, this);

		} else {
			CommandHandler handler = UtinyContext.getHandler();
			if (handler != null) {
				handler.exec(command);
			}
			
		}

	}

	protected void appendFilter(CommandFilter filter) {
		filters.add(filter);
	}
	
	protected void appendFilterChain(UtinyFilterChain chain) {
		this.filters.addAll(chain.filters);
	}

	protected int size() {
		return filters.size();
	}

	private static final String KEY_FILTER_INDEX = UtinyFilterChain.class.getName() + "_" + "FILTER_INDEX";

	/**
	 * status indexing point to the next node in the chain to be executed
	 * 
	 * @return next node index
	 */
	protected int nextIndex() {
		/*
		 * in order to ensure 'thread-safe' accessing, 'object status variables'
		 * are stored in 'ThreadLocal' rather than in 'object attributes'
		 */
		Integer idx = (Integer) UtinyContext.get(KEY_FILTER_INDEX);
		if (idx == null) {
			UtinyContext.put(KEY_FILTER_INDEX, 0);
			return 0;
		}
		return idx;
	}

	/**
	 * increase indexing point and then return current value
	 * 
	 * @return next node index after moving one step
	 */
	protected int moveIndex() {
		int idx = nextIndex();
		UtinyContext.put(KEY_FILTER_INDEX, idx + 1);
		return idx + 1;
	}

	boolean hasRemaining() {
		return nextIndex() < filters.size();
	}

}
