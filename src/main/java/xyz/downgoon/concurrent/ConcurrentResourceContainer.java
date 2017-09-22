package xyz.downgoon.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author downgoon
 * @since 2011-04-22
 */
public class ConcurrentResourceContainer<T> {

	private final Map<String, T> resourceContainer = new HashMap<String, T>();

	private final ConcurrentHashMap<String, ReentrantLock> stuntmanContainer = new ConcurrentHashMap<String, ReentrantLock>();

	private ResourceLifecycle<T> lifecycle;

	public ConcurrentResourceContainer(ResourceLifecycle<T> lifecycle) {
		this.lifecycle = lifecycle;
	}

	/**
	 * get or create named resource
	 * 
	 * @param name
	 *            the name of resource
	 * 
	 * @return newly created or cached resource
	 * 
	 * @throws Exception
	 *             thrown when resource building failed. on how to build
	 *             resource, please refer to
	 *             {@link ResourceLifecycle#buildResource(String)}
	 * 
	 */
	public T getResource(String name) throws Exception {
		T resource = resourceContainer.get(name);
		if (resource == null) {
			ReentrantLock stuntman = getStuntmanInstanceByName(name);
			try {
				stuntman.lock();
				resource = resourceContainer.get(name);
				if (resource == null) {
					resource = lifecycle.buildResource(name); // build exception
					if (resource != null) {
						resourceContainer.put(name, resource);
					}
				}

			} finally {
				stuntman.unlock();
			}

		}
		return resource;
	}

	public T addResource(String name) throws Exception {
		return getResource(name);
	}

	public T removeResource(String name) throws Exception {
		T resource = resourceContainer.get(name);
		if (resource == null) {
			return null;
		}

		ReentrantLock stuntman = getStuntmanInstanceByName(name);
		try {
			stuntman.lock();

			lifecycle.destoryResource(name, resource); // destroy exception
			resourceContainer.remove(name);
			stuntmanContainer.remove(name);

		} finally {
			stuntman.unlock();
		}

		return resource;
	}

	private ReentrantLock getStuntmanInstanceByName(String name) {
		ReentrantLock stuntman = stuntmanContainer.get(name);
		if (stuntman == null) {
			ReentrantLock newStuntman = new ReentrantLock();
			ReentrantLock preStuntman = stuntmanContainer.putIfAbsent(name, newStuntman);
			stuntman = (preStuntman != null ? preStuntman : newStuntman);
		}
		return stuntman;
	}

	public int size() {
		return stuntmanContainer.size();
	}

	public Map<String, T> container() {
		return resourceContainer;
	}

	public boolean containsName(String name) {
		return stuntmanContainer.containsKey(name);
	}

}
