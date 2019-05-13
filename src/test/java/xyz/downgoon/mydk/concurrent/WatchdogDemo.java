package xyz.downgoon.mydk.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

import xyz.downgoon.mydk.testing.ConsoleCmder;
import xyz.downgoon.mydk.testing.FileTailAppender;
import xyz.downgoon.mydk.testing.MiniHttpc;
import xyz.downgoon.mydk.testing.MiniHttpd;

public class WatchdogDemo {

	public static void main(String[] args) throws Exception {

		MiniHttpd httpServer = new MiniHttpd();
		final int port = httpServer.start().getListeningPort();
		FileTailAppender httpLogger = FileTailAppender.tempFile();

		System.out.println("tailLight -100f " + httpLogger.getFileName());

		Watchdog.WatchJob watchJob = new Watchdog.WatchJob() {

			private AtomicInteger requestCount = new AtomicInteger(0);

			@Override
			public void watch(long times, String name) {

				MiniHttpc httpClient = new MiniHttpc(port);

				httpClient.get("/", new MiniHttpc.ResponseHandler() {

					@Override
					public void onHeadLine(String head, int num) {
						httpLogger.appendLine("<<< " + head);
					}

					@Override
					public void onContentLength(int contentLength) {

					}

					@Override
					public void onBodyTrunk(String trunk, int num) {
						httpLogger.appendLine("<<< " + trunk);
					}

					@Override
					public void onException(Exception e) {
						httpLogger.appendLine("!!! exception occured: ");
						httpLogger.appendLine(e.getMessage());
					}

					@Override
					public void onBodyEnd() {
						httpLogger.appendLine("");
						httpLogger.appendLine("");
					}

				}, new MiniHttpc.RequestTracer() {

					@Override
					public void requestFlushed(String requestPath) {
						httpLogger.appendLine(">>> request#" + requestCount.incrementAndGet());
						httpLogger.appendLine(">>> GET " + requestPath);
					}
				});

			} // end watch

		};

		// start watchdog
		Watchdog watchdog = new Watchdog(watchJob, 1000L).start();

		// control watchdog's action from console
		new ConsoleCmder().on("s", "stop", () -> {
			watchdog.stop();
		}).on("p", "pause", () -> {
			watchdog.pause();
		}).on("r", "resume", () -> {
			watchdog.resume();
		}).on("v", "view", () -> {
			System.out.println(watchdog.toString());
		}).start();

		// clean resources
		if (watchdog.isAlive()) {
			watchdog.stop();
		}

		httpLogger.closeAndDelete();
		httpServer.stop();

	}

}
