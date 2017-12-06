package xyz.downgoon.mydk.process;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * execute external commands in javascript callback style
 */
public class Cmd {

	/* static style */
	public static void exec(String cmd, Options options) {
		ProcessFork pf = new ProcessFork();

		// default timeout is 10 min
		int timeoutSec = (options.timeoutSecond <= 0 ? 600 : options.timeoutSecond);
		ForkFuture future = null;
		int exitValue = 0;
		try {
			future = pf.fork(cmd);
			exitValue = future.awaitTerminated(timeoutSec, TimeUnit.SECONDS);

		} catch (ForkTimeoutException timeoutException) {
			if (options.timeoutCallback != null) {
				String timeoutMsg = pump(future, true, true);
				options.timeoutCallback.accept(timeoutMsg);
			}
			return; // onTimeout

		} catch (Exception e) {
			if (options.failCallback != null) {
				String errmsg = pump(future, true, true);
				options.failCallback.accept(null, errmsg);
				return; // onException
			}
		}

		if (exitValue == 0) {
			if (options.successCallback != null) {
				String okmsg = pump(future, true, false);
				options.successCallback.accept(okmsg);
			}
			return; // onSuccess
		}

		if (exitValue != 0) {
			if (options.failCallback != null) {
				String errmsg = pump(future, true, true);
				options.failCallback.accept(null, errmsg);
			}
			return; // onFail
		}

	}
	
	
	public static void exec(String cmd, Consumer<String> callback) throws Exception {
		AtomicReference<Exception> eref = new AtomicReference<>();
		exec(cmd, new Options().success(callback).fail((e, m) -> {
			eref.set(e);
		}));
		if (eref.get() != null) {
			throw eref.get();
		}
	}

	/**
	 * pump stdout and stderr message from sub-process
	 */
	protected static String pump(ForkFuture future, boolean extractStdout, boolean extractStderr) {
		StringBuffer outmsg = new StringBuffer();
		try {
			if (extractStdout && future.hasStdout()) {
				outmsg.append(future.readFullyStdout());
			}
			if (extractStderr && future.hasStderr()) {
				if (outmsg.length() > 0) {
					outmsg.append(System.lineSeparator());
				}
				outmsg.append(future.readLineStderr());
			}
			return outmsg.toString();

		} catch (InterruptedException ie) {
			throw new IllegalStateException(ie);
		}

	}

	/*
	 * another coding style
	 */

	private String cmd;

	private Options options;

	public Cmd(String cmd) {
		this.cmd = cmd;
		this.options = new Options();

	}

	public Cmd onSucc(Consumer<String> callback) {
		this.options.success(callback);
		return this;
	}

	public Cmd onFail(BiConsumer<Exception, String> callback) {
		this.options.fail(callback);
		return this;
	}

	public Cmd onTimeout(int timeoutSecond, Consumer<String> callback) {
		this.options.timeout(timeoutSecond, callback);
		return this;
	}

	public void exec() {
		// call static method
		Cmd.exec(this.cmd, this.options);
	}

	/* inner class */

	public static class Options {

		Consumer<String> successCallback;

		BiConsumer<Exception, String> failCallback;

		int timeoutSecond = 0;

		Consumer<String> timeoutCallback;

		public Options success(Consumer<String> callback) {
			this.successCallback = callback;
			return this;
		}

		public Options fail(BiConsumer<Exception, String> callback) {
			this.failCallback = callback;
			return this;
		}

		public Options timeout(int timeoutSecond, Consumer<String> callback) {
			this.timeoutSecond = timeoutSecond;
			this.timeoutCallback = callback;
			return this;
		}

	}

}
