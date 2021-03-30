package com.nr.instrumentation.executors;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRRunnable implements Runnable {
	
	private Runnable delegate = null;
	private Token token = null;
	private static boolean isTransformed = false;
	
	public NRRunnable(Runnable r,Token t) {
		delegate = r;
		token = t;
		if(!isTransformed) {
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
			isTransformed = true;
		}
	}

	@Override
	@Trace(async=true,excludeFromTransactionTrace=true)
	public void run() {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Executors","Submitted-Runnable",delegate.getClass().getSimpleName());
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		delegate.run();
	}

	public void cancel() {
		if(token != null) {
			token.expire();
			token = null;
		}
	}
}
