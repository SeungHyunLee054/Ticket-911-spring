package nbc.ticketing.ticket911.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.common.response.CommonResponse;
import nbc.ticketing.ticket911.domain.auth.application.AuthService;
import nbc.ticketing.ticket911.domain.auth.dto.request.SignInRequestDto;
import nbc.ticketing.ticket911.domain.auth.dto.response.SignInResponseDto;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/signin")
	public ResponseEntity<CommonResponse<SignInResponseDto>> signIn(
		@Valid @RequestBody SignInRequestDto signInRequestDto
	) {
		SignInResponseDto responseDto = authService.signIn(signInRequestDto);

		return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "로그인 성공", responseDto));
	}

	@PostMapping("/signout")
	public ResponseEntity<CommonResponse<Void>> signOut() {
		authService.signOut();

		return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "로그아웃 성공"));
	}
}
