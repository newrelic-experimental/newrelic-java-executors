package com.nr.instrumentation.executors;

import java.util.concurrent.atomic.AtomicInteger;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;

public class NRTokenWrapper {

	protected boolean hasExecutor;
	protected TokenAndRefCount refCount = null;
	
	public NRTokenWrapper(boolean h) {
		hasExecutor = h;
		
		if (hasExecutor) {
			refCount = Utils.activeToken.get();
			if (refCount == null) {
				Token t = NewRelic.getAgent().getTransaction().getToken();
				if (t != null && t.isActive()) {
					refCount = new TokenAndRefCount(t, new AtomicInteger(1));
					Utils.activeToken.set(refCount);
				} else if(t != null) {
					t.expire();
					t = null;
				}
			} else {
				
				if (refCount.refCount.get() < Utils.MAXCFSEGMENTS) {
					if (refCount.token == null) {
						Token t = NewRelic.getAgent().getTransaction().getToken();
						if (t != null && t.isActive()) {
							refCount.token = t;
						} else if (t != null) {
							t.expire();
							t = null;
						}

					}
					refCount.refCount.incrementAndGet();
				}
			} 
		}
	}

	protected TokenAndRefCount getToken() {
		TokenAndRefCount refCount = Utils.activeToken.get();

		return refCount;
	}
}
