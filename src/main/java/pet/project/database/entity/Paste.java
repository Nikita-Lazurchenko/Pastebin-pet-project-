package pet.project.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@ToString(exclude = {"user"})
@EqualsAndHashCode(of="pasteHash")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "pastebin", name = "pastes")
public class Paste implements BaseEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paste_hash", nullable = false, unique = true)
    private String pasteHash;

    @Column(name = "google_file_id", unique = true)
    private String googleFileId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tags", columnDefinition = "text[]")
    private Set<String> tags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Expiration expiration;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private Access access;

    @Column(name = "two_factor_authentication", nullable = false)
    private Boolean twoFactorAuthentication;

    @Column(nullable = false)
    private String title;

    @Builder.Default
    @Column(nullable = false)
    private Long views = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void setUser(User user){
        this.user = user;
        user.getPastes().add(this);
    }
}