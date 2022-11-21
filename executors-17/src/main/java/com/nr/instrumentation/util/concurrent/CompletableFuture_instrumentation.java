package com.nr.instrumentation.util.concurrent;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.executors.NRBiConsumer;
import com.nr.instrumentation.executors.NRBiFunctionWrapper;
import com.nr.instrumentation.executors.NRConsumerWrapper;
import com.nr.instrumentation.executors.NRErrorBiFunctionWrapper;
import com.nr.instrumentation.executors.NRErrorFunctionWrapper;
import com.nr.instrumentation.executors.NRFunctionWrapper;
import com.nr.instrumentation.executors.NRRunnable;
import com.nr.instrumentation.executors.NRSupplier;
import com.nr.instrumentation.executors.TokenAndRefCount;
import com.nr.instrumentation.executors.Utils;

@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
@Weave(originalName="java.util.concurrent.CompletableFuture")
public abstract class CompletableFuture_instrumentation<T> implements CompletionStage<T> {

	volatile Completion_instrumentation stack = Weaver.callOriginal();

	volatile Object result = Weaver.callOriginal();

	public CompletableFuture_instrumentation() {
	}

	CompletableFuture_instrumentation(Object o) {
	}

	private CompletableFuture_instrumentation<Void> uniAcceptStage(Executor e, Consumer<? super T> f) {
		if(e != null && !(f instanceof NRConsumerWrapper)) {
			Token t = NewRelic.getAgent().getTransaction().getToken();
			if(t != null && t.isActive()) {
				NRConsumerWrapper wrapper = new NRConsumerWrapper(f,true);
				f = wrapper;
			} else if(t != null) {
				t.expire();
				t = null;
			}
		}
		return Weaver.callOriginal();
	}

	private <U,V> CompletableFuture_instrumentation<V> biApplyStage(Executor e, CompletionStage<U> o,BiFunction<? super T,? super U,? extends V> f) {
		if(e != null && !(f instanceof NRBiFunctionWrapper)) {
			NRBiFunctionWrapper wrapper = new NRBiFunctionWrapper(f,true);
			f = wrapper;
		}

		CompletableFuture_instrumentation<V>  result = Weaver.callOriginal();

		return result;
	}

	private <U> CompletableFuture_instrumentation<Void> biAcceptStage(Executor e, CompletionStage<U> o,BiConsumer<? super T,? super U> f) {
		if(e != null && !(f instanceof NRBiConsumer)) {
			NRBiConsumer wrapper = new NRBiConsumer(f, true);
			f = wrapper;
		}
		return Weaver.callOriginal();
	}

	private <V> CompletableFuture_instrumentation<V> uniApplyStage(Executor e, Function<? super T,? extends V> f) {
		if(e != null && !(f instanceof NRFunctionWrapper)) {
			NRFunctionWrapper wrapper = new NRFunctionWrapper(f,true);
			f = wrapper;
		}
		return Weaver.callOriginal();
	}

	private <V> CompletableFuture_instrumentation<V> uniComposeStage(Executor e, Function<? super T, ? extends CompletionStage<V>> f) {
		if(e != null && !(f instanceof NRFunctionWrapper)) {
			NRFunctionWrapper wrapper = new NRFunctionWrapper(f,true);
			f = wrapper;
		}
		return Weaver.callOriginal();
	}

	private CompletableFuture_instrumentation<Void> biRunStage(Executor e, CompletionStage<?> o, Runnable f) {
		if(e != null && !(f instanceof NRRunnable)) {
			NRRunnable wrapper = Utils.getWrapper(f);
			if(wrapper != null) {
				f = wrapper;
			}
		}
		return Weaver.callOriginal();
	}

	boolean uniExceptionally(Object r, Function<? super Throwable, ? extends T> f, UniExceptionally_instrumentation<T> c) {
		if(!(f instanceof NRErrorFunctionWrapper)) {
			NRErrorFunctionWrapper wrapper = new NRErrorFunctionWrapper(f,false);
			f = wrapper;
		}

		return Weaver.callOriginal();
	}

	private <V> CompletableFuture_instrumentation<V> uniHandleStage(Executor e, BiFunction<? super T, Throwable, ? extends V> f) {
		NRErrorBiFunctionWrapper wrapper =  new NRErrorBiFunctionWrapper(f, e != null);
		f = wrapper;
		return Weaver.callOriginal();
	}

