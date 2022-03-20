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

package com.tui.proof.hooks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tui.proof.domain.Order;

@Component
public class OrderListener {

	@Value("${pilotes.unitprice}")
	private String unitPrice;

	@PrePersist
	public void prePersist(Order order) {
		initializeOrderRef(order);
		setOrderPrice(order);
	}

	@PreUpdate
	public void preUpdate(Order order) {
		setOrderPrice(order);
	}

	private void setOrderPrice(Order order) {
		order.setTotalPrice((float) (order.getQuantity() * Float.parseFloat(unitPrice)));
	}

	private void initializeOrderRef(Order order) {
		String sequence = String.valueOf(
				1 + Optional.ofNullable(order.getCustomer().getOrders()).orElse(Collections.emptyList()).size());
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddyyyy"));
		String customerId = String.valueOf(order.getCustomer().getId());
		order.setReference(String.join("-", customerId, date, strLeftPad(sequence, 3)));
	}

	private String strLeftPad(String str, int length) {
		return String.format("%1$" + length + "s", str).replace(' ', '0');
	}


}
