package com.aicommunity.curation.domain;

import com.aicommunity.common.entity.BaseTimeEntity;
import com.aicommunity.common.support.StringListJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Post 애그리거트. 불변식(2_ddd.md): 제목 5자↑, 내용 10자↑, 허용 않는 HTML 제거.
 */
@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "text")
    private List<String> tags;

    public static Post create(String title, String content, UUID authorId, List<String> tags) {
        Post p = new Post();
        p.id = UUID.randomUUID();
        p.title = title;
        p.content = content;
        p.authorId = authorId;
        p.tags = tags;
        return p;
    }
}
