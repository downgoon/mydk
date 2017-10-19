package xyz.downgoon.mydk.example;

import java.util.ArrayList;
import java.util.List;

import xyz.downgoon.mydk.framework.utiny.EchoHandler;
import xyz.downgoon.mydk.framework.utiny.NamedTraceStopableFilter;
import xyz.downgoon.mydk.framework.utiny.UtinyCommand;
import xyz.downgoon.mydk.framework.utiny.UtinyFramework;

public class UtinyFrameworkExample {

	public static void main(String[] args) {
		UtinyFramework framework = new UtinyFramework();
		
		// filters
		List<String> tracer = new ArrayList<>();
		NamedTraceStopableFilter logFilter = new NamedTraceStopableFilter("logFilter", false, tracer);
		NamedTraceStopableFilter timerFilter = new NamedTraceStopableFilter("timerFilter", false, tracer);

		// path matching
		framework.path("/**", null, logFilter);
		framework.path("/fastapi/**", null, timerFilter);
		framework.path("/fastapi/echo", new EchoHandler(tracer));
		
		
		// handle command
		UtinyCommand command = new UtinyCommand("/fastapi/echo");
		
		framework.exec(command);
		
	}

}
