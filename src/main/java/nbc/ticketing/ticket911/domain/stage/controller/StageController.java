package nbc.ticketing.ticket911.domain.stage.controller;

import jakarta.validation.Valid;
import nbc.ticketing.ticket911.application.stage.service.StageService;
import nbc.ticketing.ticket911.common.response.CommonResponse;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.stage.dto.request.CreateStageRequestDto;
import nbc.ticketing.ticket911.domain.stage.dto.response.CreateStageResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

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



}
