package com.cesco.pillintime.repository;

import com.cesco.pillintime.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE r.senderId = :userId")
    List<Request> findBySenderId(@Param("userId") long userId);
}
