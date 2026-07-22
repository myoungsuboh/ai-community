package com.aicommunity.curation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.aicommunity.curation.domain.Post;
import com.aicommunity.curation.domain.PostRepository;
import com.aicommunity.curation.domain.event.CurationEvents;
import com.aicommunity.curation.dto.PostDtos.CreatePostRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock PostRepository postRepository;
    @Mock ApplicationEventPublisher events;

    Clock clock = Clock.fixed(Instant.parse("2026-07-22T00:00:00Z"), ZoneOffset.UTC);
    PostService service;

    @BeforeEach
    void setUp() {
        service = new PostService(postRepository, events, clock);
    }

    @Test
    @DisplayName("작성: 허용되지 않는 HTML 태그 제거(POL-36) + PostCreated 발행")
    void create_sanitizesHtml() {
        UUID author = UUID.randomUUID();
        CreatePostRequest req = new CreatePostRequest(
                "제목 <script>alert(1)</script>",
                "본문 <b>강조</b> 그리고 <img src=x onerror=y> 내용입니다.",
                List.of("tag<i>", "정상"));

        service.create(author, req);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        Post saved = captor.getValue();
        assertThat(saved.getTitle()).doesNotContain("<script>").doesNotContain("</script>");
        assertThat(saved.getContent()).doesNotContain("<b>").doesNotContain("<img");
        assertThat(saved.getTags()).allSatisfy(t -> assertThat(t).doesNotContain("<"));
        verify(events).publishEvent(any(CurationEvents.PostCreated.class));
    }
}
