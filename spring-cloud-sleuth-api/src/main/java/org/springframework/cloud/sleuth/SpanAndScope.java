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

package org.springframework.cloud.sleuth;

/**
 * Container object for {@link Span} and its corresponding {@link Tracer.SpanInScope}.
 *
 * @author Marcin Grzejszczak
 * @since 3.1.0
 */
public class SpanAndScope {

	private final Span span;

	private final Tracer.SpanInScope scope;

	public SpanAndScope(Span span, Tracer.SpanInScope scope) {
		this.span = span;
		this.scope = scope;
	}

	public Span getSpan() {
		return this.span;
	}

	public Tracer.SpanInScope getScope() {
		return this.scope;
	}

}
