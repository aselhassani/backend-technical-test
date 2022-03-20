package com.tui.proof.mapper;

import java.util.List;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.tui.proof.domain.Order;
import com.tui.proof.dto.order.OrderRequest;
import com.tui.proof.dto.order.OrderResponse;
import com.tui.proof.service.CustomerService;

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

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public abstract class OrderMapper {

	@Autowired
	protected CustomerService customerService;
	
	@Mapping(target = "deliveryAddress.city", source = "city")
	@Mapping(target = "deliveryAddress.postcode", source = "postcode")
	@Mapping(target = "deliveryAddress.street", source = "street")
	@Mapping(target = "customer", expression = "java(customerService.findByEmail(orderRequest.getEmail()))")
	@Mapping(target = "deliveryAddress.country", expression = "java(com.tui.proof.enums.Country.fromValue(orderRequest.getCountry()))")
	public abstract Order toEntity(OrderRequest orderRequest);

	@Mapping(target = "country", source = "deliveryAddress.country")
	@Mapping(target = "city", source = "deliveryAddress.city")
	@Mapping(target = "postcode", source = "deliveryAddress.postcode")
	@Mapping(target = "street", source = "deliveryAddress.street")
	@Mapping(target = "totalPrice", source = "totalPrice", numberFormat = "#.00 EUR")
	@Mapping(target = "createdAt", source = "createdAt", dateFormat = "MM-dd-yyyy HH:mm")
	public abstract OrderResponse toDTO(Order order);

	public abstract List<OrderResponse> toDTOs(List<Order> orders);


}