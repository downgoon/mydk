package xyz.downgoon.mydk.testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class MiniHttpc {

	private String host;

	private int port;

	public MiniHttpc(int port) {
		this("127.0.0.1", port);
	}
	
	public MiniHttpc(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public void get(String path, ResponseHandler responseHandler) {
		get(path, responseHandler, null);
	}

	public void get(String path, ResponseHandler responseHandler, RequestTracer requestTracer) {
		Socket sock = null;
		try {
			sock = new Socket(host, port);
			BufferedWriter sockWriter = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			BufferedReader sockReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			sockWriter.write("GET " + path + " HTTP/1.1\r\n");
			sockWriter.write("Host: " + host + "\r\n");
			sockWriter.write("\r\n");
			sockWriter.write("\r\n");
			sockWriter.flush();

			if (requestTracer != null) {
				requestTracer.requestFlushed(path);
			}

			String resline = sockReader.readLine();

			// parse headLight
			int headLineNum = 0;
			int contentLength = 0;
			while (!resline.equals("")) { // empty line

				responseHandler.onHeadLine(resline, headLineNum);

				if (resline.startsWith("Content-Length")) {
					// sample: "Content-Length: 101"
					contentLength = Integer.parseInt((resline.split(":")[1].trim()));
					responseHandler.onContentLength(contentLength);
				}

				headLineNum++;
				resline = sockReader.readLine();

			}

			// parse body
			int contentReadedSize = 0;
			char[] cbuf = new char[128];
			int trunkSize = 0;
			int trunkNum = 0;
			while (contentReadedSize < contentLength && (trunkSize = sockReader.read(cbuf)) != -1) {
				responseHandler.onBodyTrunk(new String(cbuf, 0, trunkSize), trunkNum);
				contentReadedSize += trunkSize;
				trunkNum++;
			}
			
			responseHandler.onBodyEnd();
			

		} catch (Exception e) {
			responseHandler.onException(e);

		} finally {
			if (sock != null) {
				try {
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static interface ResponseHandler {

		public void onHeadLine(String head, int num);

		public void onContentLength(int contentLength);

		public void onBodyTrunk(String trunk, int num);

		public void onException(Exception e);
		
		public void onBodyEnd();

	}

	public static interface RequestTracer {
		public void requestFlushed(String requestPath);
	}

	public static void main(String[] args) throws Exception {
		MiniHttpd httpd = new MiniHttpd();
		int port = httpd.start().getListeningPort();
		
		

		MiniHttpc httpc = new MiniHttpc("127.0.0.1", port);
		httpc.get("/", new ResponseHandler() {
			
			private final Logger LOG = Logger.getLogger("httpc");

			@Override
			public void onHeadLine(String head, int num) {
				LOG.info(head);
			}

			@Override
			public void onContentLength(int contentLength) {
				
			}

			@Override
			public void onBodyTrunk(String trunk, int num) {
				LOG.info(trunk);
			}

			@Override
			public void onException(Exception e) {
				e.printStackTrace();
			}
			
			@Override
			public void onBodyEnd() {
				try {
					httpd.stop();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});

	}

}
