package com.nr.instrumentation.labs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.function.Supplier;
import java.util.logging.Level;

import com.newrelic.api.agent.Config;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.nr.instrumentation.labs.util.concurrent.ForkJoinTask_instrumentation;

public class Utils {

	public static ThreadLocal<TokenAndRefCount> activeToken = new ThreadLocal<TokenAndRefCount>();
	
	public static int MAXCFSEGMENTS = 20;
	
	public static final String LAMBDA = "$$Lambda";
	
	private static List<String> ignoredPackages = new ArrayList<String>();
	
	private static List<String> ignoredClasses = new ArrayList<String>();

	private static List<Class<?>> ignoredSuperClasses = new ArrayList<Class<?>>();

	private static final String NEWRELICAGENT = "com.newrelic.agent";
	
	// Not why but Caffeine causes unexpired tokens so ignoring
	private static final String IGNORECAFFEINE = "com.github.benmanes.caffeine";
	// ditto for sbt
	private static final String IGNORESBT = "sbt";

	public static final String IGNORESPACKAGESPROPERTY = "Executors.ignore.packages";
	
	public static final String IGNORESCLASSESPROPERTY = "Executors.ignore.classes";
	
	public static final String MAXCOMPLETABLEPROPERTY = "Executors.completable.max";
	
