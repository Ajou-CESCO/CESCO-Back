package com.cesco.pillintime.api.request.repository;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.request.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE r.receiverPhone = :receiverPhone")
    Optional<List<Request>> findByReceiverPhone(@Param("receiverPhone") String receiverPhone);

    Optional<Request> findBySenderAndReceiverPhone(Member sender, String receiverPhone);
}