package xyz.downgoon.mydk.concurrent;

public interface ResourceLifecycle<T> {

	public T buildResource(String name) throws Exception;
	
	public void destoryResource(String name, T resource) throws Exception;
	
}
