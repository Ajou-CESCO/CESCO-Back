package com.cesco.pillintime.plan.repository;

import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    @Query("SELECT r FROM Plan r WHERE r.member= :member")
    Optional<List<Plan>> findByMember(Member member);

    @Query("SELECT p FROM Plan p WHERE :today <= p.endedAt")
    Optional<List<Plan>> findActivePlan(LocalDate today);

}
