package nbc.ticketing.ticket911.domain.stage.controller;

import jakarta.validation.Valid;
import nbc.ticketing.ticket911.application.stage.service.StageService;
import nbc.ticketing.ticket911.common.response.CommonResponse;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.stage.dto.request.CreateStageRequestDto;
import nbc.ticketing.ticket911.domain.stage.dto.response.CreateStageResponseDto;
import nbc.ticketing.ticket911.domain.stage.dto.response.GetStageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    @PostMapping
    public ResponseEntity<CommonResponse<CreateStageResponseDto>> createStage(
            @Valid @RequestBody CreateStageRequestDto createStageRequestDto
    ){
        CreateStageResponseDto createStageResponseDto = stageService.createStage(createStageRequestDto);

        return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "공연장 생성 성공", createStageResponseDto));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<Page<GetStageResponseDto>>> getStages(
            @RequestParam(defaultValue = "") String keyword,
            @PageableDefault(size = 10, sort = "stageName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<GetStageResponseDto> getStageResponseDtos = stageService.getStages(keyword, pageable);
        return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "전체 공연장 조회 성공", getStageResponseDtos));
    }


}
