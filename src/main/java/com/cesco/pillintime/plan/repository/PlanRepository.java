package com.cesco.pillintime.plan.repository;

import com.cesco.pillintime.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {


}
