package nbc.ticketing.ticket911.domain.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import nbc.ticketing.ticket911.application.auth.service.AuthService;
import nbc.ticketing.ticket911.domain.auth.dto.request.SignInRequestDto;
import nbc.ticketing.ticket911.domain.auth.dto.response.SignInResponseDto;
import nbc.ticketing.ticket911.support.security.TestSecurityConfig;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {
	@MockitoBean
	private AuthService authService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final SignInRequestDto signInRequestDto = new SignInRequestDto("test@test.com", "Password123!");

	private final SignInResponseDto signInResponseDto = SignInResponseDto.builder()
		.accessToken("testToken")
		.build();

	@Test
	@DisplayName("로그인 성공")
	void success_signIn() throws Exception {
		// Given
		given(authService.signIn(any()))
			.willReturn(signInResponseDto);

		// When
		ResultActions perform = mockMvc.perform(post("/auth/signin")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signInRequestDto)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(200),
				jsonPath("$.message")
					.value("로그인 성공"),
				jsonPath("$.result.accessToken")
					.value(signInResponseDto.getAccessToken())
			);

		verify(authService, times(1)).signIn(any(SignInRequestDto.class));

	}

	@Test
	@DisplayName("로그아웃 성공")
	void success_signOut() throws Exception {
		// Given

		// When
		ResultActions perform = mockMvc.perform(post("/auth/signout"));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(200),
				jsonPath("$.message")
					.value("로그아웃 성공")
			);

		verify(authService, times(1)).signOut();

	}

}