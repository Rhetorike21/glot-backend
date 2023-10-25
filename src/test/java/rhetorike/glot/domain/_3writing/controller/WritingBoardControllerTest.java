package rhetorike.glot.domain._3writing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import rhetorike.glot.domain._3writing.dto.WritingDto;
import rhetorike.glot.domain._3writing.service.WritingBoardService;
import rhetorike.glot.global.constant.Header;
import rhetorike.glot.global.security.JwtAuthenticationFilter;
import rhetorike.glot.global.security.SecurityConfig;
import rhetorike.glot.global.util.dto.SingleResponseDto;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static hansol.restdocsdsl.docs.RestDocsAdapter.docs;
import static hansol.restdocsdsl.docs.RestDocsHeader.requestHeaders;
import static hansol.restdocsdsl.docs.RestDocsPathParam.pathParams;
import static hansol.restdocsdsl.docs.RestDocsRequest.requestFields;
import static hansol.restdocsdsl.docs.RestDocsResponse.responseFields;
import static hansol.restdocsdsl.element.FieldElement.field;
import static hansol.restdocsdsl.element.HeaderElement.header;
import static hansol.restdocsdsl.element.ParamElement.param;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(value = WritingBoardController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class WritingBoardControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WritingBoardService writingBoardService;


    @Test
    @WithMockUser
    @DisplayName("[작문 보드 생성]")
    void createBoard() throws Exception {
        //given
        final String ACCESS_TOKEN = "access-token";
        WritingDto.CreationRequest requestDto = new WritingDto.CreationRequest("부자 되는 법");
        given(writingBoardService.createBoard(any(), any())).willReturn(new SingleResponseDto<>(1L));

        //when
        ResultActions actions = mockMvc.perform(post(WritingBoardController.CREATE_WRITING_BOARD_URI)
                .with(csrf())
                .header(Header.AUTH, ACCESS_TOKEN)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("board-create",
                        requestHeaders(
                                header(Header.AUTH).description("액세스 토큰").optional()
                        ),
                        requestFields(
                                field("title").description("제목")
                        ),
                        responseFields(
                                field("data").type(JsonFieldType.NUMBER).description("생성된 보드의 아이디넘버")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("[작문 보드 전체 조회]")
    void getAllBoards() throws Exception {
        //given
        final String ACCESS_TOKEN = "access-token";
        given(writingBoardService.getAllBoards(any())).willReturn(List.of(new WritingDto.Response(1L, "제목", YearMonth.of(2023, 10))));

        //when
        ResultActions actions = mockMvc.perform(get(WritingBoardController.GET_ALL_WRITING_BOARD_URI)
                .with(csrf())
                .header(Header.AUTH, ACCESS_TOKEN));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("board-get-all",
                        requestHeaders(
                                header(Header.AUTH).description("액세스 토큰").optional()
                        ),
                        responseFields(
                                field("[].id").type(JsonFieldType.NUMBER).description("작문 보드 아이디넘버"),
                                field("[].title").description("제목"),
                                field("[].yearMonth").description("연월(yyyy-MM)")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("[작문 보드 단건 조회]")
    void getBoard() throws Exception {
        //given
        final String ACCESS_TOKEN = "access-token";
        given(writingBoardService.getBoard(any(), any())).willReturn(new WritingDto.DetailResponse("제목", "내용", LocalDateTime.now(), LocalDateTime.now()));

        //when
        ResultActions actions = mockMvc.perform(get(WritingBoardController.GET_WRITING_BOARD_URI, 1L)
                .with(csrf())
                .header(Header.AUTH, ACCESS_TOKEN));

        //then
        actions.andExpect(status().isOk())
                .andDo(docs("board-get",
                        requestHeaders(
                                header(Header.AUTH).description("액세스 토큰").optional()
                        ),
                        pathParams(
                                param("writingId").description("작문 보드 아이디넘버")
                        ),
                        responseFields(
                                field("title").description("제목"),
                                field("content").description("내용"),
                                field("createdTime").description("생성시간"),
                                field("modifiedTime").description("수정시간")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("[작문 보드 삭제]")
    void deleteBoard() throws Exception {
        //given
        final String ACCESS_TOKEN = "access-token";

        //when
        ResultActions actions = mockMvc.perform(delete(WritingBoardController.DELETE_WRITING_BOARD_URI, 1L)
                .with(csrf())
                .header(Header.AUTH, ACCESS_TOKEN));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("board-delete",
                        requestHeaders(
                                header(Header.AUTH).description("액세스 토큰").optional()
                        ),
                        pathParams(
                                param("writingId").description("작문 보드 아이디넘버")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("[작문 보드 이동]")
    void moveBoard() throws Exception {
        //given
        final String ACCESS_TOKEN = "access-token";
        WritingDto.MoveRequest requestDto = new WritingDto.MoveRequest(1, 2);

        //when
        ResultActions actions = mockMvc.perform(post(WritingBoardController.MOVE_BOARD_URI)
                .with(csrf())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(Header.AUTH, ACCESS_TOKEN));

        //then
        actions.andExpect(status().isNoContent())
                .andDo(docs("board-move",
                        requestHeaders(
                                header(Header.AUTH).description("액세스 토큰").optional()
                        ),
                        requestFields(
                                field("targetId").type(JsonFieldType.NUMBER).description("이동할 보드의 아이디넘버"),
                                field("destinationId").type(JsonFieldType.NUMBER).description("이동할 위치에 있는 보드의 아이디넘버")
                        )));
    }



}