package com.cesco.pillintime.member.repository;

import com.cesco.pillintime.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByName(String name);

    Optional<Member> findByPhone(String phone); // ssn과 동일한 유저가 있으면 유저를 반환, 없으면 null

    Optional<Member> findBySsn(String ssn);

    Optional<Member> findByNameAndPhone(String name, String phone);

    Optional<Member> findByNameAndPhoneAndSsn(String name, String phone, String ssn); // 동일한 유저가 있으면 true, 없으면 false

}