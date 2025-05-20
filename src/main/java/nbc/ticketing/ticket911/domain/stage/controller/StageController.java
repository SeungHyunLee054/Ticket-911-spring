package nbc.ticketing.ticket911.domain.stage.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.stage.service.StageService;
import nbc.ticketing.ticket911.common.response.CommonResponse;
import nbc.ticketing.ticket911.domain.stage.dto.request.CreateStageRequestDto;
import nbc.ticketing.ticket911.domain.stage.dto.request.UpdateStageRequestDto;
import nbc.ticketing.ticket911.domain.stage.dto.response.StageResponseDto;

@RestController
@RequestMapping("/stages")
@RequiredArgsConstructor
public class StageController {

	private final StageService stageService;

	@PostMapping
	public ResponseEntity<CommonResponse<StageResponseDto>> createStage(
		@Valid @RequestBody CreateStageRequestDto createStageRequestDto
	) {
		StageResponseDto stageResponseDto = stageService.createStage(createStageRequestDto);

		return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "공연장 생성 성공", stageResponseDto));
	}

	@GetMapping
	public ResponseEntity<CommonResponse<StageResponseDto>> getStages(
		@RequestParam(defaultValue = "") String keyword,
		@PageableDefault(size = 10, sort = "stageName", direction = Sort.Direction.ASC) Pageable pageable
	) {
		Page<StageResponseDto> stageResponseDtos = stageService.getStages(keyword, pageable);

		return ResponseEntity.ok(CommonResponse.ofPage(true, HttpStatus.OK.value(), "전체 공연장 조회 성공", stageResponseDtos));
	}

	@GetMapping("/{stageId}")
	public ResponseEntity<CommonResponse<StageResponseDto>> getStage(
		@PathVariable Long stageId
	) {
		StageResponseDto stageResponseDto = stageService.getStage(stageId);

		return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "공연장 단건 조회 성공", stageResponseDto));
	}

	@PatchMapping("/{stageId}")
	public ResponseEntity<CommonResponse<StageResponseDto>> updateStage(
		@PathVariable Long stageId,
		@RequestBody @Valid UpdateStageRequestDto updateStageRequestDto
	) {
		StageResponseDto stageResponseDto = stageService.updateStage(stageId, updateStageRequestDto);

		return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "공연장 수정 성공", stageResponseDto));
	}

}
