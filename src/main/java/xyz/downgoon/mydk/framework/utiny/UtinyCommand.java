package xyz.downgoon.mydk.framework.utiny;

import xyz.downgoon.mydk.framework.Command;

public class UtinyCommand implements Command {

	private String path;

	private Object body;
	
	public UtinyCommand() {
		
	}
	
	public UtinyCommand(String path) {
		this.path = path;
	}

	public UtinyCommand(String path, Object body) {
		super();
		this.path = path;
		this.body = body;
	}

	@Override
	public String path() {
		return getPath();
	}

	@Override
	public Object body() {
		return getBody();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "PathCommand [path=" + path + ", body=" + body + "]";
	}

}
