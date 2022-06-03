package com.nr.instrumentation.executors;

import java.util.function.BiConsumer;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRBiConsumer<T,U> extends NRTokenWrapper implements BiConsumer<T, U> {
	
	private BiConsumer<T,U> delegate = null;
	
	private static boolean isTransformed = false;
	
	public NRBiConsumer(BiConsumer<T,U> d,boolean hasExe) {
		super(hasExe);
		delegate = d;
		
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	@Override
	@Trace(async=true)
	public void accept(T t, U u) {
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
					if(token != null) {
						token.link();
					}
				}
			} 
		}
		if(u instanceof Throwable) {
			NewRelic.noticeError((Throwable)u);
		}
		if(delegate != null) {
			delegate.accept(t, u);
		}
	}

}
