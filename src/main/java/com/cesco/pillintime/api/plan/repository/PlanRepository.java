package com.cesco.pillintime.api.plan.repository;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<List<Plan>> findPlanByMemberIdAndGroupId(Long memberId, Long groupId);

    @Query("SELECT r FROM Plan r WHERE r.member= :member")
    Optional<List<Plan>> findByMember(Member member);

    @Query("SELECT p FROM Plan p WHERE :today <= p.endAt")
    Optional<List<Plan>> findActivePlan(LocalDate today);

    @Query("SELECT p FROM Plan p WHERE :today > p.endAt")
    Optional<List<Plan>> findInactivePlan(LocalDate today);

    @Query("SELECT p FROM Plan p WHERE p.member= :member AND p.groupId= :groupId")
    Optional<List<Plan>> findTargetPlan(Member member, Long groupId);

    @Query("SELECT DISTINCT p.medicineName , p.medicineSeries FROM Plan p WHERE p.member= :member")
    List<Map<String, String>> findTakingMedicine(@Param("member") Member member);

    @Query("SELECT DISTINCT p.cabinetIndex FROM Plan p WHERE p.member = :member ORDER BY p.cabinetIndex ASC")
    List<Long> findUsingCabinetIndex(Member member);

    @Query("SELECT COALESCE(MAX(p.groupId), 0) FROM Plan p")
    Long findMaxGroupId();
}
