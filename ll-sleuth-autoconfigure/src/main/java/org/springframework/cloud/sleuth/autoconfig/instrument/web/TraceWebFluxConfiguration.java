/*
 * Copyright 2013-2021 the original author or authors.
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

package org.springframework.cloud.sleuth.autoconfig.instrument.web;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.http.HttpServerHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.sleuth.instrument.web.TraceWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} enables tracing to HTTP requests with Spring WebFlux.
 *
 * @author Marcin Grzejszczak
 * @since 2.0.0
 */
@Deprecated
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
class TraceWebFluxConfiguration {

	@Bean
	TraceWebFilter traceFilter(Tracer tracer, HttpServerHandler httpServerHandler) {
		return new TraceWebFilter(tracer, httpServerHandler);
	}

	@Bean
	static TraceHandlerFunctionAdapterBeanPostProcessor traceHandlerFunctionAdapterBeanPostProcessor(
			BeanFactory beanFactory) {
		return new TraceHandlerFunctionAdapterBeanPostProcessor(beanFactory);
	}

}
