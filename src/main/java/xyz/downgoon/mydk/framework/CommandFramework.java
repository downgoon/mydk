package xyz.downgoon.mydk.framework;

import java.util.List;

public interface CommandFramework {

	public void exec(Command command);
	
	public CommandFramework location(String location, CommandHandler handler, CommandFilter ... filters);
	
	default CommandFramework location(String location, CommandFilter[] filters, CommandHandler handler) {
		return location(location, handler, filters);
	}
	
	default CommandFramework location(String location, List<CommandFilter> filters, CommandHandler handler) {
		CommandFilter[] filterArray = new CommandFilter[filters.size()]; 
		return location(location, handler, filters.toArray(filterArray));
	}
	
	default CommandFramework location(String location, CommandFilter ... filters) {
		return location(location, null, filters);
	}
	
	default CommandFramework location(CommandFilter ... filters) {
		return location("/**", null, filters);
	}
	
	default CommandFramework location(CommandHandler handler) {
		return location("/**", handler);
	}
	
}
