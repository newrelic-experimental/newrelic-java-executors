package com.nr.instrumentation.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.executors.NRCallable;
import com.nr.instrumentation.executors.NRRunnable;
import com.nr.instrumentation.executors.NRRunnableFuture;
import com.nr.instrumentation.executors.Utils;

@Weave(originalName = "java.util.concurrent.ExecutorCompletionService")
public abstract class CompletionService_instrumentation<V> {

	
	public Future<V> submit(Callable<V> task) {
		NRCallable<V> wrapper = Utils.getWrapper(task);
		if(wrapper != null) {
			task = wrapper;
		}
		return Weaver.callOriginal();
	}
	
	public Future<V> submit(Runnable task, V result) {
		NRRunnable wrapper = Utils.getWrapper(task);
		if(wrapper != null) {
			task = wrapper;
		}
		return Weaver.callOriginal();
	}
	
	@SuppressWarnings("unused")
	private RunnableFuture<V> newTaskFor(Runnable task, V result) {
		NRRunnable taskWrapper = Utils.getWrapper(task);
		if(taskWrapper != null) {
			task = taskWrapper;
		}
		RunnableFuture<V> f = Weaver.callOriginal();
		if(!(f instanceof NRRunnableFuture)) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			if(token != null && token.isActive()) {
				NRRunnableFuture<V> wrapper = new NRRunnableFuture<V>(f, token);
				f = wrapper;
			} else if(token != null) {
				token.expire();
				token = null;
			}
		}
		return f;
	}
	
	@SuppressWarnings("unused")
	private RunnableFuture<V> newTaskFor(Callable<V> task) {
		NRCallable<V> callableWrapper = Utils.getWrapper(task);
		if(callableWrapper != null) {
			task = callableWrapper;
		}
		RunnableFuture<V> f = Weaver.callOriginal();
		if(!(f instanceof NRRunnableFuture)) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			if(token != null && token.isActive()) {
				NRRunnableFuture<V> wrapper = new NRRunnableFuture<V>(f, token);
				wrapper.setToken(NewRelic.getAgent().getTransaction().getToken());
				f = wrapper;
			} else if(token != null) {
				token.expire();
				token = null;
			}
		}
		return f;
	}
	
	@Trace(async = true)
	public Future<V> take() {
		Future<V> f = Weaver.callOriginal();
		if (f != null) {
			if (f instanceof NRRunnableFuture) {
				NRRunnableFuture<V> wrapper = (NRRunnableFuture<V>) f;
				Token t = wrapper.getToken();

				if (t != null) {
					t.linkAndExpire();
					t = null;
				}
			} 
		}
		return f;
	}
	
	@Trace(async = true)
	public Future<V> poll() {
		Future<V> f = Weaver.callOriginal();
		if (f != null) {
			if (f instanceof NRRunnableFuture) {
				NRRunnableFuture<V> wrapper = (NRRunnableFuture<V>) f;
				Token t = wrapper.getToken();

				if (t != null) {
					t.linkAndExpire();
					t = null;
				}
			} 
		}
		return f;
	}
	
	@Trace(async = true)
	public Future<V> poll(long timeout, TimeUnit unit) {
		Future<V> f = Weaver.callOriginal();
		if (f != null) {
			if (f instanceof NRRunnableFuture) {
				NRRunnableFuture<V> wrapper = (NRRunnableFuture<V>) f;
				Token t = wrapper.getToken();

				if (t != null) {
					t.linkAndExpire();
					t = null;
				}
			} 
		}
		return f;
	}
	
	
}
