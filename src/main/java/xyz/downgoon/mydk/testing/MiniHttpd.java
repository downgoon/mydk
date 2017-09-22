package xyz.downgoon.mydk.testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * a mini httpd for unit testing
 */
public class MiniHttpd {

	private Logger LOG = Logger.getLogger(MiniHttpd.class.getName());

	private int port;

	private long responseDelayMS;

	private String responseJson;

	private volatile ServerSocket server;

	private volatile boolean stopFlag = false;

	private Thread acceptThread;
	
	private volatile boolean debugMode = false;

	/**
	 * @param port
	 *            the port number, or {@code 0} to use a port number that is
	 *            automatically allocated.
	 * 
	 * @param responseDelayMS
	 *            sleeping milliseconds before response outputting to clients
	 * 
	 * @param responseJson
	 *            response message in json format
	 * 
	 */
	public MiniHttpd(int port, long responseDelayMS, String responseJson) {
		super();
		this.port = port;
		this.responseDelayMS = responseDelayMS;
		this.responseJson = responseJson;
	}

	public MiniHttpd(int port) {
		this(port, 0, "{\"id\":2,\"name\":\"downgoon\"," + "\"age\":25,\"credit\":8888}");
	}

	/**
	 * create a http server at random port which you can get by calling
	 * {@link #getListeningPort()} after {@link #start()}
	 */
	public MiniHttpd() {
		this(0, 0, "{\"id\":2,\"name\":\"downgoon\"," + "\"age\":25,\"credit\":8888}");
	}

	public MiniHttpd start() throws IOException {

		this.server = new ServerSocket(port);
		if (port != 0) {
			port = server.getLocalPort();
		}
		LOG.info("mini httpd listening on: " + port);
		this.acceptThread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (!stopFlag) {
					try {

						Socket socket = null;
						try {
							if (debugMode) {
								LOG.info("wait for a new connection ...");
							}
							
							socket = server.accept();
							if (debugMode) {
								LOG.info("got a connection");
							}
							
						} catch (IOException ioe) {
							LOG.info("mini httpd closed: accept broken");
							break;
						}

						BufferedReader reader = null;
						BufferedWriter writer = null;

						try {

							reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							String data = "";
							while ((data = reader.readLine()) != null) {
								if (debugMode) {
									LOG.info(data);
								}
								
								if ("".equalsIgnoreCase(data)) {
									break; // empty line
								}
							}

							writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

							// delay some time before response outputting
							if (responseDelayMS > 0) {
								Thread.sleep(responseDelayMS);
							}

							writer.write("HTTP/1.1 200 OK\r\n");
							writer.write("Server: mini-httpd\r\n");
							writer.write("Content-Type: application/json;charset=UTF-8\r\n");
							writer.write("Content-Length: " + responseJson.length() + "\r\n");
							writer.write("\r\n");

							writer.write(responseJson);

							writer.flush();

						} finally {
							if (debugMode) {
								LOG.info("Response Sent OK");
							}
							

							if (reader != null) {
								reader.close();
							}
							if (writer != null) {
								writer.close();
							}
						}

					} catch (Exception e) {
						// exception handler
						e.printStackTrace();
					}

				}
			}
		}, "mini-httpd");
		acceptThread.start();
		return this;
	}

	public int getListeningPort() {
		if (server == null || server.isClosed()) {
			throw new IllegalStateException("mini httpd server is not alive");
		}
		return server.getLocalPort();
	}

	public int port() {
		return port;
	}

	public void stop() throws IOException {
		stopFlag = true;
		if (server != null && !server.isClosed()) {
			server.close();
		}
	}
	

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public static void main(String[] args) throws Exception {
		MiniHttpd httpd = new MiniHttpd(8080, 0, "{\"id\":2,\"name\":\"downgoon\"," + "\"age\":25,\"credit\":8888}");
		httpd.start();
		Thread.sleep(1000L * 2);
		httpd.stop();
	}

}
