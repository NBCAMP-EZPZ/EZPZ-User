package com.sparta.ezpzuser.domain.host.repository;

import com.sparta.ezpzuser.domain.host.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostRepository extends JpaRepository<Host, Long> {
}
