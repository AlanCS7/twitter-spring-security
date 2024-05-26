package io.github.alancs7.twitter.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    private String name;

    @Getter
    public enum Values {
        ADMIN(1),
        BASIC(2);

        final long roleId;

        Values(long roleId) {
            this.roleId = roleId;
        }
    }
}
