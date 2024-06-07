package com.cesco.pillintime.api.medicine.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultHandler;

import java.nio.charset.StandardCharsets;

public class CustomResultHandlers {

    public static ResultHandler printResponseOnly() {
        return result -> {
            ObjectMapper objectMapper = new ObjectMapper();
            String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(responseContent).get("result");
            for(JsonNode items : jsonNode){
                System.out.print("\""+items.get("medicineName").asText()+"\", ");
            }
            System.out.println("\n총 " + jsonNode.size() + "개");
//            System.out.println(responseContent);
        };
    }
}