package nbc.ticketing.ticket911.domain.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

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

import nbc.ticketing.ticket911.application.user.service.UserService;
import nbc.ticketing.ticket911.domain.user.dto.request.SignupRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.request.UpdateUserRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.response.UserResponseDto;
import nbc.ticketing.ticket911.support.security.TestSecurityConfig;
import nbc.ticketing.ticket911.support.security.WithCustomMockUser;

@Import(TestSecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerTest {
	@MockitoBean
	private UserService userService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final SignupRequestDto signupRequestDto = new SignupRequestDto("test@test.com", "Password123!",
		"test", Set.of("ROLE_USER"));

	private final UserResponseDto userResponseDto = UserResponseDto.builder()
		.email("test@test.com")
		.nickname("test")
		.point(100)
		.build();

	private final UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto("newTest",
		new UpdateUserRequestDto.PasswordUpdateForm("oldPassword", "newPassword1!"));

	@Test
	@DisplayName("회원가입 성공")
	void success_signUp() throws Exception {
		// Given
		given(userService.signUp(any()))
			.willReturn(userResponseDto);

		// When
		ResultActions perform = mockMvc.perform(post("/users/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signupRequestDto)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isCreated(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(201),
				jsonPath("$.message")
					.value("회원가입 성공"),
				jsonPath("$.result.email")
					.value(userResponseDto.getEmail()),
				jsonPath("$.result.nickname")
					.value(userResponseDto.getNickname())
			);

		verify(userService, times(1)).signUp(any(SignupRequestDto.class));

	}

	@Test
	@WithCustomMockUser
	@DisplayName("유저 본인 정보 조회 성공")
	void success_getMyProfile() throws Exception {
		// Given
		given(userService.getUser(anyLong()))
			.willReturn(userResponseDto);

		// When
		ResultActions perform = mockMvc.perform(get("/users"));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(200),
				jsonPath("$.message")
					.value("본인 정보 조회 성공"),
				jsonPath("$.result.email")
					.value(userResponseDto.getEmail()),
				jsonPath("$.result.nickname")
					.value(userResponseDto.getNickname())
			);

		verify(userService, times(1)).getUser(anyLong());

	}

	@Test
	@WithCustomMockUser
	@DisplayName("유저 본인 정보 수정 성공")
	void success_updateMyProfile() throws Exception {
		// Given
		given(userService.updateUser(anyLong(), any()))
			.willReturn(userResponseDto);

		// When
		ResultActions perform = mockMvc.perform(patch("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateUserRequestDto)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(200),
				jsonPath("$.message")
					.value("본인 정보 수정 성공"),
				jsonPath("$.result.email")
					.value(userResponseDto.getEmail()),
				jsonPath("$.result.nickname")
					.value(userResponseDto.getNickname())
			);

		verify(userService, times(1)).updateUser(anyLong(), any(UpdateUserRequestDto.class));

	}

	@Test
	@WithCustomMockUser
	@DisplayName("유저 탈퇴 성공")
	void success_withdrawal() throws Exception {
		// Given
		given(userService.withdrawUser(anyLong()))
			.willReturn(1L);

		// When
		ResultActions perform = mockMvc.perform(delete("/users"));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(200),
				jsonPath("$.message")
					.value("회원 탈퇴 성공"),
				jsonPath("$.result")
					.value(1L)
			);

		verify(userService, times(1)).withdrawUser(anyLong());

	}

	@Test
	@WithCustomMockUser
	@DisplayName("포인트 충전 성공")
	void success_chargePoint() throws Exception {
		// Given
		given(userService.chargePoint(anyLong(), anyInt()))
			.willReturn(userResponseDto);
		int point = 100;

		// When
		ResultActions perform = mockMvc.perform(put("/users/charge/{point}", point));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(200),
				jsonPath("$.message")
					.value("포인트 충전 성공"),
				jsonPath("$.result.point")
					.value(point)
			);

		verify(userService, times(1)).chargePoint(anyLong(), anyInt());

	}

}