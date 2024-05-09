package com.cesco.pillintime.repository;

import com.cesco.pillintime.entity.Health;
import com.cesco.pillintime.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthRepository extends JpaRepository<Health, Long> {

    List<Health> findByOwnerId(Member member);

    Health findByDate(int date);

    void deleteByOwnerId(Long id);
}
