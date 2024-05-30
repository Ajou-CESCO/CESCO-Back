package com.cesco.pillintime.api.health.repository;

import com.cesco.pillintime.api.health.entity.Health;
import com.cesco.pillintime.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthRepository extends JpaRepository<Health, Long> {

    Optional<List<Health>> findByMember(Member member);

    @Query("SELECT h FROM Health h WHERE h.member = :member ORDER BY h.lastUpLoadTime DESC LIMIT 1")
    Optional<Health> findMaxLocalDateTimeByMember(@Param("member") Member member);
}
