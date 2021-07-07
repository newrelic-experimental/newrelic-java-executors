package com.nr.instrumentation.executors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class NRBlockingQueueWrapper implements BlockingQueue<Runnable> {

	private BlockingQueue<Runnable> delegate = null;

	public NRBlockingQueueWrapper(BlockingQueue<Runnable> d) {
		delegate = d;
	}

	@Override
	public Runnable remove() {
		return delegate.remove();
	}

	@Override
	public Runnable poll() {
		return delegate.poll();
	}

	@Override
	public Runnable element() {
		return delegate.element();
	}

	@Override
	public Runnable peek() {
		return delegate.peek();
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public Iterator<Runnable> iterator() {
		return delegate.iterator();
	}

	@Override
	public Object[] toArray() {
		return delegate.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return delegate.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return delegate.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Runnable> c) {
		Collection<Runnable> copy = new ArrayList<Runnable>();

		for(Runnable r : c) {
			if(!(r instanceof NRRunnable)) {
				NRRunnable wrapper = Utils.getWrapper(r);
				
				if(wrapper != null) {
					copy.add(wrapper);
				} else {
					copy.add(r);
				}
			} else {
				copy.add(r);
			}
		}
		return delegate.addAll(copy);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for(Object obj : c) {
			if(obj instanceof NRRunnable) {
				NRRunnable nr = (NRRunnable)obj;
				nr.cancel();
			}
		}

		return delegate.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Object[] all = toArray();
		for(Object obj : all) {
			if(!c.contains(obj)) {
				if(obj instanceof NRRunnable) {
					NRRunnable nr = (NRRunnable)obj;
					nr.cancel();
				}
			}
		}
		return delegate.retainAll(c);
	}

	@Override
	public void clear() {
		Object[] all = toArray();
		for(Object obj : all) {
			if(obj instanceof NRRunnable) {
				NRRunnable nr = (NRRunnable)obj;
				nr.cancel();
			}
		}
		delegate.clear();
	}

	@Override
	public boolean add(Runnable e) {
		NRRunnable wrapper = null;
		if(!(e instanceof NRRunnable)) {
			wrapper = Utils.getWrapper(e);
			if(wrapper != null) {
				e = wrapper;
			}
		}
		boolean b =  delegate.add(e);
		if(!b && wrapper != null) {
			wrapper.cancel();
		}
		return b;
	}

	@Override
	public boolean offer(Runnable e) {
		NRRunnable wrapper = null;
		if(!(e instanceof NRRunnable)) {
			wrapper = Utils.getWrapper(e);
			if(wrapper != null) {
				e = wrapper;
			}
		}
		boolean b =  delegate.offer(e);
		if(!b && wrapper != null) {
			wrapper.cancel();
		}
		return b;
	}

	@Override
	public void put(Runnable e) throws InterruptedException {
		NRRunnable wrapper = null;
		if(!(e instanceof NRRunnable)) {
			wrapper = Utils.getWrapper(e);
			if(wrapper != null) {
				e = wrapper;
			}
		}
		delegate.put(e);
	}

	@Override
	public boolean offer(Runnable e, long timeout, TimeUnit unit) throws InterruptedException {
		NRRunnable wrapper = null;
		if(!(e instanceof NRRunnable)) {
			wrapper = Utils.getWrapper(e);
			if(wrapper != null) {
				e = wrapper;
			}
		}
		boolean b =  delegate.offer(e,timeout,unit);
		if(!b && wrapper != null) {
			wrapper.cancel();
		}
		return b;
	}

	@Override
	public Runnable take() throws InterruptedException {
		return delegate.take();
	}

	@Override
	public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
		return delegate.poll(timeout, unit);
	}

	@Override
	public int remainingCapacity() {
		return delegate.remainingCapacity();
	}

	@Override
	public boolean remove(Object o) {
		if(o instanceof NRRunnable) {
			NRRunnable nr = (NRRunnable)o;
			nr.cancel();
		}
		return delegate.remove(o);
	}

	@Override
	public boolean contains(Object o) {
		return delegate.contains(o);
	}

	@Override
	public int drainTo(Collection<? super Runnable> c) {
		return delegate.drainTo(c);
	}

	@Override
	public int drainTo(Collection<? super Runnable> c, int maxElements) {
		return delegate.drainTo(c, maxElements);
	}



}