	private CompletableFuture_instrumentation<Void> uniRunStage(Executor e, Runnable f) {
		if(e != null && !(f instanceof NRRunnable)) {
			NRRunnable wrapper = Utils.getWrapper(f);
			if(wrapper != null) {
				f = wrapper;
			}
		}
		return Weaver.callOriginal();
	}

	private CompletableFuture_instrumentation<T> uniWhenCompleteStage(Executor e, BiConsumer<? super T, ? super Throwable> f) {
		NRBiConsumer wrapper = new NRBiConsumer(f, e != null);

		f = wrapper;
		return Weaver.callOriginal();
	}

	private <U extends T> CompletableFuture_instrumentation<Void> orAcceptStage(Executor e, CompletionStage<U> o, Consumer<? super T> f) {
		if(e != null && !(f instanceof NRConsumerWrapper)) {
				NRConsumerWrapper wrapper = new NRConsumerWrapper(f,true);
				f = wrapper;
		}
		return Weaver.callOriginal();
	}

	private <U extends T,V> CompletableFuture_instrumentation<V> orApplyStage(Executor e, CompletionStage<U> o, Function<? super T, ? extends V> f) {
		if(e != null && !(f instanceof NRFunctionWrapper)) {
			NRFunctionWrapper wrapper = new NRFunctionWrapper(f,true);
			f = wrapper;
		}
		return Weaver.callOriginal();
	}

	static CompletableFuture_instrumentation<Void> asyncRunStage(Executor e, Runnable f) {
		NRRunnable wrapper = Utils.getWrapper(f);
		if(wrapper != null) {
			f = wrapper;
		}
		return Weaver.callOriginal();
	}

	static <U> CompletableFuture_instrumentation<U> asyncSupplyStage(Executor e,Supplier<U> f) {
		return Weaver.callOriginal();
	}

	@Weave(originalName="java.util.concurrent.CompletableFuture$AsyncRun")
	static abstract class AsyncRun_instrumentation extends ForkJoinTask_instrumentation<Void> {

		AsyncRun_instrumentation(CompletableFuture_instrumentation<Void> dep, Runnable fn) {
			super.alreadyWrapped = fn instanceof NRRunnable;
		}
	}

	@Weave(originalName="java.util.concurrent.CompletableFuture$AsyncSupply")
	static abstract class AsyncSupply<T> extends ForkJoinTask_instrumentation<Void> {

		AsyncSupply(CompletableFuture_instrumentation<T> dep, Supplier<? extends T> fn) {
			super.alreadyWrapped = fn instanceof NRSupplier;
		}


	}

	@Weave(type=MatchType.BaseClass,originalName="java.util.concurrent.CompletableFuture$Completion")
	static abstract class Completion_instrumentation  extends ForkJoinTask_instrumentation<Void> implements Runnable {

		volatile Completion_instrumentation next = Weaver.callOriginal();

		@NewField
		public TokenAndRefCount tokenAndRefCount = null;

		@WeaveAllConstructors
		Completion_instrumentation() {
			tokenAndRefCount = Utils.activeToken.get();
		}

		@Trace(async=true,excludeFromTransactionTrace=true)
		public boolean exec() {
			if(tokenAndRefCount != null) {
				Token token = tokenAndRefCount.token;
				if(token != null) {
					token.link();
				}
			}
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Completion",getClass().getSimpleName(),"exec");
			return Weaver.callOriginal();
		}

		@Trace(async=true,excludeFromTransactionTrace=true)
		CompletableFuture_instrumentation<?> tryFire(int mode) {
			if(super.token != null) {
				super.token.linkAndExpire();
				super.token = null;
			}
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Completion",getClass().getSimpleName(),"tryFire",getModeName(mode));
			return Weaver.callOriginal();
		}

		private String getModeName(int mode) {
			switch(mode) {
			case 0:
				return "SYNC";
			case 1:
				return "ASYNC";
			case -1:
				return "NESTED";

			}
			return "UNKNOWN";
		}
	}

	@Weave(originalName="java.util.concurrent.CompletableFuture$UniExceptionally")
	static class UniExceptionally_instrumentation<T> {

	}

}
