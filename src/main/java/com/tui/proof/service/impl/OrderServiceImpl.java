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

package com.tui.proof.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tui.proof.domain.Order;
import com.tui.proof.dto.order.OrderRequest;
import com.tui.proof.dto.order.OrderResponse;
import com.tui.proof.dto.order.OrderUpdateRequest;
import com.tui.proof.dto.order.OrdersPaginatedResponse;
import com.tui.proof.dto.order.SearchRequest;
import com.tui.proof.exception.BusinessException;
import com.tui.proof.exception.ExceptionCode;
import com.tui.proof.mapper.OrderMapper;
import com.tui.proof.repository.OrderRepository;
import com.tui.proof.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderMapper mapper;

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public OrderResponse create(OrderRequest request) {
		return mapper.toDTO(orderRepository.save(this.mapper.toEntity(request)));
	}

	@Override
	public void update(OrderUpdateRequest request) {
		Order order = orderRepository.findByReference(request.getReference())
				.orElseThrow(() -> new BusinessException(ExceptionCode.NOT_FOUND, request.getReference()));
		order.setQuantity(request.getQuantity());
		checkUpdateTime(order);
		orderRepository.save(order);
	}

	private void checkUpdateTime(Order order) {
		if (LocalDateTime.now().isAfter(order.getCreatedAt().plusMinutes(5))) {
			throw new BusinessException(ExceptionCode.UPDATE_REQUEST_TIMEOUT);
		}
	}

	@Override
	public OrdersPaginatedResponse find(SearchRequest request, Pageable pageable) {
		Page<Order> result = this.orderRepository.findAll(new OrderSpecificationBuilder(request).build(), pageable);
		return new OrdersPaginatedResponse(mapper.toDTOs(result.getContent()), result.getTotalElements());
	}


}

