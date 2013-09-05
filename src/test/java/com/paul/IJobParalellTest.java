package com.paul;

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;

import org.junit.Test;

public class IJobParalellTest {

	
	@Test
	public void testExecuteMaxThreads() {

		ThreadMXBean threads = ManagementFactory.getThreadMXBean();
		int beginningThreadCount = threads.getThreadCount();
		
		try {
			testJobs(5,10,-1,5000,1200);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		int endThreadCount = threads.getPeakThreadCount();
		
		assertEquals(5, endThreadCount - beginningThreadCount);
		
		
	}

	@Test
	public void testExecuteSafe() {

		ThreadMXBean threads = ManagementFactory.getThreadMXBean();
		long beginningThreadCount = threads.getTotalStartedThreadCount();
		
		try {
			testJobs(5,5,-1,1000,1200);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		long endThreadCount = threads.getTotalStartedThreadCount();
		
		assertEquals(5L, endThreadCount - beginningThreadCount);
		
		
	}

	
	@Test
	public void testExecuteException() {

		ThreadMXBean threads = ManagementFactory.getThreadMXBean();
		long beginningThreadCount = threads.getTotalStartedThreadCount();
		
		try {
			testJobs(5,10,4,5000,1200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long endThreadCount = threads.getTotalStartedThreadCount();
		
		assertEquals(5, Math.abs(endThreadCount - beginningThreadCount));
		
		
	}
	
	
	

	private void testJobs(
			int maxThreads,
			int numberOfJobs, 
			int addExceptionJobAtIndex,
			int jobRunTime,
			int newJobStartWaitTime) throws Exception {

		ArrayList<IJob> jobs = new ArrayList<IJob>();
		
		for (int i = 0; i < numberOfJobs; i++) {
			if (i == addExceptionJobAtIndex)
				jobs.add(new ExceptionThrowingIJob());
			else
				jobs.add(new InteruptableIJob(jobRunTime));
				
		}
		
		IJobParalell iJobP = new IJobParalell(jobs,maxThreads,newJobStartWaitTime);
		
		iJobP.execute();
		
		
	}

}
