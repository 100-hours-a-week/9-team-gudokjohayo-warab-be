package store.warab.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "kakao_id", length = 255)
    private String kakaoId;

    @Column(nullable = false, unique = true, length = 12)
    private String nickname;

    @Column(name = "discord_link", length = 255, nullable = false)
    private String discordLink;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // 엔티티가 처음 저장될 때 자동으로 생성일 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = Timestamp.valueOf(LocalDateTime.now());
    }

}
