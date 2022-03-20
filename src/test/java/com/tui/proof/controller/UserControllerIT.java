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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.tui.proof.domain.User;
import com.tui.proof.dto.user.LoginRequest;
import com.tui.proof.repository.UserRepository;

@WithMockUser(value = "test")
class UserControllerIT extends AbstractRestControllerTest {

    static final String TEST_USER_LOGIN = "test";

    @Autowired
    private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @Transactional
    void testRegisterValid() throws Exception {
        Map<String, String> validUser = new HashMap<>();
        validUser.put("login", "test-register-valid");
        validUser.put("password", "password");
        validUser.put("firstName", "Alice");
        validUser.put("lastName", "Test");
        validUser.put("email", "test-register-valid@example.com");
		validUser.put("phoneNumber", "+2127667575");
        
        assertThat(userRepository.findOneByLogin("test-register-valid")).isEmpty();

        mockMvc
				.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(asJsonString(validUser)))
            .andExpect(status().isCreated());

        assertThat(userRepository.findOneByLogin("test-register-valid")).isPresent();
    }

    @Test
    @Transactional
    void testRegisterInvalidLogin() throws Exception {
        Map<String, String> invalidUser = new HashMap<>();
        invalidUser.put("login", "funky-log(n"); // <-- invalid
        invalidUser.put("password", "password");
        invalidUser.put("firstName", "Alice");
        invalidUser.put("lastName", "Test");
        invalidUser.put("email", "test-register-valid@example.com");

        mockMvc
				.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByEmailIgnoreCase("funky@example.com");
        assertThat(user).isEmpty();
    }

    @Test
    @Transactional
    void testRegisterInvalidEmail() throws Exception {
    	Map<String, String> invalidUser = new HashMap<>();
    	invalidUser.put("login", "test-register-valid-mail");
    	invalidUser.put("password", "password");
    	invalidUser.put("firstName", "Alice");
    	invalidUser.put("lastName", "Test");
    	invalidUser.put("email", "invalid"); // <-- invalid
		invalidUser.put("phoneNumber", "+2127667575");
        mockMvc
				.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user).isEmpty();
    }

	/**
	 * Test register invalid password.
	 *
	 * @throws Exception the exception
	 */
    @Test
    @Transactional
    void testRegisterInvalidPassword() throws Exception {
    	Map<String, String> invalidUser = new HashMap<>();
    	invalidUser.put("login", "test-register-valid");
    	invalidUser.put("password", "123"); // password with only 3 digits
    	invalidUser.put("firstName", "Alice");
    	invalidUser.put("lastName", "Test");
    	invalidUser.put("email", "test-register-valid@example.com");
        
        mockMvc
				.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user).isEmpty();
    }

    @Test
    @Transactional
    void testRegisterNullPassword() throws Exception {
    	Map<String, String> invalidUser = new HashMap<>();
    	invalidUser.put("login", "test-register-valid");
    	invalidUser.put("password", null); // null password
    	invalidUser.put("firstName", "Alice");
    	invalidUser.put("lastName", "Test");
    	invalidUser.put("email", "test-register-valid@example.com");
		invalidUser.put("phoneNumber", "+2127667575");

        mockMvc
				.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user).isEmpty();
    }

    @Test
    @Transactional
    void testRegisterDuplicateLogin() throws Exception {
        // First registration
    	 Map<String, String> firstUser = new HashMap<>();
			firstUser.put("login", "test-register-valid");
    	 firstUser.put("password", "password");
    	 firstUser.put("firstName", "Alice");
    	 firstUser.put("lastName", "Test");
    	 firstUser.put("email", "test-register-valid@example.com");
			firstUser.put("phoneNumber", "+2127667575");

        // Duplicate login, different email
         Map<String, String> secondUser = new HashMap<>();
		 secondUser.put("login", "test-register-valid");
         secondUser.put("password", "password");
         secondUser.put("firstName", "Alice");
         secondUser.put("lastName", "Test");
         secondUser.put("email", "test-register-duplicated@example.com");
		 secondUser.put("phoneNumber", "+2127667575");

        // First user
        mockMvc
				.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(asJsonString(firstUser)))
            .andExpect(status().isCreated());

        // Second user
        mockMvc
				.perform(
						post("/api/register").contentType(MediaType.APPLICATION_JSON).content(asJsonString(secondUser)))
            .andExpect(status().is4xxClientError());

    }

	@Test
	@Transactional
	void testAuthenticate() throws Exception {
		User user = new User();
		user.setLogin("user-jwt-controller");
		user.setEmail("user-jwt-controller@example.com");
		user.setActivated(true);
		user.setPassword(passwordEncoder.encode("test"));
		userRepository.saveAndFlush(user);

		LoginRequest login = new LoginRequest();
		login.setUsername("user-jwt-controller");
		login.setPassword("test");

		mockMvc.perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(asJsonString(login)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.token").isString())
				.andExpect(jsonPath("$.token").isNotEmpty());

	}

	@Test
	@Transactional
	void testFailToAuthenticateIfBadCredentials() throws Exception {
		LoginRequest login = new LoginRequest();
		login.setUsername("invalid-login");
		login.setPassword("invalid-password");

		mockMvc.perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(asJsonString(login)))
				.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.id_token").doesNotExist());

	}

}

