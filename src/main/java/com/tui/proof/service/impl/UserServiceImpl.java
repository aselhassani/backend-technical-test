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

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tui.proof.domain.Authority;
import com.tui.proof.domain.User;
import com.tui.proof.dto.user.CustomerRequest;
import com.tui.proof.exception.BusinessException;
import com.tui.proof.exception.ExceptionCode;
import com.tui.proof.mapper.UserMapper;
import com.tui.proof.repository.AuthorityRepository;
import com.tui.proof.repository.UserRepository;
import com.tui.proof.security.AuthoritiesConstants;
import com.tui.proof.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private UserMapper mapper;

	@Override
	public void registerUser(CustomerRequest request) {

		User user = mapper.toEntity(request);

		userRepository.findOneByLogin(user.getLogin())
		.ifPresent(exsistingUser -> {
			throw new BusinessException(ExceptionCode.USERNAME_ALREADY_USED, exsistingUser.getLogin());
		});
		
		userRepository.findOneByEmailIgnoreCase(user.getEmail())
		.ifPresent(exsistingUser -> {
					throw new BusinessException(ExceptionCode.EMAIL_ALREADY_USED, exsistingUser.getEmail());
		});

		Set<Authority> authorities = new HashSet<>();
		authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
		user.setAuthorities(authorities);

		userRepository.save(user);
		
	}
	
	

}
