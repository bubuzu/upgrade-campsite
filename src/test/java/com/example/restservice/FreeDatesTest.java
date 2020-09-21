/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.restservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FreeDatesTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void FreeDatesGetSingleDateSuccess() throws Exception {
		this.mockMvc.perform(get("/free-dates")
			.param("from", "2010-01-15")
			.param("to", "2010-01-15"))
			.andDo(print()).andExpect(status().isOk())
			.andExpect(content().string("{\"freeDates\":[\"2010-01-15T08:00:00.000+00:00\"]}"));
	}

	@Test
	public void FreeDatesGetMonthSuccess() throws Exception {
		this.mockMvc.perform(get("/free-dates")
			.param("from", "2010-01-15"))
			.andDo(print()).andExpect(status().isOk())
			.andExpect(jsonPath("$.freeDates.length()", is(31)));
	}

	@Test
	public void FreeDatesErrorNoFromDate() throws Exception {
		this.mockMvc.perform(get("/free-dates"))
			.andDo(print()).andExpect(status().is4xxClientError());
	}

	@Test
	public void FreeDatesErrorFromBeforeToDate() throws Exception {
		this.mockMvc.perform(get("/free-dates")
			.param("from", "2010-01-16")
			.param("to", "2010-01-15"))
			.andDo(print()).andExpect(status().is4xxClientError());
	}
}
