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

import static com.tui.proof.exception.ExceptionCode.INTERNAL_ERROR;
import static org.slf4j.helpers.MessageFormatter.arrayFormat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tui.proof.dto.common.ValidationError;
import com.tui.proof.exception.BusinessException;;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	private final Logger log = LoggerFactory.getLogger(ControllerAdvice.class);

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", ex.getCode().getStatus());
		body.put("error", getMessage(ex.getCode().name(), ex.getArgs()));

		log.warn(getMessage(ex.getCode().name()), ex);

		return ResponseEntity.status(ex.getCode().getStatus()).body(body);
	}
	
	@ExceptionHandler({ BadCredentialsException.class })
	public ResponseEntity<Object> handleBadCredentials(Exception ex, WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", UNAUTHORIZED);
		body.put("error", getMessage(ex.getMessage()));

		log.warn(ex.getMessage(), ex);

		return ResponseEntity.status(UNAUTHORIZED).body(body);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", INTERNAL_ERROR.getStatus());
		body.put("error", getMessage(INTERNAL_ERROR.name()));

		log.warn(ex.getMessage(), ex);

		return ResponseEntity.status(INTERNAL_ERROR.getStatus()).body(body);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());

		List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> new ValidationError(err.getField(), err.getRejectedValue(), err.getDefaultMessage()))
				.collect(Collectors.toList());

		body.put("errors", errors);

		log.warn(ex.getMessage(), ex);

		return ResponseEntity.status(BAD_REQUEST).body(body);
	}

	private String getMessage(String key, String... args) {
		return arrayFormat(getMessage(key), args).getMessage();
	}

	private String getMessage(String key) {
		return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
	}
}