package nbc.ticketing.ticket911.domain.stage.controller;

import nbc.ticketing.ticket911.domain.stage.dto.response.StageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.stage.service.StageService;
import nbc.ticketing.ticket911.common.response.CommonResponse;
import nbc.ticketing.ticket911.domain.stage.dto.request.CreateStageRequestDto;

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
	public ResponseEntity<CommonResponse<Page<StageResponseDto>>> getStages(
		@RequestParam(defaultValue = "") String keyword,
		@PageableDefault(size = 10, sort = "stageName", direction = Sort.Direction.ASC) Pageable pageable
	) {
		Page<StageResponseDto> stageResponseDtos = stageService.getStages(keyword, pageable);

		return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "전체 공연장 조회 성공", stageResponseDtos));
	}

	@GetMapping("/{stageId}")
	public ResponseEntity<CommonResponse<StageResponseDto>> getStage(
			@PathVariable Long stageId
	){
		StageResponseDto stageResponseDto = stageService.getStage(stageId);

		return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "공연장 단건 조회 성공", stageResponseDto));
	}

}
