package com.cesco.pillintime.api.bug.repository;

import com.cesco.pillintime.api.bug.entity.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {
}
