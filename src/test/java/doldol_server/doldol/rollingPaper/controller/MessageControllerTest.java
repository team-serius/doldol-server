package doldol_server.doldol.rollingPaper.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import doldol_server.doldol.common.ControllerTest;
import doldol_server.doldol.common.dto.CursorPage;
import doldol_server.doldol.rollingPaper.dto.request.CreateMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.DeleteMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.UpdateMessageRequest;
import doldol_server.doldol.rollingPaper.dto.response.MessageListResponse;
import doldol_server.doldol.rollingPaper.dto.response.MessageResponse;
import doldol_server.doldol.rollingPaper.entity.MessageType;
import doldol_server.doldol.rollingPaper.service.MessageService;

@WebMvcTest(controllers = MessageController.class)
class MessageControllerTest extends ControllerTest {

	@MockitoBean
	private MessageService messageService;

	@Test
	@DisplayName("받은 메시지 목록 조회 - 성공")
	void getMessages_Receive_Success() throws Exception {
		// given
		List<MessageResponse> mockMessages = List.of(
			MessageResponse.builder()
				.messageId(1L)
				.messageType(MessageType.RECEIVE)
				.content("안녕하세요!")
				.fontStyle("Arial")
				.backgroundColor("#FFFFFF")
				.isDeleted(false)
				.name("김철수")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build()
		);

		CursorPage<MessageResponse, Long> cursorPage = CursorPage.of(mockMessages, 10, MessageResponse::messageId);
		MessageListResponse mockResponse = MessageListResponse.of(1, cursorPage);

		when(messageService.getMessages(anyLong(), any(MessageType.class), any(LocalDateTime.class), any(), anyLong()))
			.thenReturn(mockResponse);

		// when & then
		mockMvc.perform(get("/messages")
				.param("paperId", "1")
				.param("messageType", "RECEIVE")
				.param("openDate", "2025-06-10T10:00:00")
				.param("size", "10")
				.with(mockUser(1L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.messageCount").value(1))
			.andExpect(jsonPath("$.data.message.data").isArray())
			.andExpect(jsonPath("$.data.message.data[0].messageType").value("RECEIVE"))
			.andExpect(jsonPath("$.data.message.data[0].content").value("안녕하세요!"))
			.andExpect(jsonPath("$.status").value(200));

		verify(messageService).getMessages(eq(1L), eq(MessageType.RECEIVE), any(LocalDateTime.class), any(), eq(1L));
	}

	@Test
	@DisplayName("보낸 메시지 목록 조회 - 성공 (기본값 SEND)")
	void getMessages_Send_Success() throws Exception {
		// given
		List<MessageResponse> mockMessages = List.of(
			MessageResponse.builder()
				.messageId(1L)
				.messageType(MessageType.SEND)
				.content("잘 지내세요!")
				.fontStyle("Georgia")
				.backgroundColor("#F0F0F0")
				.isDeleted(false)
				.name("이영희")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build()
		);

		CursorPage<MessageResponse, Long> cursorPage = CursorPage.of(mockMessages, 10, MessageResponse::messageId);
		MessageListResponse mockResponse = MessageListResponse.of(1, cursorPage);

		when(messageService.getMessages(anyLong(), any(MessageType.class), any(LocalDateTime.class), any(), anyLong()))
			.thenReturn(mockResponse);

		// when & then
		mockMvc.perform(get("/messages")
				.param("paperId", "1")
				.param("openDate", "2025-06-10T10:00:00")
				.param("size", "10")
				.with(mockUser(1L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.message.data").isArray())
			.andExpect(jsonPath("$.data.message.data[0].messageType").value("SEND"));

		verify(messageService).getMessages(eq(1L), eq(MessageType.SEND), any(LocalDateTime.class), any(), eq(1L));
	}

	@Test
	@DisplayName("커서 페이징을 사용한 메시지 목록 조회 - 성공")
	void getMessages_WithCursor_Success() throws Exception {
		// given
		List<MessageResponse> mockMessages = List.of();
		CursorPage<MessageResponse, Long> cursorPage = CursorPage.of(mockMessages, 5, MessageResponse::messageId);
		MessageListResponse mockResponse = MessageListResponse.of(0, cursorPage);

		when(messageService.getMessages(anyLong(), any(MessageType.class), any(LocalDateTime.class), any(), anyLong()))
			.thenReturn(mockResponse);

		// when & then
		mockMvc.perform(get("/messages")
				.param("paperId", "1")
				.param("messageType", "RECEIVE")
				.param("openDate", "2025-06-10T10:00:00")
				.param("cursorId", "10")
				.param("size", "5")
				.with(mockUser(1L)))
			.andExpect(status().isOk());

		verify(messageService).getMessages(eq(1L), eq(MessageType.RECEIVE), any(LocalDateTime.class), any(), eq(1L));
	}

	@Test
	@DisplayName("메시지 작성 - 성공")
	void createMessage_Success() throws Exception {
		// given
		CreateMessageRequest request = new CreateMessageRequest(
			1L, 2L, "안녕하세요!", "김철수", "Arial", "#FFFFFF"
		);
		doNothing().when(messageService).createMessage(any(CreateMessageRequest.class), anyLong());

		// when & then
		mockMvc.perform(post("/messages")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204));

		verify(messageService).createMessage(any(CreateMessageRequest.class), eq(1L));
	}

	@Test
	@DisplayName("메시지 작성 - 내용 비어있으면 오류를 발생시킵니다.")
	void createMessage_ValidationFail_ContentBlank() throws Exception {
		// given
		CreateMessageRequest request = new CreateMessageRequest(
			1L, 2L, "", "김철수", "Arial", "#FFFFFF"
		);

		// when & then
		mockMvc.perform(post("/messages")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(messageService, never()).createMessage(any(CreateMessageRequest.class), anyLong());
	}

	@Test
	@DisplayName("메시지 수정 - 성공")
	void updateMessage_Success() throws Exception {
		// given
		UpdateMessageRequest request = new UpdateMessageRequest(
			1L, "Georgia", "#F0F0F0", "수정된 내용", "김철수"
		);
		doNothing().when(messageService).updateMessage(any(UpdateMessageRequest.class), anyLong());

		// when & then
		mockMvc.perform(patch("/messages")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204));

		verify(messageService).updateMessage(any(UpdateMessageRequest.class), eq(1L));
	}

	@Test
	@DisplayName("메시지 수정 - 메시지 ID가 null이면 오류를 발생시킵니다.")
	void updateMessage_ValidationFail_MessageIdNull() throws Exception {
		// given
		UpdateMessageRequest request = new UpdateMessageRequest(
			null, "Georgia", "#F0F0F0", "수정된 내용", "김철수"
		);

		// when & then
		mockMvc.perform(patch("/messages")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(messageService, never()).updateMessage(any(UpdateMessageRequest.class), anyLong());
	}

	@Test
	@DisplayName("메시지 삭제 - 성공")
	void deleteMessage_Success() throws Exception {
		// given
		DeleteMessageRequest request = new DeleteMessageRequest(1L);
		doNothing().when(messageService).deleteMessage(any(DeleteMessageRequest.class), anyLong());

		// when & then
		mockMvc.perform(delete("/messages")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204));

		verify(messageService).deleteMessage(any(DeleteMessageRequest.class), eq(1L));
	}

	@Test
	@DisplayName("메시지 삭제 - 메시지 ID가 null이면 오류를 발생시킵니다.")
	void deleteMessage_ValidationFail_MessageIdNull() throws Exception {
		// given
		DeleteMessageRequest request = new DeleteMessageRequest(null);

		// when & then
		mockMvc.perform(delete("/messages")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(messageService, never()).deleteMessage(any(DeleteMessageRequest.class), anyLong());
	}

	@Test
	@DisplayName("메시지 목록 조회 - paperId 누락이면 오류를 발생시킵니다.")
	void getMessages_ValidationFail_PaperIdMissing() throws Exception {
		// when & then
		mockMvc.perform(get("/messages")
				.param("openDate", "2025-06-10T10:00:00")
				.param("size", "10"))
			.andExpect(status().isBadRequest());

		verify(messageService, never()).getMessages(anyLong(), any(MessageType.class), any(LocalDateTime.class), any(), anyLong());
	}

	@Test
	@DisplayName("메시지 목록 조회 - openDate 누락이면 오류를 발생시킵니다.")
	void getMessages_ValidationFail_OpenDateMissing() throws Exception {
		// when & then
		mockMvc.perform(get("/messages")
				.param("paperId", "1")
				.param("size", "10"))
			.andExpect(status().isBadRequest());

		verify(messageService, never()).getMessages(anyLong(), any(MessageType.class), any(LocalDateTime.class), any(), anyLong());
	}

	@Test
	@DisplayName("메시지 목록 조회 - size가 0 이하이면 오류를 발생시킵니다.")
	void getMessages_ValidationFail_InvalidSize() throws Exception {
		// when & then
		mockMvc.perform(get("/messages")
				.param("paperId", "1")
				.param("openDate", "2025-06-10T10:00:00")
				.param("size", "0"))
			.andExpect(status().isBadRequest());

		verify(messageService, never()).getMessages(anyLong(), any(MessageType.class), any(LocalDateTime.class), any(), anyLong());
	}
}
