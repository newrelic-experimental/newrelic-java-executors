package com.nr.instrumentation.util.concurrent;

import java.util.concurrent.Callable;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.executors.Utils;

@Weave(originalName="java.util.concurrent.ForkJoinPool")
public abstract class ForkJoinPool_instrumentation {

	public void execute(Runnable task) {
		Weaver.callOriginal();

	}

	public ForkJoinTask_instrumentation<?> submit(Runnable task) {
		return Weaver.callOriginal();
	}

	public <T> ForkJoinTask_instrumentation<T> submit(Runnable task, T result) {
		return Weaver.callOriginal();
	}

	public <T> ForkJoinTask_instrumentation<T> submit(Callable<T> task) {
		return Weaver.callOriginal();
	}

	@Trace
	void externalPush(ForkJoinTask_instrumentation<?> task) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","ForkJoinPool","externalPush",task.getClass().getName()});

		if (!Utils.isCompletion(task)) {
			if (task.token == null && !task.alreadyWrapped) {

				Token t = Utils.getToken(task);
				if (t != null && t.isActive()) {
					task.token = t;
				} else {
					if (t != null && !t.isActive()) {
						t.expire();
						t = null;
					}
				}
			}
		}
		
		if(task.token == null) {
			Token t = Utils.getToken(task);
			if(t !=  null && t.isActive()) {
				task.token = t;
			} else {
				if(t != null && !t.isActive()) {
					t.expire();
					t = null;
				}
			} 
		} else {
			
		}
		Weaver.callOriginal();
	}

	@Weave(originalName="java.util.concurrent.ForkJoinPool$WorkQueue")
	static final class WorkQueue_instrumentation {

		@Trace(dispatcher=true)
		void push(ForkJoinTask_instrumentation<?> task) {
			if (!Utils.isCompletion(task)) {
				if (task.token == null && !task.alreadyWrapped) {

					Token t = Utils.getToken(task);
					if (t != null && t.isActive()) {
						task.token = t;
					} else {
						if (t != null && !t.isActive()) {
							t.expire();
							t = null;
						}
					}
				} 
			} else {
//				NewRelic.getAgent().getTransaction().ignore();
			}
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","WorkQueue","push",task.getClass().getName()});
			Weaver.callOriginal();
		}


	}
}
