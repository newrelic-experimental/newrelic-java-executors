package com.nr.instrumentation.util.concurrent;

import java.util.concurrent.Callable;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.executors.Utils;

@Weave(type=MatchType.BaseClass,originalName="java.util.concurrent.ForkJoinTask")
public abstract class ForkJoinTask_instrumentation<V> {

	@NewField
	public boolean alreadyWrapped = false;
	
	@NewField
	public Token token = null;
	
	
	@Trace(async=true,excludeFromTransactionTrace=true)
	public boolean cancel(boolean mayInterruptIfRunning) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		return Weaver.callOriginal();
	}
	
	public abstract void complete(V value);
	
	@Trace(excludeFromTransactionTrace=true)
	public void completeExceptionally(Throwable ex) {
		NewRelic.noticeError(ex);
		Weaver.callOriginal();
	}
	
	@Trace(async=true,excludeFromTransactionTrace=true)
	int doExec() {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ForkJoinTask",getClass().getSimpleName(),"doExec");
		if(token != null) {
			token.link();
		}
		return Weaver.callOriginal();
	}
	
	@Trace(async=true,excludeFromTransactionTrace=true)
	protected boolean exec() {
		if(token != null) {
			token.link();
		}
		return Weaver.callOriginal();
	}
	
	@Trace
	public ForkJoinTask_instrumentation<V> fork() {
		return Weaver.callOriginal();
	}
	
	@Trace
	public V invoke() {
		return Weaver.callOriginal();
	}
	
	@Trace
	public V join() {
		return Weaver.callOriginal();
	}
	
	@Trace
	public void reinitialize() {
		Weaver.callOriginal();
	}

	@Trace(excludeFromTransactionTrace=true)
	private int setCompletion(int completion) {
		if(token != null) {
			token.expire();
			token = null;
		}
		return Weaver.callOriginal();
	}

	@Weave(originalName="java.util.concurrent.ForkJoinTask$AdaptedCallable")
	static abstract class AdaptedCallable_instrumentation<T> extends ForkJoinTask_instrumentation<T> {
		
		AdaptedCallable_instrumentation(Callable<? extends T> callable) {
			super.alreadyWrapped = false;
		}
		
	}

	@Weave(originalName="java.util.concurrent.ForkJoinTask$AdaptedRunnable")
	static abstract class AdaptedRunnable_instrumentation<T> extends ForkJoinTask_instrumentation<T> {
		AdaptedRunnable_instrumentation(Runnable runnable, T result) {
			super.alreadyWrapped = false;
		}
	}
	
	@Weave(originalName="java.util.concurrent.ForkJoinTask$AdaptedRunnableAction")
	static abstract class AdaptedRunnableAction_instrumentation<T> extends ForkJoinTask_instrumentation<T> {
		AdaptedRunnableAction_instrumentation(Runnable runnable) {
			super.alreadyWrapped = false;
		}
	}
	
	@Weave(originalName="java.util.concurrent.ForkJoinTask$RunnableExecuteAction")
	static abstract class RunnableExecuteAction_instrumentation<T> extends ForkJoinTask_instrumentation<T> {
		RunnableExecuteAction_instrumentation(Runnable runnable) {
			super.alreadyWrapped = false;
			
		}
	}
	
}
