package com.cesco.pillintime.repository;

import com.cesco.pillintime.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByName(String name);

    Optional<Member> findByPhone(String phone); // ssn과 동일한 유저가 있으면 유저를 반환, 없으면 null

    Optional<Member> findByUuid(String uuid);

    Optional<Member> findByNameAndPhone(String name, String phone);

    Optional<Member> findByNameAndPhoneAndSsn(String name, String phone, String ssn); // 동일한 유저가 있으면 true, 없으면 false

}