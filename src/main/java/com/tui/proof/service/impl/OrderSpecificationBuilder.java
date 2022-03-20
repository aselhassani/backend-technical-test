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

import static org.springframework.data.jpa.domain.Specification.where;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tui.proof.domain.Order;
import com.tui.proof.dto.order.SearchRequest;
import com.tui.proof.repository.OrderSpecification;

import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class OrderSpecificationBuilder {

	private final SearchRequest request;

	public Specification<Order> build() {

		ObjectMapper oMapper = new ObjectMapper();
		Map<String, String> criteria = oMapper.convertValue(request, Map.class);
		
		List<Specification<Order>> specs = criteria.entrySet().stream().filter(e -> (e.getValue() != null))
				.map(e -> new OrderSpecification(e.getKey(), e.getValue())).collect(Collectors.toList());

		if (specs.size() == 0)
			return null;

		Specification<Order> result = specs.get(0);

		for (int i = 1; i < specs.size(); i++) {
			result = where(result).and(specs.get(i));
		}
		return result;
	}

}
