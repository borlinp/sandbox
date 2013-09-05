package com.paul;

import org.junit.Ignore;

@Ignore
public class InteruptableIJob implements IJob {
	
	private int sleepTime = 15000;
	
	public InteruptableIJob () {}
	
	public InteruptableIJob (int sleepTime) {
		this.sleepTime = sleepTime;
	}


	public void execute() throws Exception {
		
		Thread.sleep(sleepTime);

	}

}
