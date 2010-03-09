package es.uvigo.darwin.prottest.exe;

import java.util.ArrayList;
import java.util.Collection;

public class ExternalExecutionManager {

	private static ExternalExecutionManager instance;
	
	private final Collection<Process> processes;
	
	private ExternalExecutionManager() {
		this.processes = new ArrayList<Process>();
	}
	
	public static ExternalExecutionManager getInstance() {
		if (instance == null)
			instance = new ExternalExecutionManager();
		return instance;
	}
	
	public boolean addProcess(Process proc) {
		boolean result = false;
		if (!processes.contains(proc)) {
			result = processes.add(proc);
		}
		return result;
	}
	
	public boolean removeProcess(Process proc) {
		boolean result = false;
		if (processes.contains(proc)) {
			result = processes.remove(proc);
		}
		return result;
	}
	
	public void killProcesses() {
		for (final Process proc : processes) {
			if (proc != null) {
				try {
					proc.exitValue();
				} catch (IllegalThreadStateException e) {
					// The process is executing, so we should kill it
					Runtime.getRuntime().addShutdownHook(
						new Thread(new Runnable() {
				            public void run() {
				            	proc.destroy();
				            	}
						}));
				}
			}
		}
		processes.clear();
	}
	
}
