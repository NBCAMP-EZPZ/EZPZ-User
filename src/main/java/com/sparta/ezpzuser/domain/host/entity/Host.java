package com.sparta.ezpzuser.domain.host.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Host extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "host_id")
    private Long id;

    private String username;

    private String password;

    private String email;

    private String companyName;

    private String businessNumber;
    
    private Host (String username, String password, String email, String companyName, String businessNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.companyName = companyName;
        this.businessNumber = businessNumber;
    }
    
    public static Host of(String username, String password, String email, String companyName, String businessNumber) {
        return new Host(username, password, email, companyName, businessNumber);
    }
}
