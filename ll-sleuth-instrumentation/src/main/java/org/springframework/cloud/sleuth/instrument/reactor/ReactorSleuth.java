/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.sleuth.instrument.reactor;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.cloud.sleuth.internal.LazyBean;
import org.springframework.context.ConfigurableApplicationContext;
import reactor.core.CoreSubscriber;
import reactor.core.Fuseable;
import reactor.core.Scannable;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.springframework.cloud.sleuth.instrument.reactor.ReactorHooksHelper.named;

/**
 * Reactive Span pointcuts factories.
 *
 * @author Stephane Maldini
 * @author Roman Matiushchenko
 * @since 2.0.0
 */
// TODO: this is public as it is used out of package, but unlikely intended to be
// non-internal
public abstract class ReactorSleuth {

	private static final Log log = LogFactory.getLog(ReactorSleuth.class);

	private ReactorSleuth() {
	}

	/**
	 * Function that does additional wrapping of the Reactor context.
	 */
	public static Function<Context, Context> contextWrappingFunction = Function.identity();

	static <O> BiFunction<Publisher, ? super CoreSubscriber<? super O>, ? extends CoreSubscriber<? super O>> liftFunction(
			ConfigurableApplicationContext springContext, LazyBean<CurrentTraceContext> lazyCurrentTraceContext,
			LazyBean<Tracer> lazyTracer) {
		return (p, sub) -> {
			if (!springContext.isActive() || !springContext.isRunning()) {
				if (log.isTraceEnabled()) {
					String message = "Spring Context [" + springContext
							+ "] is not yet refreshed. This is unexpected. Reactor Context is [" + context(sub)
							+ "] and name is [" + name(sub) + "]";
					log.trace(message);
				}
				return sub;
			}

			Context context = context(sub);

			if (log.isTraceEnabled()) {
				log.trace("Spring context [" + springContext + "], Reactor context [" + context + "], name ["
						+ name(sub) + "]");
			}

			// Try to get the current trace context bean, lenient when there are problems
			CurrentTraceContext currentTraceContext = lazyCurrentTraceContext.get();
			if (currentTraceContext == null) {
				if (log.isTraceEnabled()) {
					String message = "Spring Context [" + springContext
							+ "] did not return a CurrentTraceContext. Reactor Context is [" + context
							+ "] and name is [" + name(sub) + "]";
					log.trace(message);
				}
				return sub;
			}

			TraceContext parent = traceContext(context, currentTraceContext);
			if (parent == null) {
				return sub; // no need to scope a null parent
			}

			// Handle scenarios such as Mono.defer
			if (sub instanceof ScopePassingSpanSubscriber) {
				ScopePassingSpanSubscriber<?> scopePassing = (ScopePassingSpanSubscriber<?>) sub;
				if (scopePassing.parent.equals(parent)) {
					return sub; // don't double-wrap
				}
			}

			context = contextWithBeans(context, lazyTracer, lazyCurrentTraceContext);
			if (log.isTraceEnabled()) {
				log.trace("Spring context [" + springContext + "], Reactor context [" + context + "], name ["
						+ name(sub) + "]");
			}

			if (log.isTraceEnabled()) {
				log.trace("Creating a scope passing span subscriber with Reactor Context " + "[" + context
						+ "] and name [" + name(sub) + "]");
			}

			return new ScopePassingSpanSubscriber<>(sub, context, currentTraceContext, parent);
		};
	}

	private static <T> Context contextWithBeans(Context context, LazyBean<Tracer> tracer,
			LazyBean<CurrentTraceContext> currentTraceContext) {
		if (!context.hasKey(Tracer.class)) {
			context = context.put(Tracer.class, tracer.getOrError());
		}
		if (!context.hasKey(CurrentTraceContext.class)) {
			context = context.put(CurrentTraceContext.class, currentTraceContext.getOrError());
		}
		return context;
	}

	private static <T> Context context(CoreSubscriber<? super T> sub) {
		try {
			return sub.currentContext();
		}
		catch (Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug("Exception occurred while trying to retrieve the context", ex);
			}
		}
		return Context.empty();
	}

	static String name(CoreSubscriber<?> sub) {
		return Scannable.from(sub).name();
	}

	/**
	 * Like {@link CurrentTraceContext#context()}, except it first checks the reactor
	 * context.
	 */
	static TraceContext traceContext(Context context, CurrentTraceContext fallback) {
		if (context.hasKey(TraceContext.class)) {
			return context.get(TraceContext.class);
		}
		return fallback.context();
	}

	/**
	 * Mutates the Reactor context depending on the classpath contents.
	 * @param context Reactor context
	 * @return mutated context
	 */
	public static Context wrapContext(Context context) {
		return contextWrappingFunction.apply(context);
	}
}

