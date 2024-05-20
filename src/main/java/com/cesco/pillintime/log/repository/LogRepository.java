package com.cesco.pillintime.log.repository;

import com.cesco.pillintime.log.entity.Log;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    @Query("SELECT l FROM Log l WHERE l.member= :member")
    Optional<List<Log>> findByMember(Member member);

    Optional<List<Log>> findByMemberAndPlannedAt(Member member, LocalDate today);

    @Query("SELECT l FROM Log l WHERE l.member = :member AND l.plannedAt = :today AND l.plan.cabinetIndex = :index AND l.plan.time BETWEEN :rangeStartAt AND :rangeEndAt")
    Optional<Log> findTargetLog(
            @Param("member") Member member,
            @Param("today") LocalDate today,
            @Param("index") Integer index,
            @Param("rangeStartAt") LocalTime rangeStartAt,
            @Param("rangeEndAt") LocalTime rangeEndAt
    );

    @Query("SELECT l FROM Log l WHERE l.plannedAt = :today AND l.plan.time <= :currentTime AND l.takenStatus = 0")
    List<Log> findIncompleteLog(LocalDate today, LocalTime currentTime);

    boolean existsByMemberAndPlanAndPlannedAt(Member member, Plan plan, LocalDate plannedAt);

}
