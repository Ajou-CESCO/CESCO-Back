package com.cesco.pillintime.repository;

import com.cesco.pillintime.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {

}
