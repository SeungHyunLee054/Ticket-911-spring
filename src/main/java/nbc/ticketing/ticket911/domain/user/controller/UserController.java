package nbc.ticketing.ticket911.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.user.service.UserService;
import nbc.ticketing.ticket911.common.response.CommonResponse;
import nbc.ticketing.ticket911.domain.user.dto.request.SignupRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.response.UserResponseDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<CommonResponse<UserResponseDto>> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
		UserResponseDto responseDto = userService.signUp(signupRequestDto);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(CommonResponse.of(true, HttpStatus.CREATED.value(), "회원가입 성공", responseDto));
	}
}
