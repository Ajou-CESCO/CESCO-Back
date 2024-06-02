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
import java.util.Set;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    @Query("SELECT r FROM Plan r WHERE r.member= :member")
    Optional<List<Plan>> findByMember(Member member);

    @Query("SELECT p FROM Plan p WHERE :today <= p.endAt")
    Optional<List<Plan>> findActivePlan(LocalDate today);

    @Query("SELECT p FROM Plan p WHERE p.member= :member AND p.medicineId= :medicineId AND p.cabinetIndex= :cabinetIndex")
    Optional<List<Plan>> findTargetPlan(Member member, Long medicineId, int cabinetIndex);

    @Query("SELECT DISTINCT p.medicineName , p.medicineSeries FROM Plan p WHERE p.member= :member")
    Optional<Map<String,String>> findUniqueMedicineNameAndAdverse(@Param("member") Member member);
}
