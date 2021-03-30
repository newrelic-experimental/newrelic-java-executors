package com.nr.instrumentation.util.concurrent;

import java.util.concurrent.BlockingQueue;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.executors.NRBlockingQueueWrapper;
import com.nr.instrumentation.executors.NRRunnable;
import com.nr.instrumentation.executors.Utils;

@Weave(type=MatchType.BaseClass,originalName="java.util.concurrent.ThreadPoolExecutor")
public abstract class ThreadPoolExecutor_instrumentation {

	public void execute(Runnable task) {
		NRRunnable wrapper = Utils.getWrapper(task);
		if(task != null && wrapper != null) {
			task = wrapper;
		}
		Weaver.callOriginal();
		
	}
	
	public BlockingQueue<Runnable> getQueue() {
		BlockingQueue<Runnable> queue = Weaver.callOriginal();
		if(!(queue instanceof NRBlockingQueueWrapper)) {
			return new NRBlockingQueueWrapper(queue);
		}
		return queue;
	}
}
