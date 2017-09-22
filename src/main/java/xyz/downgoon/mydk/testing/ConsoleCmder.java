package xyz.downgoon.mydk.testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsoleCmder {

	private Map<String, Runnable> cmdHandlers = new HashMap<String, Runnable>();

	private Map<String, String> cmdComments = new HashMap<String, String>();

	private volatile ExecutorService executor = null;

	private boolean multiThread;

	public ConsoleCmder() {
		this(false);
	}

	public ConsoleCmder(boolean multiThread) {
		this.multiThread = multiThread;
	}

	public ConsoleCmder on(String cmd, String comment, Runnable handler) {
		String normCmd = cmd.trim().toLowerCase();
		cmdHandlers.put(normCmd, handler);
		if (comment != null) {
			cmdComments.put(normCmd, comment);
		}
		return this;
	}

	public ConsoleCmder on(String cmd, Runnable handler) {
		return on(cmd, null, handler);
	}

	public void start() throws IOException {
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter stdout = new BufferedWriter(new OutputStreamWriter(System.out));

		if (multiThread) {
			this.executor = Executors.newFixedThreadPool(1);
		}

		while (true) {
			prompt(stdout, usage(), false);
			String cmd = stdin.readLine();

			if ("quit".equalsIgnoreCase(cmd)) {
				break;
			}

			Runnable handler = cmdHandlers.get(cmd);
			if (handler == null) {
				prompt(stdout, "cmd not found");
				continue;
			}

			if (multiThread) {
				executor.submit(handler);
			} else {
				handler.run();
			}

		} // end while
		
		stop(); 
		if (stdin != null) {
			stdin.close();
		}
		if (stdout != null) {
			prompt(stdout, "console cmder quit");
			stdout.close();
		}
		
		
	}

	public void stop() {
		if (executor != null) {
			executor.shutdown();
			executor = null;
		}
	}

	private void prompt(BufferedWriter stdout, String line) throws IOException {
		prompt(stdout, line, true);
	}

	private void prompt(BufferedWriter stdout, String line, boolean newLine) throws IOException {
		stdout.write(line);
		if (newLine) {
			stdout.write("\r\n");
		}
		stdout.flush();
	}

	protected String usage() {
		StringBuilder sb = new StringBuilder();
		sb.append("cmd (");

		cmdHandlers.keySet().forEach(cmd -> {
			sb.append(cmd);
			String comment = cmdComments.get(cmd);
			if (comment != null) {
				sb.append(": ").append(comment);
			}
			sb.append(" | ");
		});

		sb.append("quit) >");
		return sb.toString();

	}

	public static void main(String[] args) throws IOException {
		ConsoleCmder cmder = new ConsoleCmder(false);
		cmder.on("start", () -> {
			System.out.println("exec start");
		}).on("stop", () -> {
			System.out.println("exec stop");
		}).start();

	}

}
