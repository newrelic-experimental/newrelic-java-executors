package com.nr.instrumentation.labs.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.labs.executors.NRCallable;
import com.nr.instrumentation.labs.executors.NRRunnable;
import com.nr.instrumentation.labs.executors.Utils;

@Weave(originalName="java.util.concurrent.ScheduledThreadPoolExecutor")
public abstract class ScheduledThreadPoolExecutor_instrumentation {

	
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		NRRunnable wrapper = Utils.getWrapper(command);
		if(wrapper != null) {
			command = wrapper;
		}
		return Weaver.callOriginal();
	}
	
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		NRCallable<V> wrapper = Utils.getWrapper(callable);
		if(wrapper != null) {
			callable = wrapper;
		}
		return Weaver.callOriginal();
	}
}
