package com.sparta.ezpzuser.domain.host.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.sparta.ezpzuser.domain.host.entity.Host;

public interface HostRepository extends JpaRepository<Host, Long> {
}
