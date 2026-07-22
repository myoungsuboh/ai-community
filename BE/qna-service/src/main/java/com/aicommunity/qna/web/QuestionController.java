package com.aicommunity.qna.web;

import com.aicommunity.common.security.UserPrincipal;
import com.aicommunity.common.web.PageResponse;
import com.aicommunity.qna.domain.Answer;
import com.aicommunity.qna.domain.Question;
import com.aicommunity.qna.dto.QnaDtos.AnswerResponse;
import com.aicommunity.qna.dto.QnaDtos.CreateAnswerRequest;
import com.aicommunity.qna.dto.QnaDtos.CreateQuestionRequest;
import com.aicommunity.qna.dto.QnaDtos.QuestionDetail;
import com.aicommunity.qna.dto.QnaDtos.QuestionSummary;
import com.aicommunity.qna.service.QnaService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QnaService qnaService;

    public QuestionController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    // 질문 등록 (회원)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuestionSummary> create(@AuthenticationPrincipal UserPrincipal user,
                                                  @Valid @RequestBody CreateQuestionRequest req) {
        Question q = qnaService.createQuestion(user.id(), req.title(), req.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(QuestionSummary.from(q));
    }

    // 답변 등록 (회원)
    @PostMapping("/{questionId}/answers")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnswerResponse> answer(@AuthenticationPrincipal UserPrincipal user,
                                                 @PathVariable UUID questionId,
                                                 @Valid @RequestBody CreateAnswerRequest req) {
        Answer a = qnaService.addAnswer(user.id(), questionId, req.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(AnswerResponse.from(a));
    }

    // Q&A 목록 (공개) — Q&A 목록 화면용 (20-API 명세엔 없으나 화면 구현에 필요)
    @GetMapping
    public PageResponse<QuestionSummary> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        Page<Question> result = qnaService.list(keyword, page, size);
        return PageResponse.from(result, result.map(QuestionSummary::from).getContent());
    }

    // 질문 상세 + 답변 (공개) — 답변 조회용
    @GetMapping("/{questionId}")
    public QuestionDetail detail(@PathVariable UUID questionId) {
        return qnaService.detail(questionId);
    }
}
