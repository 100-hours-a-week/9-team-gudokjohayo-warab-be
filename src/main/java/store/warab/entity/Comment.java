package store.warab.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private GameStatic gameStatic;

    @Column(length = 100, nullable = false)
    private String content;  // varchar(100)

    @Column(name = "created_at", insertable = false, updatable = false,
        columnDefinition = "timestamp default now()")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false,
        columnDefinition = "timestamp default now()")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;  // null 가능

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GameStatic getGameStatic() {
        return gameStatic;
    }

    public void setGameStatic(GameStatic gameStatic) {
        this.gameStatic = gameStatic;
    }
}
