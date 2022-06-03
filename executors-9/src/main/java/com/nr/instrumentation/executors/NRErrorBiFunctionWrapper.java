package com.nr.instrumentation.executors;

import java.util.function.BiFunction;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRErrorBiFunctionWrapper<T, R> extends NRTokenWrapper implements BiFunction<T, Throwable, R> {

	private BiFunction<T, Throwable, R> delegate = null;
	
	private static boolean isTransformed = false;
	
	
	public NRErrorBiFunctionWrapper(BiFunction<T, Throwable, R> d, boolean hasExec) {
		super(hasExec);
		delegate = d;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}
	
	@Override
	@Trace(async=true)
	public R apply(T t, Throwable e) {
		NewRelic.noticeError(e);
		if (hasExecutor) {
			if (refCount != null) {
				Token token = refCount.token;
				int count = refCount.refCount.decrementAndGet();
				if (count < 1) {
					if (token != null) {
						token.linkAndExpire();
					}
					refCount.token = null;
				} else {
					if(token != null) {
						token.link();
					}
				}
			} 
		}
		if(delegate != null) {
			return delegate.apply(t, e);
		}
		return null;
	}

}
