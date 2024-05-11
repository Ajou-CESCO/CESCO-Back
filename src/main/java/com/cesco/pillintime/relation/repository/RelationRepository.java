package com.cesco.pillintime.relation.repository;

import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.relation.entity.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RelationRepository extends JpaRepository<Relation, Long> {

    @Query("SELECT r FROM Relation r WHERE r.manager = :id OR r.client = :id")
    Optional<List<Relation>> findByMember(Member member);

//    @Query("DELETE FROM Relation r WHERE r.manager = :id OR r.client = :id")
//    void deleteByMemberId(Member member);
}