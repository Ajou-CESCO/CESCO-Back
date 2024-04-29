package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.UserDto;
import com.cesco.pillintime.dto.user.response.UserSearchResponse;
import com.cesco.pillintime.entity.User;
import com.cesco.pillintime.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    // 회원 가입
    public ResponseEntity<String> createUser(UserDto request){
        // 회원가입 여부 확인
        User user = userRepository.findByPhone(request.getPhone());
        if(user != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
        }
        // 성별 확인
        int gender = Character.getNumericValue(request.getSsn().charAt(7));
        if(gender % 2 == 0){
            gender = 0;
        } else {
            gender = 1;
        }
        // 회원가입 진행
        User newUser = new User(request.getName(), request.getPhone(), request.getSsn(), gender, request.getUserType());
        User savedUser = userRepository.saveAndFlush(newUser);

        if (savedUser.getPhone() == null) { // 회원가입 실패
            System.out.println("newUser = " + newUser);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success create plan");
    }
    // 로그인
    public ResponseEntity<String> joinUser(UserDto request){
        try {
            // 회원 여부 확인
            User user = userRepository.findByNameAndSsnAndPhone(request.getName(), request.getSsn(), request.getPhone());
//            userRepository.findByNameAndSsnAndPhone(request.getName(), request.getSsn(), request.getPhone());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            } else {
                // JWT TOKEN 생성
                return ResponseEntity.status(HttpStatus.OK).body("Success create plan");
            }
        }
        catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
    // 내 정보 조회
    public User searchUserByUuid() {
        try {
            /*
            토큰 검증 로직 필요
            사용자 토큰을 기반으로 유효한 사용자일 경우 해당 사용자 정보 반환
             */

            String uuid = "1234"; // 토큰 내부 uuid 임시정의

            User user = userRepository.findByUuid(uuid);
            return user;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    // 사용자 검색
    public ResponseEntity<?> searchUserByPhone(String phone){
        try {
            User user = userRepository.findByPhone(phone);
            if( user == null ) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
            // 사용자 정보 반환
            UserSearchResponse response = new UserSearchResponse(HttpStatus .OK, "Success get user", user);
            return ResponseEntity.ok(response);

        } catch(EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
    // 정보 수정
    public ResponseEntity<String> updateUserById(UserDto request) {

        User user = userRepository.findByUuid(request.getUuid()); // 토큰 검증 대신 사용

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User is not exist");
        }

        // 성별 확인
        int gender = Character.getNumericValue(request.getSsn().charAt(7));

        if(gender % 2 == 0){
            gender = 0;
        } else {
            gender = 1;
        }

        // 사용자 정보 임시 업데이트
        user.setName(request.getName());
        user.setSsn(request.getSsn());
        user.setGender(gender);
        user.setPhone(request.getPhone());

        // 사용자 정보 저장
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("Success update plan");
    }
    // 탈퇴
    public ResponseEntity<String> deleteUser(UserDto request){
        /*
        헤더에 담어서 토큰을 담아서 보낸다.
         */
        try {
            User user = userRepository.findByUuid(request.getUuid()); // 토큰 검증 대신 사용
            if( user == null ) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
            userRepository.delete(user);

            return ResponseEntity.status(HttpStatus.OK).body("Success delete user");
        }
        catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

}
