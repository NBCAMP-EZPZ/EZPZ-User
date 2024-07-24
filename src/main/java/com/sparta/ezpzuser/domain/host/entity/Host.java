package com.sparta.ezpzuser.domain.host.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Host extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "host_id", nullable = false, unique = true)
    private Long id;

    private String username;

    private String password;

    private String email;

    private String companyName;

    private String businessNumber;
}
