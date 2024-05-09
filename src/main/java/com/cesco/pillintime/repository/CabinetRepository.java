package com.cesco.pillintime.repository;

import com.cesco.pillintime.entity.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CabinetRepository extends JpaRepository<Cabinet, Long> {

    Optional<Cabinet> findByUuid(String uuid);

}