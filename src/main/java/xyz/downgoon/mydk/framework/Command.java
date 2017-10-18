package xyz.downgoon.mydk.framework;

public class Command {

	private String path;
	
	private Object body;

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

	public Command(String path, Object body) {
		super();
		this.path = path;
		this.body = body;
	}

	public Command(String path) {
		super();
		this.path = path;
	}

	@Override
	public String toString() {
		return "Command [path=" + path + ", body=" + body + "]";
	}
	
}
