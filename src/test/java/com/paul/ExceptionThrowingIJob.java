package com.paul;

import org.junit.Ignore;

@Ignore
public class ExceptionThrowingIJob implements IJob {

	public void execute() throws Exception {
	
		//Thread.sleep(500);
		throw new IJobException("Oh no!");

	}

}
