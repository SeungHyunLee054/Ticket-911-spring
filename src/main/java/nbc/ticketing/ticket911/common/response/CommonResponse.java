package nbc.ticketing.ticket911.common.response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
	private boolean success;
	private int status;
	private String message;
	private Results<T> results;
	private T result;

	public static <T> CommonResponse<T> of(boolean success, int status, String message, T result) {
		return CommonResponse.<T>builder()
			.success(success)
			.status(status)
			.message(message)
			.result(result)
			.build();
	}

	public static <T> CommonResponse<T> of(boolean success, int status, String message) {
		return CommonResponse.<T>builder()
			.success(success)
			.status(status)
			.message(message)
			.build();
	}

	public static <T> CommonResponse<T> ofPage(boolean success, int status, String message, Page<T> page) {
		return CommonResponse.<T>builder()
			.success(success)
			.status(status)
			.message(message)
			.results(Results.<T>builder()
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.hasNextPage(page.hasNext())
				.hasPreviousPage(page.hasPrevious())
				.content(page.getContent())
				.build())
			.build();
	}

	@Getter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Results<T> {
		private Long totalElements;
		private Integer totalPages;
		private Boolean hasNextPage;
		private Boolean hasPreviousPage;
		@Builder.Default
		private List<T> content = new ArrayList<>();
	}
}