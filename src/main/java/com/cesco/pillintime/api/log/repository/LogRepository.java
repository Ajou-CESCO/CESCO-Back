package com.cesco.pillintime.api.log.repository;

import com.cesco.pillintime.api.log.entity.Log;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    @Query("SELECT l FROM Log l WHERE l.member= :member")
    Optional<List<Log>> findByMember(Member member);

    @Query("SELECT l FROM Log l WHERE l.member = :member AND l.plannedAt BETWEEN :startOfDay AND :endOfDay")
    Optional<List<Log>> findByMemberAndPlannedAtBetween(Member member, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT l FROM Log l WHERE l.member = :member AND l.plan.cabinetIndex = :index AND l.plannedAt BETWEEN :rangeStartAt AND :rangeEndAt")
    Optional<Log> findTargetLog(
            @Param("member") Member member,
            @Param("index") Integer index,
            @Param("rangeStartAt") LocalDateTime rangeStartAt,
            @Param("rangeEndAt") LocalDateTime rangeEndAt
    );

    // 시간초과된 미복용 로그를 조회하기 위한 쿼리
    @Query("SELECT l FROM Log l WHERE l.plannedAt <= :targetTime AND l.takenStatus = 0")
    List<Log> findIncompleteLog(LocalDateTime targetTime);

    // 예정된 미복용 로그를 조회하기 위한 쿼리
    @Query("SELECT l FROM Log l WHERE l.plannedAt BETWEEN :startOfSecond AND :endOfSecond AND l.takenStatus = 0")
    Optional<List<Log>> findPlannedLogBetween(LocalDateTime startOfSecond, LocalDateTime endOfSecond);

    @Query("SELECT l FROM Log l WHERE l.member = :member AND l.takenStatus = 0 AND l.plan.groupId = :groupId")
    Optional<List<Log>> findPlannedLog(Member member, Long groupId);

    @Query("SELECT l FROM Log l WHERE DATE(l.plannedAt) = :today AND l.member = :targetMember AND l.takenStatus = 0")
    Optional<List<Log>> findUnfinishedLog(LocalDate today, Member targetMember);

    boolean existsByMemberAndPlanAndPlannedAt(Member member, Plan plan, LocalDateTime plannedAt);

}
