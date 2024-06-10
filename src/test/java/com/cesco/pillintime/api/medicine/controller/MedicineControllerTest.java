//package com.cesco.pillintime.api.medicine.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.hamcrest.Matchers.greaterThanOrEqualTo;
//import static org.hamcrest.Matchers.hasSize;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//@SpringBootTest
//@AutoConfigureMockMvc
//class MedicineControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private String jwtToken;
//
//    @BeforeEach
//    public void setup() {
//        // JWT 토큰 생성 또는 설정
//        jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjMxIiwiaWF0IjoxNzE3NjcwMTYwLCJleHAiOjE4MDQwNzAxNjB9.pLWs7kGVD1HLhO1504sMwi4McaxJmFIQtjeWwJRTe8s";
//    }
//
//    @Test
//    public void testGetMedicineWithDifferentNames() throws Exception {
//
////        String[] names = {"가","나","다","라","마","바","사","아","자","카","타","파","하"};
//        String name = "라";
//        long total = 0 ;
//        for (int i = 0 ; i < 10 ; i++){
//            long sum=0;
////            for(String name : names){
//                long startTime = System.currentTimeMillis();
//                mockMvc.perform(MockMvcRequestBuilders.get("/api/medicine")
//                                .param("name", name)
//                                .param("memberId", "31")
//                                .header("Authorization", "Bearer " + jwtToken)
//                                .contentType(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isOk())
//                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("$.status").value(200))
//                        .andExpect(jsonPath("$.message").value("Success get medicine"))
//                        .andExpect(jsonPath("$.result").isArray())
//                        .andExpect(jsonPath("$.result", hasSize(greaterThanOrEqualTo(1))))
//                        .andDo(CustomResultHandlers.printResponseOnly());
//                long stopTime = System.currentTimeMillis();
//                sum += (stopTime - startTime);
////            }
//            total += sum/13;
//                System.out.println(i+1 +"번째 실행 시간 : " + (sum)/13 + "ms");
//        }
//        System.out.println("total = " + total/10);
//    }
//}