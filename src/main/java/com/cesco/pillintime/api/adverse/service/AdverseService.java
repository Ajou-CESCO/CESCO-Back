package com.cesco.pillintime.api.adverse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdverseService {

    @Value("${DRUG_AVERSE_COMB_SERVICE_URL}")
    private String CombineUrl;
    @Value("${DRUG_AVERSE_SENI_SERVICE_URL}")
    private String SeniorUrl;
    @Value("${DRUG_AVERSE_DUR_SERVICE_URL}")
    private String DurInfoUrl;
    @Value("${DRUG_AVERSE_SPEC_SERVICE_URL}")
    private String SpecificUrl;
    @Value("${DRUG_AVERSE_DOSA_SERVICE_URL}")
    private String DosageUrl;
    @Value("${DRUG_AVERSE_PERI_SERVICE_URL}")
    private String PeriodUrl;
    @Value("${DRUG_AVERSE_DUPL_SERVICE_URL}")
    private String DuplicateUrl;
    @Value("${DRUG_AVERSE_DIVI_SERVICE_URL}")
    private String DivideUrl;
    @Value("${DRUG_AVERSE_PREG_SERVICE_URL}")
    private String PregnantUrl;

    @Value("${EASY_DRUG_AVERSE_SERVICE_KEY}")
    private String serviceKey;

    public Map<String, String> DURSearch(String drugName, List<Map<String,String>> takingMedicineList) {

        // 입력받은 약물이 기본적으로 갖고 있는 부작용 목록 조회
        // 기본적으로 갖고 있는 부작용이 없을 경우, 기존 약물과 중복여부만 반환
        String adverseSet = requestDurApiBy(DurInfoUrl, drugName);
        List<String> adverseNameList = Arrays.asList(adverseSet.split(","));

        return new HashMap<>(adverseSearch(takingMedicineList, adverseNameList, drugName));
    }

    // =================================================================================

    public Map<String, String> adverseSearch(List<Map<String, String>> takingMedicineList, List<String> adverseNameList, String drugName) {
        Map<String, String> adverseMap = new HashMap<>();
        Map<String, String> serviceUrlList = Map.of(
                "노인주의", SeniorUrl,
                "특정연령대금기", SpecificUrl,
                "용량주의", DosageUrl,
                "투여기간주의", PeriodUrl,
                "임부금기", PregnantUrl
        );

        // 기존 복용중인 약물과 효능군중복 여부 확인
        if (takingMedicineList != null) {
            String adverse = requestDurApiBy(DuplicateUrl, drugName);
            adverseMap.put("sersName", adverse);

            if (!adverse.isEmpty()) {
                for (Map<String, String> takingMedicineMap : takingMedicineList) {
                    if (takingMedicineMap.containsValue(adverse)) {
                        adverseMap.put("효능군중복", drugName);
                    }
                }
            }
        }

        // 검색한 약물이 기본적으로 포함하는 부작용 확인
        for (String adverseName : adverseNameList){
            String targetUrl = serviceUrlList.get(adverseName);
            if (targetUrl == null) {
                continue;
            }

            String adverseDescription = requestDurApiBy(targetUrl, drugName);
            adverseMap.put(adverseName, adverseDescription.isEmpty() ? adverseName : adverseDescription);
        }

        return adverseMap;
    }

    public String requestDurApiBy(String adverseUrl, String drugName){
        StringBuilder result = new StringBuilder();
        String encodedName = URLEncoder.encode(drugName, StandardCharsets.UTF_8);
        String apiUrl = adverseUrl + "serviceKey=" + serviceKey + "&itemName=" + encodedName + "&type=json";
        try {
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
            if(adverseUrl.equals(DurInfoUrl))
                return parseJsonResponse(result.toString(),"TYPE_NAME  ");
            else if(adverseUrl.equals(CombineUrl))
                return parseJsonResponse(result.toString(),"");
            else if(adverseUrl.equals(DivideUrl))
                return parseJsonResponse(result.toString(),"");
            else if(adverseUrl.equals(DuplicateUrl))
                return parseJsonResponse(result.toString(),"SERS_NAME");
            else
                return parseJsonResponse(result.toString(),"PROHBT_CONTENT");

        } catch (IOException e){
            System.out.println("e = " + e);
            return null;
        }
    }
    public String parseJsonResponse(String jsonResponse, String key){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode body = objectMapper.readTree(jsonResponse).get("body");
            int totalCount;
            try {
                totalCount = body.get("totalCount").asInt(); // boolean items = body.has("items");
            } catch (Exception e) {
                totalCount = 0;
            }

            if (totalCount == 0)
                return "";
            String value = body.get("items").get(0).get(key).asText(); // itemsArray == Json 배열
            if(value == "null") return "";
            return value.replaceAll("[\\\\\"]", "");
        } catch (JsonProcessingException e) {
            System.out.println("JsonProcessingException = " + e);
            return null;
        }
    }

}
