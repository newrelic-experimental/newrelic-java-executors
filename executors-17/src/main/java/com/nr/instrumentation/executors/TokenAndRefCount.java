package com.nr.instrumentation.executors;

import java.util.concurrent.atomic.AtomicInteger;

import com.newrelic.api.agent.Token;

public class TokenAndRefCount {

	
	public Token token;
	public AtomicInteger refCount;
//	public AtomicInteger useCount;
	
	public TokenAndRefCount(Token t, AtomicInteger rc) {
		token = t;
		refCount = rc;
//		useCount = new AtomicInteger(0);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("token[");
		if(token != null) {
			sb.append(token.hashCode());
		} else {
			sb.append("null");
		}
		sb.append(']');

		sb.append(",refCount[");
		if(refCount != null) {
			sb.append(refCount.get());
		} else {
			sb.append("null");
		}
		sb.append(']');

//		sb.append(",useCount[");
//		if(useCount != null) {
//			sb.append(useCount.get());
//		} else {
//			sb.append("null");
//		}
//		sb.append(']');
		
		return sb.toString();
	}
}
