package xyz.downgoon.mydk.framework.utiny;

import java.util.List;

import xyz.downgoon.mydk.framework.Command;
import xyz.downgoon.mydk.framework.CommandException;
import xyz.downgoon.mydk.framework.CommandHandler;

public class EchoHandler implements CommandHandler {

	private String echo = null;
	
	private List<String> tracer;
	
	public EchoHandler(List<String> tracer) {
		super();
		this.tracer = tracer;
	}

	@Override
	public void exec(Command command) throws CommandException {
		System.out.println("exec " + command.path() + " in echo handler");
		echo = command.path();
		tracer.add("echo exec");
	}

	public String getEcho() {
		return echo;
	}
}
