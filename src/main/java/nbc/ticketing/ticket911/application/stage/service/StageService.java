package nbc.ticketing.ticket911.application.stage.service;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.stage.dto.request.CreateStageRequestDto;
import nbc.ticketing.ticket911.domain.stage.dto.response.CreateStageResponseDto;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.repository.StageRepository;
import nbc.ticketing.ticket911.domain.stage.status.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StageService {

    public final StageRepository stageRepository;
    public CreateStageResponseDto createStage(CreateStageRequestDto createStageRequestDto) {
        Stage stage = Stage.builder()
                        .stageName(createStageRequestDto.getStageName())
                        .totalSeat(0L)
                        .status(Status.AVAILABLE)
                        .build();

        stageRepository.save(stage);

        return CreateStageResponseDto.builder()
                .stageName(stage.getStageName())
                .totalSeats(stage.getTotalSeat())
                .status(stage.getStatus())
                .build();
    }
}
