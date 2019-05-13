package xyz.downgoon.mydk.example;

import java.util.ArrayList;
import java.util.List;

import xyz.downgoon.mydk.framework.Command;
import xyz.downgoon.mydk.framework.CommandException;
import xyz.downgoon.mydk.framework.CommandHandler;
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
		framework.location("/**", (CommandHandler) null, logFilter);
		framework.location("/fastapi/**", timerFilter);
		framework.location("/fastapi/echo", new EchoHandler(tracer));

		// RESTful style: Resource&Action - EntPoint
		UserHandler user = new UserHandler();
		framework.location("/user/**", (cmd, chain) -> {
			System.out.println("-> /user RESTful api ..." + cmd.path());
			chain.doFilter(cmd);
			System.out.println("<- /user RESTful api ... Fin");
		});
		framework.location("/user/create", user::create);
		framework.location("/user/view", user::view); // Java8 Function Pointer
		framework.location("/user/remove", new CommandHandler() {
			
			@Override
			public void exec(Command command) throws CommandException {
				user.remove(command); // adaptor pattern beforeLight Java8
			}
		});

		// handle command
		UtinyCommand command = new UtinyCommand("/fastapi/echo");
		framework.exec(command);

		framework.exec(new UtinyCommand("/user/create"));
		framework.exec(new UtinyCommand("/user/view"));
		framework.exec(new UtinyCommand("/user/remove"));

	}

	static class UserHandler {

		public void create(Command command) throws CommandException {
			System.out.println("user create action");
		}

		public void view(Command command) throws CommandException {
			System.out.println("user view action");
		}

		public void remove(Command command) throws CommandException {
			System.out.println("user remove action");
		}

	}

}
