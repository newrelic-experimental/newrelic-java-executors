package com.nr.instrumentation.labs.executors;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRRunnable implements Runnable {
	
	private Runnable delegate = null;
	protected Token token = null;
	private String classname = null;
	private static boolean isTransformed = false;
	
	public NRRunnable(Runnable r,Token t) {
		delegate = r;
		String cn = delegate.getClass().getSimpleName();
		if(cn.contains(Utils.LAMBDA)) {
			int index = cn.indexOf(Utils.LAMBDA);
			if(index > -1) {
				cn = cn.substring(0, index+Utils.LAMBDA.length());
			}
		}
		classname = cn != null && !cn.isEmpty() ? cn : "Runnable";
		token = t;
		if(!isTransformed) {
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
			isTransformed = true;
		}
	}

	@Override
	@Trace(async=true)
	public void run() {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Executors","Submitted-Runnable",classname);
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
