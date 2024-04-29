package com.cesco.pillintime.repository.user;

import com.cesco.pillintime.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByPhone(String phone); // ssn과 동일한 유저가 있으면 유저를 반환, 없으면 null
    User findByUuid(String uuid);
    User findByNameAndSsnAndPhone(String name, String ssn, String phone); // 동일한 유저가 있으면 true, 없으면 false

}