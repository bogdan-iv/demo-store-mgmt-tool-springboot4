package com.demo.store.mgmt.tool.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "authorities")
@Getter // Use specific getters rather than @Data
@Setter // Use specific setters
@NoArgsConstructor // The essential no-arg constructor for JPA
@AllArgsConstructor
public class Authority {

    // An auto-incrementing ID for the authority record itself
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A field to hold the username this authority belongs to (the foreign key)
    @Column(name = "username")
    private String username;

    // A field to hold the actual role name (e.g., "ROLE_ADMIN", "ROLE_USER")
    @Column(name = "authority")
    private String authority;
}
