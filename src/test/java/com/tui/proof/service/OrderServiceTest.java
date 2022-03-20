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

package com.tui.proof.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tui.proof.domain.Address;
import com.tui.proof.domain.Customer;
import com.tui.proof.domain.Order;
import com.tui.proof.dto.order.OrderRequest;
import com.tui.proof.dto.order.OrderUpdateRequest;
import com.tui.proof.dto.order.OrdersPaginatedResponse;
import com.tui.proof.dto.order.SearchRequest;
import com.tui.proof.enums.Country;
import com.tui.proof.exception.BusinessException;
import com.tui.proof.repository.CustomerRepository;
import com.tui.proof.repository.OrderRepository;

@SpringBootTest
public class OrderServiceTest {

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	public void setup() {
		this.customerRepository.deleteAll();
	}

	@Test
	public void testCreateOrder() {
		createCustomer();
		OrderRequest request = new OrderRequest();
		request.setCity("Marrakesh");
		request.setCountry("Morocco");
		request.setEmail("mgemmons@att.net");
		request.setPostcode("40000");
		request.setQuantity(10);
		request.setStreet("48, bd. Moulay Ismail, arset Al Bilk");
		orderService.create(request);
	}

	@Test
	public void testFailToCreateOrderIfCustomerNotFound() {
		createCustomer();
		OrderRequest request = new OrderRequest();
		request.setCity("Marrakesh");
		request.setCountry("MA");
		request.setEmail("beenter87@einrot.com");
		request.setPostcode("40000");
		request.setQuantity(10);
		request.setStreet("48, bd. Moulay Ismail, arset Al Bilk");
		assertThatExceptionOfType(BusinessException.class).isThrownBy(() -> orderService.create(request));
	}

	@Test
	public void testUpdateOrder() {
		Order order = new Order();
		order.setDeliveryAddress(
				new Address(null, "48, bd. Moulay Ismail, arset Al Bilk", "40000", "Marrakesh", Country.MA));
		order.setCustomer(createCustomer());
		order.setQuantity(10);
		order = orderRepository.save(order);
		OrderUpdateRequest request = new OrderUpdateRequest();
		request.setQuantity(10);
		request.setReference(order.getReference());
		orderService.update(request);
	}

	@Test
	public void testFindByCustomerData() {
		Order order = new Order();
		order.setDeliveryAddress(
				new Address(null, "48, bd. Moulay Ismail, arset Al Bilk", "40000", "Marrakesh", Country.MA));
		order.setCustomer(createCustomer());
		order.setQuantity(10);
		orderRepository.save(order);

		SearchRequest request = new SearchRequest();
		request.setEmail("mgemmons"); // email contains
		request.setPhoneNumber("77"); // phone starts with
		request.setLastName("oumar"); // lastname contains
		request.setFirstName("moun"); // firsttname equals

		OrdersPaginatedResponse response = orderService.find(request, PageRequest.of(0, 10));

		assertThat(response.getOrders()).hasSize(1);
		assertThat(response.getTotalElements()).isEqualTo(1);
	}

	private Customer createCustomer() {
		Customer c = new Customer();
		c.setEmail("mgemmons@att.net");
		c.setLogin("mgemmons");
		c.setPassword(passwordEncoder.encode("admin"));
		c.setFirstName("Mimoun");
		c.setLastName("Joumari");
		c.setPhoneNumber("+2126435577");
		c.setActivated(true);
		return customerRepository.save(c);
	}

}
