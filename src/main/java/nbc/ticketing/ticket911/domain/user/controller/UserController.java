package nbc.ticketing.ticket911.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.user.service.UserService;
import nbc.ticketing.ticket911.common.response.CommonResponse;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.user.dto.request.SignupRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.request.UpdateUserRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.response.UserResponseDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<CommonResponse<UserResponseDto>> signup(
		@Valid @RequestBody SignupRequestDto signupRequestDto
	) {
		UserResponseDto responseDto = userService.signUp(signupRequestDto);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(CommonResponse.of(true, HttpStatus.CREATED.value(), "회원가입 성공", responseDto));
	}

	@GetMapping
	public ResponseEntity<CommonResponse<UserResponseDto>> getMyProfile(@AuthenticationPrincipal AuthUser authUser) {
		UserResponseDto responseDto = userService.getUser(authUser.getId());

		return ResponseEntity.ok(CommonResponse
			.of(true, HttpStatus.OK.value(), "본인 정보 조회 성공", responseDto));
	}

	@PatchMapping
	public ResponseEntity<CommonResponse<UserResponseDto>> updateMyProfile(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid UpdateUserRequestDto updateUserRequestDto
	) {
		UserResponseDto responseDto = userService.updateUser(authUser.getId(), updateUserRequestDto);

		return ResponseEntity.ok(CommonResponse
			.of(true, HttpStatus.OK.value(), "본인 정보 수정 성공", responseDto));
	}

	@DeleteMapping
	public ResponseEntity<CommonResponse<Long>> withdrawal(@AuthenticationPrincipal AuthUser authUser) {
		Long withdrawnUserId = userService.withdrawUser(authUser.getId());

		return ResponseEntity.ok(CommonResponse
			.of(true, HttpStatus.OK.value(), "회원 탈퇴 성공", withdrawnUserId));
	}

	@PutMapping("/charge/{point}")
	public ResponseEntity<CommonResponse<UserResponseDto>> chargePoint(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable(name = "point") @Positive int point
	) {
		UserResponseDto responseDto = userService.chargePoint(authUser.getId(), point);

		return ResponseEntity.ok(CommonResponse
			.of(true, HttpStatus.OK.value(), "포인트 충전 성공", responseDto));
	}
}
