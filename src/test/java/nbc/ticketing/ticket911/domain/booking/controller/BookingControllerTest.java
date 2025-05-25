package nbc.ticketing.ticket911.domain.booking.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import nbc.ticketing.ticket911.domain.booking.application.BookingFacade;
import nbc.ticketing.ticket911.domain.booking.application.BookingService;
import nbc.ticketing.ticket911.domain.booking.dto.request.BookingRequestDto;
import nbc.ticketing.ticket911.domain.booking.dto.response.BookingResponseDto;
import nbc.ticketing.ticket911.support.security.TestSecurityConfig;

@WebMvcTest(controllers = BookingController.class)
@Import(TestSecurityConfig.class)
class BookingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private BookingService bookingService;

	@MockitoBean
	private BookingFacade bookingFacade;

	private final BookingResponseDto mockResponseDto = BookingResponseDto.builder()
		.id(1L)
		.userEmail("test@test.com")
		.userNickname("테스터")
		.concertTitle("테스트 콘서트")
		.stageName("테스트홀")
		.startTime(LocalDateTime.now().plusDays(1))
		.updatedAt(LocalDateTime.now())
		.seatNames(List.of("A1", "A2"))
		.build();

	private final BookingRequestDto requestDto = new BookingRequestDto(List.of(1L, 2L));

	@Test
	@DisplayName("예매 생성 - 기본 API 성공")
	@WithMockUser(username = "user", roles = "USER")
	void createBooking_success() throws Exception {
		// given
		given(bookingService.createBooking(any(), any())).willReturn(mockResponseDto);

		// when, then
		mockMvc.perform(post("/bookings")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.status").value(201))
			.andExpect(jsonPath("$.message").value("예약 성공"))
			.andExpect(jsonPath("$.result.userEmail").value("test@test.com"))
			.andExpect(jsonPath("$.result.seatNames[0]").value("A1"));
	}

	@Test
	@DisplayName("예매 생성 - Redisson 락 API 성공")
	@WithMockUser(username = "user", roles = "USER")
	void createBooking_redisson_success() throws Exception {
		// given
		given(bookingService.createBookingByRedisson(any(), any())).willReturn(mockResponseDto);

		// when, then
		mockMvc.perform(post("/bookings/redisson")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.status").value(201))
			.andExpect(jsonPath("$.message").value("예약 성공"))
			.andExpect(jsonPath("$.result.userNickname").value("테스터"))
			.andExpect(jsonPath("$.result.seatNames[1]").value("A2"));
	}
}
