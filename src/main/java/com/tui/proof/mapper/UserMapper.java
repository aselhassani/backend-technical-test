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

package com.tui.proof.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tui.proof.domain.Customer;
import com.tui.proof.dto.user.CustomerRequest;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public abstract class UserMapper {
	
	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Mapping(target = "login", expression = "java(request.getLogin().toLowerCase())")
	@Mapping(target = "email", expression = "java(request.getEmail().toLowerCase())")
	@Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
	@Mapping(target = "activated", constant = "true")
	public abstract Customer toEntity(CustomerRequest request);

}