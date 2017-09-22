package xyz.downgoon.concurrent;

public interface ResourceLifecycle<T> {

	public T buildResource(String name) throws Exception;
	
	public void destoryResource(String name, T resource) throws Exception;
	
}
