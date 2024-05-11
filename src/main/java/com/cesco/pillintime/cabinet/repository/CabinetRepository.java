package com.cesco.pillintime.cabinet.repository;

import com.cesco.pillintime.cabinet.entity.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CabinetRepository extends JpaRepository<Cabinet, Long> {

    Optional<Cabinet> findBySerial(String serial);

}