package com.paul;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IJobParalell implements IJob {

	
	private List<IJob> iJobs = null;
	private ArrayList<IJobThread> threads = new ArrayList<IJobThread>();
	
	private int maxThreads = 1;
	private int newJobStartWaitTime = 30000;
	
	private boolean isHalting = false;
	
	
	public IJobParalell () {}
	
	public IJobParalell (List<IJob> iJobs) {
		setIJobs(iJobs);
	}
	
	public IJobParalell (List<IJob> iJobs, int maxThreads, int newJobStartWaitTime) {
		setIJobs(iJobs);
		setMaxThreads(maxThreads);
		setNewJobStartWaitTime(newJobStartWaitTime);
	}
	
	
	public void execute() throws Exception {
		
		if (iJobs == null) {
			throw new IJobException("No jobs given to run.  Assign a list of jobs first with the setIJobs(List<IJob> iJobs) first.");
		}
		
		try {
			Iterator<IJob> it = iJobs.iterator();
			while (it.hasNext() && !Thread.currentThread().isInterrupted() && !isHalting) {
				
				if (threads.size() < maxThreads) {
					IJobThread newThread = new IJobThread(it.next());
					threads.add(newThread);
					newThread.start();
				} else {
					removeDeadThreads();					
					if (threads.size() == maxThreads)
						Thread.sleep(newJobStartWaitTime);
				}
					
			}
			
			while ((threads.size() > 0) && !Thread.currentThread().isInterrupted()) {
				Thread.yield();
				if (Thread.currentThread().isInterrupted()) {
					haltAllJobs();
					throw new InterruptedException("Stopped by ifInterruptedStop()");
				}
				removeDeadThreads();
				if (threads.size() == maxThreads)
					Thread.sleep(newJobStartWaitTime);
			}
			if (Thread.currentThread().isInterrupted())
				haltAllJobs();
		} catch (Exception e) {
			haltAllJobs();
		} 
		
	}


	private synchronized void haltAllJobs(){
		
		if (isHalting)
			return;
		else
			isHalting = true;
		
		Iterator<IJobThread> threadIt = threads.iterator();
		while (threadIt.hasNext()) {
			threadIt.next().interrupt();
		}
		
		while (threads.size() > 0) {Thread.yield();
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			removeDeadThreads();
		}
		
		
	}
	
	
	private void removeDeadThreads () {
		
		Iterator<IJobThread> threadIt = threads.iterator();
		while (threadIt.hasNext()) {
			IJobThread thisThread = threadIt.next();
			if (!thisThread.isAlive() || thisThread.isInterrupted() || thisThread.getThrownException() != null) {
				threads.remove(thisThread);
				thisThread = null;
				threadIt = threads.iterator();
			}
		}
		
	}
	
	
	
	
	public void setIJobs(List<IJob> iJobs) {
		this.iJobs = iJobs;
	}


	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}


	public void setNewJobStartWaitTime(int newJobStartWaitTime) {
		this.newJobStartWaitTime = newJobStartWaitTime;
	}
	
	
	
	
	private class IJobThread extends Thread {
		
		IJob iJob;
		
		IJobThread (IJob iJob) {
			this.iJob = iJob;
		}
		
		private Exception thrownException = null;
		
		public Exception getThrownException() {
			return thrownException;
		}
		
		public void run() {
			
			try {
				iJob.execute();
			} catch (Exception e) {
				e.printStackTrace();
				haltAllJobs();
				thrownException = e;
			}
			
		}
		
	}

}
