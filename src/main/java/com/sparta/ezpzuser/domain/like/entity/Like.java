package com.sparta.ezpzuser.domain.like.entity;

import com.sparta.ezpzuser.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @CreatedDate
    @Column(name = "liked_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime likedAt;

    private Like(User user, Long contentId, String contentType) {
        this.user = user;
        this.contentId = contentId;
        this.contentType = contentType;
    }

    public static Like of(User user, Long contentId, String contentType) {
        return new Like(user, contentId, contentType);
    }
}
