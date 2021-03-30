package com.nr.instrumentation.executors;

import java.util.function.Function;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRErrorFunctionWrapper<R> extends NRTokenWrapper implements Function<Throwable, R> {

	private Function<Throwable, R> delegate = null;
	
	private static boolean isTransformed = false;
	
	
	public NRErrorFunctionWrapper(Function<Throwable, R> d, boolean hasExec) {
		super(hasExec);
		delegate = d;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}
	
	@Override
	@Trace(async=true)
	public R apply(Throwable t) {
		NewRelic.noticeError(t);
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
					token.link();
				}
			} 
			Utils.logForkJoinTask("current value of TokenAndRefCount: {0}", refCount);
		}
		if(delegate != null) {
			return delegate.apply(t);
		}
		return null;
	}

}
