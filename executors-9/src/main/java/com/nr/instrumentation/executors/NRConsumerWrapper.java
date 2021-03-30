package com.nr.instrumentation.executors;

import java.util.function.Consumer;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRConsumerWrapper<T> extends NRTokenWrapper implements Consumer<T> {
	
	private Consumer<T> delegate = null;
	
	private static boolean isTransformed = false;
	
	public NRConsumerWrapper(Consumer<T> d, boolean hasExec) {
		super(hasExec);
		delegate = d;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	@Override
	@Trace(async=true)
	public void accept(T t) {
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
		if (delegate != null) {
			delegate.accept(t);
		}
	}
}
