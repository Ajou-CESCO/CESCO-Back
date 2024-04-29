package com.cesco.pillintime.repository.user;

import com.cesco.pillintime.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByPhone(String phone); // ssn과 동일한 유저가 있으면 유저를 반환, 없으면 null
    Member findByUuid(String uuid);
    Member findByNameAndSsnAndPhone(String name, String ssn, String phone); // 동일한 유저가 있으면 true, 없으면 false

}