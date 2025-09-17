package com.nr.instrumentation.labs.util.concurrent;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.labs.executors.Utils;

@Weave(type=MatchType.BaseClass,originalName="java.util.concurrent.CountedCompleter")
public abstract class CountedCompleter_instrumentation<T> extends ForkJoinTask_instrumentation<T> {

	
	protected CountedCompleter_instrumentation() {
		super.alreadyWrapped = false;
	}
	
	protected CountedCompleter_instrumentation(CountedCompleter_instrumentation<?> completer) {
		super.alreadyWrapped = false;
	}
	
	protected CountedCompleter_instrumentation(CountedCompleter_instrumentation<?> completer,int initialPendingCount) {
		super.alreadyWrapped = false;
	}
	
	final CountedCompleter_instrumentation<?> completer = Weaver.callOriginal();
	volatile int pending = Weaver.callOriginal();
	
	@SuppressWarnings("unchecked")
	@Trace
	protected boolean exec() {
		
		CountedCompleter_instrumentation<?> c = completer;
		
		if(c != null && super.token != null) {
			while (c !=  null) {
				ForkJoinTask_instrumentation<T> task = (ForkJoinTask_instrumentation<T>) completer;
				if (task.token == null) {
					task.token = super.token;
				} 
				c = c.completer;
			}
		}
		return Weaver.callOriginal();
	}
	
	public void tryComplete() {
		if(super.token != null) {
			super.token.expire();
			super.token = null;
		}
		Weaver.callOriginal();
	}
	
	public void complete(T rawResult) {
		if(super.token != null) {
			super.token.expire();
			super.token = null;
		}
		Weaver.callOriginal();
	}
	
	public void propagateCompletion() {
		if(super.token != null) {
			super.token.expire();
			super.token = null;
		}
		Weaver.callOriginal();
	}
	
	@Trace
	public void compute() {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","CountedCompleter",getClass().getSimpleName(),"compute"});
		Weaver.callOriginal();
	}
	
}
