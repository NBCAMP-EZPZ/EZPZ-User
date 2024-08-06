package com.sparta.ezpzuser.domain.like.entity;

import com.sparta.ezpzuser.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private LikeContentType contentType;

    private Long contentId;

    private LocalDateTime likedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Like(LikeContentType contentType, Long contentId, User user) {
        this.likedAt = LocalDateTime.now();
        this.contentType = contentType;
        this.contentId = contentId;
        this.user = user;
        user.addLike(this);
    }

    public static Like of(LikeContentType contentType, Long contentId, User user) {
        return new Like(contentType, contentId, user);
    }

}
