package md.bumbac.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String fullName;
    private boolean emailVerified;

    /** роли: USER / ADMIN / CONTENT_MANAGER … */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    /** избранные товары */
    @ManyToMany
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> favorites = new HashSet<>();

    /** настройки уведомлений */
    private boolean notifyByEmail = true;
    private boolean notifyBySms   = false;
    private boolean notifyByPush  = false;
}   // ← единственная финальная скобка, после неё пусто
