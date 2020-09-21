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

import com.example.restservice.util.DateHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void BookAndCancelSuccessTest() throws Exception {
		String from = dateToString(DateHelper.incrementDate(new Date(), 2));
		String to = dateToString(DateHelper.incrementDate(new Date(), 3));

		String bookingId = bookSuccessfully(from, to);
		cancelBooking(bookingId);
	}

	@Test
	public void BookingCancelIfThereIsAnotherBooking() throws Exception {
		String from = dateToString(DateHelper.incrementDate(new Date(), 4));
		String to = dateToString(DateHelper.incrementDate(new Date(), 4));
		String bullockBooking = bookSuccessfully(from, to);

		from = dateToString(DateHelper.incrementDate(new Date(), 3));
		to = dateToString(DateHelper.incrementDate(new Date(), 5));
		this.mockMvc.perform(post("/book")
			.param("from", from)
			.param("to", to)
			.param("name", "Keanu Reeves"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(false)));
		cancelBooking(bullockBooking);
	}

	@Test
	public void BookingFailMissingName() throws Exception {
		String from = dateToString(DateHelper.incrementDate(new Date(), 4));
		String to = dateToString(DateHelper.incrementDate(new Date(), 4));
		this.mockMvc.perform(post("/book")
			.param("from", from)
			.param("to", to))
			.andDo(print()).andExpect(status().is4xxClientError());
	}

	@Test
	public void BookingFailMissingDateTo() throws Exception {
		String from = dateToString(DateHelper.incrementDate(new Date(), 4));
		this.mockMvc.perform(post("/book")
			.param("from", from)
			.param("name", "Keanu Reeves"))
			.andDo(print()).andExpect(status().is4xxClientError());
	}

	@Test
	public void BookingFailMissingDateFrom() throws Exception {
		String to = dateToString(DateHelper.incrementDate(new Date(), 4));
		this.mockMvc.perform(post("/book")
			.param("to", to)
			.param("name", "Keanu Reeves"))
			.andDo(print()).andExpect(status().is4xxClientError());
	}

	@Test
	public void BookingFailDateFromBeforeTo() throws Exception {
		String to = dateToString(DateHelper.incrementDate(new Date(), 4));
		String from = dateToString(DateHelper.incrementDate(new Date(), 5));
		this.mockMvc.perform(post("/book")
			.param("from", from)
			.param("to", to)
			.param("name", "Keanu Reeves"))
			.andDo(print()).andExpect(status().is4xxClientError());
	}

	@Test
	public void BookingFailToEarly() throws Exception {
		String from = dateToString(new Date());
		String to = dateToString(DateHelper.incrementDate(new Date(), 3));
		this.mockMvc.perform(post("/book")
			.param("from", from)
			.param("to", to)
			.param("name", "Keanu Reeves"))
			.andDo(print()).andExpect(status().is4xxClientError());
	}

	@Test
	public void BookingFailToLate() throws Exception {
		String from = dateToString(DateHelper.incrementDate(new Date(), 50));
		String to = dateToString(DateHelper.incrementDate(new Date(), 51));
		this.mockMvc.perform(post("/book")
			.param("from", from)
			.param("to", to)
			.param("name", "Keanu Reeves"))
			.andDo(print()).andExpect(status().is4xxClientError());
	}

	@Test
	public void BookingFailIntervalTooBig() throws Exception {
		String from = dateToString(DateHelper.incrementDate(new Date(), 2));
		String to = dateToString(DateHelper.incrementDate(new Date(), 6));
		this.mockMvc.perform(post("/book")
			.param("from", from)
			.param("to", to)
			.param("name", "Keanu Reeves"))
			.andDo(print()).andExpect(status().is4xxClientError());
	}

	@Test
	public void CancelBookingErrorTest() throws Exception {
		this.mockMvc.perform(post("/booking/cancel")
			.param("bookingId", "WrongId"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(false)));
	}

	private void cancelBooking(String bookingId) throws Exception {
		this.mockMvc.perform(post("/booking/cancel")
			.param("bookingId", bookingId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)));
	}

	private String bookSuccessfully(String from, String to) throws Exception {
		ResultActions resultActions = this.mockMvc.perform(post("/book")
			.param("from", from)
			.param("to", to)
			.param("name", "Sandra Bullock"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)));

		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		JSONObject obj = new JSONObject(contentAsString);
		return obj.getJSONObject("booking").getString("id");
	}

	private String dateToString(final Date date){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}

}
