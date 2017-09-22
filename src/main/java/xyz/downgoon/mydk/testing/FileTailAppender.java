package xyz.downgoon.mydk.testing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;

/**
 * append log entity line by line into log file
 */
public class FileTailAppender {

	private File file;

	private BufferedWriter fileWriter = null;

	public FileTailAppender(String fileName) {
		this(new File(fileName));
	}

	private FileTailAppender(File file) {
		this.file = file;
		open0();
	}

	public static FileTailAppender tempFile() {
		File file = null;
		try {
			file = Files.createTempFile("downgoon", ".log").toFile();
			return new FileTailAppender(file);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

	}

	public void open() {
		open0();
	}
	
	private void open0() {
		try {
			fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		} catch (FileNotFoundException e) {
			new IllegalStateException(e);
		}
	}

	public void appendLine(String line) {
		try {
			fileWriter.write(line);
			fileWriter.write("\r\n");
			fileWriter.flush();
		} catch (IOException ioe) {
			new IllegalStateException(ioe);
		}
	}

	public void close() {
		try {
			if (fileWriter != null) {
				fileWriter.close();
			}

		} catch (IOException ioe) {
			new IllegalStateException(ioe);
		}

	}
	
	public boolean closeAndDelete() {
		close();
		return file.delete();
	}

	public String getFileName() {
		return file.getAbsolutePath();
	}

}
