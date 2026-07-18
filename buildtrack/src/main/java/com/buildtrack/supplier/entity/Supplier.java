package com.buildtrack.supplier.entity;

import com.buildtrack.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Supplier {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 150)
    @Column(nullable = false)
    private String name;

    @Email @Size(max = 120)
    private String contactEmail;

    @Size(max = 40)
    private String phone;

    @Size(max = 250)
    private String address;

    /** Optional link to a User with SUPPLIER role — enables supplier login. */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
