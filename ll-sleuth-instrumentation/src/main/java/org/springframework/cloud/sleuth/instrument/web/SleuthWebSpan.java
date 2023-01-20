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

package org.springframework.cloud.sleuth.instrument.web;

import io.micrometer.common.docs.KeyName;

class SleuthWebSpan {

	/**
	 * Tags related to web.
	 *
	 * @author Marcin Grzejszczak
	 * @since 3.0.3
	 */
	enum Tags implements KeyName {

		/**
		 * Name of the class that is processing the request.
		 */
		CLASS {
			@Override
			public String asString() {
				return "mvc.controller.class";
			}
		},

		/**
		 * Name of the method that is processing the request.
		 */
		METHOD {
			@Override
			public String asString() {
				return "mvc.controller.method";
			}
		}
	}

}
