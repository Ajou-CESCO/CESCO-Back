package com.cesco.pillintime.repository;

import com.cesco.pillintime.entity.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RelationRepository extends JpaRepository<Relation, Long> {

    @Query("SELECT r FROM Relation r WHERE r.managerId = :id OR r.clientId = :id")
    List<Relation> findByMemberId(Long id);

    @Query("DELETE FROM Relation r WHERE r.managerId = :id OR r.clientId = :id")
    void deleteByMemberId(Long id);
}