	static {
		NewRelic.getAgent().getLogger().log(Level.FINE, "initialized list of packages ignored for runnables: {0} ", ignoredPackages);
		
		ignoredPackages.add(IGNORECAFFEINE);
		ignoredPackages.add(IGNORESBT);
		Config config = NewRelic.getAgent().getConfig();
		String ignoreList = config.getValue(IGNORESPACKAGESPROPERTY);
		if(ignoreList != null && !ignoreList.isEmpty()) {
			StringTokenizer st = new StringTokenizer(ignoreList,",");
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				if(token != null && !token.isEmpty() && !ignoredPackages.contains(token)) {
					NewRelic.getAgent().getLogger().log(Level.FINE, "adding the package {0} to list of packages to ignore runnables from ", token);
					ignoredPackages.add(token);
				}
			}
		}
		NewRelic.getAgent().getLogger().log(Level.FINE, "list of packages ignored for runnables: {0} ", ignoredPackages);
		ignoreList = config.getValue(IGNORESCLASSESPROPERTY);
		if(ignoreList != null && !ignoreList.isEmpty()) {
			StringTokenizer st = new StringTokenizer(ignoreList,",");
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				if(token != null && !token.isEmpty() && !ignoredClasses.contains(token)) {
					NewRelic.getAgent().getLogger().log(Level.FINE, "adding the class {0} to list of classes to ignore runnables from ", token);
					ignoredClasses.add(token);
				}
			}
		}
		NewRelic.getAgent().getLogger().log(Level.FINE, "list of packages ignored for runnables: {0} ", ignoredPackages);

		Integer max = config.getValue(MAXCOMPLETABLEPROPERTY);
		if(max != null && max != MAXCFSEGMENTS) {
			MAXCFSEGMENTS = max;
		}
	}
	
	public static <T> NRSupplier<T> getWrapper(Supplier<T> supplier) {
		if(supplier == null || supplier instanceof NRSupplier) return null;
		
		String fullclassname = supplier.getClass().getName();
		if(ignoredClasses.contains(fullclassname)) return null;
				
		Package runPackage = supplier.getClass().getPackage();
		
		if(runPackage.getName().startsWith(NEWRELICAGENT)) return null;
		
		for(String ignore : ignoredPackages) {
			if(runPackage.getName().startsWith(ignore)) return null;
		}
		
		Token token = NewRelic.getAgent().getTransaction().getToken();
		if(!token.isActive()) {
			token.expire();
			token = null;
			return null;
		}

		return new NRSupplier<T>(supplier,token);
		
	}
	
	public static NRRunnable getWrapper(Runnable runnable) {
		
		if(runnable == null || runnable instanceof NRRunnable || runnable instanceof RunnableFuture) return null;
		
		if(isCompletion(runnable)) {
			Token token = NewRelic.getAgent().getTransaction().getToken();
			if(!token.isActive()) {
				token.expire();
				token = null;
				return null;
			}

			return new NRRunnable(runnable,token);
		}
		
		String fullclassname = runnable.getClass().getName();
		if(ignoredClasses.contains(fullclassname)) return null;
		
		for(Class<?> theClass : ignoredSuperClasses) {
			if(theClass.isInstance(runnable)) return null;
		}
						
		Package runPackage = runnable.getClass().getPackage();
		
		if(runPackage.getName().startsWith(NEWRELICAGENT)) return null;
		
		for(String ignore : ignoredPackages) {
			if(runPackage.getName().startsWith(ignore)) return null;
		}
				
		Token token = NewRelic.getAgent().getTransaction().getToken();
		if(!token.isActive()) {
			token.expire();
			token = null;
			return null;
		}

		return new NRRunnable(runnable,token);
	}
	
	public static <V> NRCallable<V> getWrapper(Callable<V> callable) {
		if(callable == null || callable instanceof NRCallable) return null;
		
		String fullclassname = callable.getClass().getName();
		if(ignoredClasses.contains(fullclassname)) return null;

		Package runPackage = callable.getClass().getPackage();
		
		if(runPackage.getName().startsWith(NEWRELICAGENT)) return null;
		
		for(String ignore : ignoredPackages) {
			if(runPackage.getName().startsWith(ignore)) return null;
		}
				
		Token token = NewRelic.getAgent().getTransaction().getToken();
		if(!token.isActive()) {
			token.expire();
			token = null;
			return null;
		}
		
		
		return new NRCallable<V>(callable,token);
	}
	
	public static <V> Token getToken(ForkJoinTask_instrumentation<V> task) {
		if(task == null) return null;
		if(task.alreadyWrapped) return null;
		
		String fullclassname = task.getClass().getName();
		if(ignoredClasses.contains(fullclassname)) return null;

		Package runPackage = task.getClass().getPackage();
		
		if(runPackage.getName().startsWith(NEWRELICAGENT)) return null;
		
		for(String ignore : ignoredPackages) {
			if(runPackage.getName().startsWith(ignore)) return null;
		}
				
		Token token = NewRelic.getAgent().getTransaction().getToken();
		if(!token.isActive()) {
			token.expire();
			token = null;
			return null;
		}
		
		return token;
	}

	public static void addIfNotPresent(List<String> list) {
		for(String s : list) {
			if(s != null && !s.isEmpty() && !ignoredPackages.contains(s)) {
				NewRelic.getAgent().getLogger().log(Level.FINE, "adding the package {0} to list of packages to ignore runnables from ", s);
				ignoredPackages.add(s);
			}
		}
		NewRelic.getAgent().getLogger().log(Level.FINE, "list of packages ignored for runnables: {0} ", ignoredPackages);
	}
	
	
	public static boolean isCompletion(Object obj) {
		String classname = obj.getClass().getName();
		
		if(!classname.startsWith("java.util.concurrent.CompletableFuture$")) return false;
		
		if(classname.startsWith("java.util.concurrent.CompletableFuture$Bi")) return true;
		
		if(classname.startsWith("java.util.concurrent.CompletableFuture$Uni")) return true;
		
		if(classname.startsWith("java.util.concurrent.CompletableFuture$Or")) return true;
		
		if(classname.equals("java.util.concurrent.CompletableFuture$AnyOf")) return true;
		
		if(classname.equals("java.util.concurrent.CompletableFuture.CoCompletion")) return true;
				
		if(classname.equals("java.util.concurrent.CompletableFuture.Signaller")) return true;

		return false;
	}
	
	public static void log(String format,Object... args) {
		NewRelic.getAgent().getLogger().log(Level.FINE, format, args);
	}

	public static void log(Exception e, String format,Object... args) {
		NewRelic.getAgent().getLogger().log(Level.FINE, e, format,args);
	}

}
