package nbc.ticketing.ticket911.domain.user.service;

import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.domain.user.dto.request.SignupRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.request.UpdateUserRequestDto;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDomainService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 회원 가입
	 * @param signupRequestDto 회원가입 요청(이메일, 비밀번호, 닉네임, 권한)
	 * @return 회원 가입한 유저
	 * @author 이승현
	 */
	public User createUser(SignupRequestDto signupRequestDto) {
		validateEmailExists(signupRequestDto.getEmail());

		validateNicknameExists(signupRequestDto.getNickname());

		User user = User.builder()
			.email(signupRequestDto.getEmail())
			.password(passwordEncoder.encode(signupRequestDto.getPassword()))
			.nickname(signupRequestDto.getNickname())
			.point(0)
			.roles(signupRequestDto.getRoles().stream()
				.map(UserRole::from)
				.collect(Collectors.toSet()))
			.build();

		return userRepository.save(user);
	}

	/**
	 * 유저 id로 유저 조회(삭제되었을 경우 조회되지 않음), 존재 하지 않을 경우 exception throw
	 * @param userId 유저 id
	 * @return id로 조회된 유저
	 * @throws UserException 유저가 존재하지 않음
	 * @author 이승현
	 */
	public User findActiveUserById(Long userId) {
		return userRepository.findActiveUserById(userId)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
	}

	/**
	 * 이메일 중복 검증
	 * @param email 이메일
	 * @author 이승현
	 */
	public void validateEmailExists(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new UserException(UserExceptionCode.ALREADY_EXISTS_EMAIL);
		}
	}

	/**
	 * 닉네임 중복 검증
	 * @param nickname 닉네임
	 * @author 이승현
	 */
	public void validateNicknameExists(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new UserException(UserExceptionCode.ALREADY_EXISTS_NICKNAME);
		}
	}

	/**
	 * 닉네임 또는 비밀번호 변경
	 * @param userId 유저 id
	 * @param updateUserRequestDto 수정 요청(닉네임, 비밀번호 폼(기존 비밀번호, 새 비밀번호)
	 * @return 값이 변경된 유저
	 * @author 이승현
	 */
	public User updateUserNicknameOrPassword(Long userId, UpdateUserRequestDto updateUserRequestDto) {
		User user = findActiveUserById(userId);

		if (!StringUtils.hasText(updateUserRequestDto.getNickname())
			&& updateUserRequestDto.getPasswordUpdateForm() == null) {
			throw new UserException(UserExceptionCode.NOT_CHANGED);
		}

		changeNicknameIfPresent(user, updateUserRequestDto.getNickname());

		changePasswordIfPresent(user, updateUserRequestDto.getPasswordUpdateForm());

		return user;
	}

	/**
	 * 닉네임 요청값이 존재하면 변경
	 * @param user 유저
	 * @param newNickname 새 닉네임
	 * @author 이승현
	 */
	public void changeNicknameIfPresent(User user, String newNickname) {
		if (!StringUtils.hasText(newNickname)) {
			return;
		}

		validateNicknameExists(newNickname);

		user.changeNickname(newNickname);
	}

	/**
	 * 비밀번호 요청값이 존재하면 변경
	 * @param user 유저
	 * @param passwordUpdateForm 비밀번호 변경 폼(기존 비밀번호, 새 비밀번호)
	 * @author 이승현
	 */
	public void changePasswordIfPresent(User user, UpdateUserRequestDto.PasswordUpdateForm passwordUpdateForm) {
		if (passwordUpdateForm == null) {
			return;
		}

		validatePassword(passwordUpdateForm.getOldPassword(), user.getPassword());

		user.changePassword(passwordEncoder.encode(passwordUpdateForm.getNewPassword()));
	}

	/**
	 * 비밀번호 검증
	 * @param rawPassword     암호화되지 않은 비밀번호
	 * @param encodedPassword 암호화된 비밀번호
	 * @author 이승현
	 */
	public void validatePassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new UserException(UserExceptionCode.WRONG_PASSWORD);
		}
	}

	/**
	 * 회원 탈퇴
	 * @param userId 유저 id
	 * @return 탈퇴한 유저 id
	 * @author 이승현
	 */
	public Long withdrawUser(Long userId) {
		User user = findActiveUserById(userId);

		user.withdraw();

		return user.getId();
	}

	/**
	 * 포인트 충전
	 * @param user 유저
	 * @param point 포인트 값
	 * @return 포인트가 충전된 유저
	 * @author 이승현
	 */
	public User chargePoint(User user, int point) {
		user.addPoint(point);

		return user;
	}

	/**
	 * 포인트 차감
	 * @param user 유저
	 * @param point 포인트
	 * @return 포인트가 차감된 유저
	 * @author 이승현
	 */
	public User minusPoint(User user, int point) {
		if (user.getPoint() < point) {
			throw new UserException(UserExceptionCode.NOT_ENOUGH_POINT);
		}

		user.minusPoint(point);

		return user;
	}

	/**
	 * 이메일로 유저 조회
	 * @param email 이메일
	 * @return 이메일로 조회된 유저
	 * @author 이승현
	 */
	public User findUserByEmailOrElseThrow(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
	}

}
