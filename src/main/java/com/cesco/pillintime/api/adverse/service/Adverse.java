package com.cesco.pillintime.api.adverse.service;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class Adverse {

    @Value("${DRUG_AVERSE_COMB_SERVICE_URL}")
    private String serviceCombineUrl;
    @Value("${DRUG_AVERSE_SENI_SERVICE_URL}")
    private String getServiceSeniorUrl;
    @Value("${DRUG_AVERSE_DUR_SERVICE_URL}")
    private String serviceDurInfoUrl;
    @Value("${DRUG_AVERSE_SPEC_SERVICE_URL}")
    private String serviceSpecificUrl;
    @Value("${DRUG_AVERSE_DOSA_SERVICE_URL}")
    private String ServiceDosageUrl;
    @Value("${DRUG_AVERSE_PERI_SERVICE_URL}")
    private String servicePeriodUrl;
    @Value("${DRUG_AVERSE_DUPL_SERVICE_URL}")
    private String serviceDuplicateUrl;
    @Value("${DRUG_AVERSE_DIVI_SERVICE_URL}")
    private String serviceDivideUrl;
    @Value("${DRUG_AVERSE_PREG_SERVICE_URL}")
    private String servicePregnantUrl;

    @Value("${EASY_DRUG_AVERSE_SERVICE_KEY}")
    private String serviceKey;

    public List<String> search(String drugName) {
        if(drugName.isEmpty()) {
            throw new CustomException(ErrorCode.MEDICINE_NAME_IS_EMPTY);
        }
        List<String> typeNameList = new ArrayList<>();
        List<String> serviceUrlList = Arrays.asList(
                serviceCombineUrl,
                getServiceSeniorUrl,
//                serviceDurInfoUrl // commented out in the original code
                serviceSpecificUrl,
                ServiceDosageUrl,
                servicePeriodUrl,
                serviceDuplicateUrl,
                serviceDivideUrl,
                servicePregnantUrl
        );

        for (String url : serviceUrlList) {
            String typeName = circle(url, drugName);
            if (typeName != null) {
                typeNameList.add(typeName);
            }
        }
        System.out.println("typeNameList = " + typeNameList);

        return typeNameList;
    }

    // =================================================================================
    public String circle (String baseUrl, String name){
        try {
            StringBuilder result = new StringBuilder();
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
            String apiUrl = baseUrl + "serviceKey=" + serviceKey + "&itemName=" + encodedName + "&type=json";
            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }

                in.close();

            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }

            return parseJsonResponse(result.toString());
        } catch (Exception e){
            System.out.println("e = " + e);
        }
        return null;
    }
    public String parseJsonResponse(String jsonResponse){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode itemsArray = objectMapper.readTree(jsonResponse).get("body").get("items");

            if (itemsArray == null) {
                return null;
            }
            String typeName = null;
            for (JsonNode item : itemsArray) {
                typeName = item.get("TYPE_NAME").asText();
            }


            return typeName;
        } catch (JsonProcessingException e) {
            System.out.println("JsonProcessingException = " + e);
            return null;
        }
    }
}
