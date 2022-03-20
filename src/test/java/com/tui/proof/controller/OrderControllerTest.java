/* Copyright (c) 2022 Ali Saidi Elhassani
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

package com.tui.proof.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.tui.proof.dto.order.OrderResponse;
import com.tui.proof.dto.order.OrderUpdateRequest;
import com.tui.proof.security.AuthoritiesConstants;
import com.tui.proof.service.OrderService;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest extends AbstractRestControllerTest {

	private static final List<Integer> QUANTITIES = Arrays.asList(5, 10, 15);

	private static final String TEST_USER_LOGIN = "test";

	@MockBean
	private OrderService orderService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testCreateOrder() throws Exception {
		Map<String, Object> request = new HashMap<>();
		request.put("city", "Oujda");
		request.put("country", "Morocco");
		request.put("email", "mgemmons@att.net");
		request.put("phoneNumber", "+212623967309");
		request.put("postcode", "40000");
		request.put("quantity", 10);
		request.put("street", "Zone Industrielle lot. n°3, L'Oriental");

		given(this.orderService.create(any())).willReturn(new OrderResponse());

		this.mockMvc.perform(post("/api/v1/orders").content(asJsonString(request))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
	}

	@Test
	void testFailToCreateOrderIfMalformedRequest() throws Exception {
		Map<String, Object> request = new HashMap<>();
		request.put("city", "Oujda");
		request.put("country", "Morocco");
		request.put("email", "mgemmonsatt.net");
		request.put("phoneNumber", "+212623967309");
		request.put("postcode", "40000");
		request.put("quantity", 8);
		request.put("street", "Zone Industrielle lot. n°3, L'Oriental");

		this.mockMvc
				.perform(post("/api/v1/orders").content(asJsonString(request)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("must be a well-formed email address")))
				.andExpect(content().string(containsString("must be a valid quantity : 5, 10 or 15")));
	}

	@Test
	void testUpdateOrder() throws Exception {
		OrderUpdateRequest request = new OrderUpdateRequest();
		request.setQuantity(getValidQty());
		request.setReference("1-20223006-001");

		doNothing().when(this.orderService).update(request);

		this.mockMvc
				.perform(put("/api/v1/orders").content(asJsonString(request)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(value = TEST_USER_LOGIN, authorities = { AuthoritiesConstants.ADMIN })
	void testFindOrdersByCustomerData() throws Exception {
		Map<String, String> request = new HashMap<>();
		request.put("email", "mgemmons@att.net");
		request.put("firstName", "Mimoun");
		request.put("lastName", "Joumari");
		request.put("phoneNumber", "+212623967309");

		Set<String> keys = request.keySet();
		String params = "";
		for (String key : keys) {
			params += "&" + key + "=" + request.get(key);
		}

		this.mockMvc.perform(
				get("/api/v1/orders?" + params).content(asJsonString(request)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	/**
	 * Gets the valid qty.
	 *
	 * @return the valid qty
	 */
	private int getValidQty() {
		return QUANTITIES.get(new Random().nextInt(QUANTITIES.size()));
	}

}
