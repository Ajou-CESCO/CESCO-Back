package com.cesco.pillintime.log.repository;

import com.cesco.pillintime.log.entity.Log;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    @Query("SELECT r FROM Log r WHERE r.member= :member")
    Optional<List<Log>> findByMember(Member member);

    boolean existsByMemberAndPlanAndPlannedAt(Member member, Plan plan, LocalDate plannedAt);

}
