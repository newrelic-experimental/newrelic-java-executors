package com.nr.instrumentation.labs.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.labs.executors.NRCallable;
import com.nr.instrumentation.labs.executors.NRRunnable;
import com.nr.instrumentation.labs.executors.Utils;

@Weave(type=MatchType.BaseClass,originalName="java.util.concurrent.AbstractExecutorService")
public abstract class AbstractExecutorService_instrumentation {

	public Future<?> submit(Runnable task) {
		NRRunnable wrapper = Utils.getWrapper(task);
		if(task != null && wrapper != null) {
			task = wrapper;
		}
		return Weaver.callOriginal();
	}
	
	public <T> Future<T> submit(Runnable task, T result) {
		NRRunnable wrapper = Utils.getWrapper(task);
		if(task != null && wrapper != null) {
			task = wrapper;
		}
		return Weaver.callOriginal();
	}
	
	public <T> Future<T> submit(Callable<T> task) {
		NRCallable<T> wrapper = Utils.getWrapper(task);
		if(wrapper != null) {
			task = wrapper;
		}
		return Weaver.callOriginal();
	}
}
