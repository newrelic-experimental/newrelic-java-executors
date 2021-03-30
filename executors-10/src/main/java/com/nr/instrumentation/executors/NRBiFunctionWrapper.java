package com.nr.instrumentation.executors;

import java.util.function.BiFunction;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRBiFunctionWrapper<T, U, R> extends NRTokenWrapper implements BiFunction<T, U, R> {

	private BiFunction<T, U, R> delegate = null;
	
	private static boolean isTransformed = false;
	
	public NRBiFunctionWrapper(BiFunction<T, U, R> d, boolean hasExec) {
		super(hasExec);
		delegate = d;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}
	
	@Override
	@Trace(async=true)
	public R apply(T t, U u) {
		if (hasExecutor) {
			if (refCount != null) {
				Token token = refCount.token;
				int count = refCount.refCount.decrementAndGet();
				if (count < 1) {
					if(token != null) {
						token.linkAndExpire();
					}
					refCount.token = null;
				} else {
					token.link();
				}
			} 
		}
		if(delegate != null) {
			return delegate.apply(t, u);
		}
		return null;
	}

}
