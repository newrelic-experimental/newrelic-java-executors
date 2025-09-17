package com.nr.instrumentation.labs.executors;

import java.util.function.Supplier;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRSupplier<T> implements Supplier<T> {
	
	private Supplier<T> delegate = null;
	private Token token = null;
	private static boolean isTransformed = false;
	
	public NRSupplier(Supplier<T> d,Token t) {
		delegate = d;
		token = t;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	@Override
	@Trace(async=true,excludeFromTransactionTrace=true)
	public T get() {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Executors","Submitted-Supplier","get");
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		return delegate.get();
	}

	public Token cancel() {
		Token t = token;
		token = null;
		return t;
	}
	
}
