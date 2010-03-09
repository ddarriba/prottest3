package es.uvigo.darwin.prottest.facade.thread;

import java.util.Hashtable;

import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

public class ThreadPoolSynchronizer {
	
	private Hashtable<Long, Integer> threadIds;
	
	private int nextValue;
	
	private int poolSize;
	
	private static ThreadPoolSynchronizer instance;
	
	private ThreadPoolSynchronizer(int poolSize) {
		this.poolSize = poolSize;
		threadIds = new Hashtable<Long, Integer>(poolSize);
		nextValue = 0;
	}
	
	public synchronized int getThreadId(Thread thread) {
		long threadNumber = thread.getId();
		Integer value = threadIds.get(threadNumber);
		
		if (value == null) {
			if (nextValue >= poolSize) {
				throw new ProtTestInternalException("Thread out of sync on ThreadPool");
			}
			// next value
			value = nextValue++;
			threadIds.put(threadNumber, value);
		}
		
		return value; 
	}
  
	public static void synchronize(int poolSize) {
		instance = new ThreadPoolSynchronizer(poolSize);
	}
	
	public synchronized static ThreadPoolSynchronizer getInstance() {
		if (instance == null)
			throw new ProtTestInternalException("ThreadPool out of sync");
		return instance;
	}
}