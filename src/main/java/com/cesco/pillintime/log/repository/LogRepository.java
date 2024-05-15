package com.cesco.pillintime.log.repository;

import com.cesco.pillintime.log.entity.Log;
import com.cesco.pillintime.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    @Query("SELECT r FROM Log r WHERE r.member= :member")
    Optional<List<Log>> findByMember(Member member);
}
