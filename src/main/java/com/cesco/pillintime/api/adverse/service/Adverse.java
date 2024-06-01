package com.cesco.pillintime.api.adverse.service;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
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
public class Adverse {

    private final PlanRepository planRepository;

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

    public Map<String, String> DURSearch(String drugName, Member targetMember) {
        String adverseSet = readyOpenApi(DurInfoUrl, drugName);
        if(adverseSet == null) {
            return null;
        }
        List<String> adverseNameList = Arrays.asList(adverseSet.split(","));
        Map<String, String> search = search(adverseNameList, drugName);

        if(targetMember != null) {
            Set<String> medicAdverseList = Dupl(targetMember);
            search.putAll(otherSearch(medicAdverseList, drugName));
        }

        return search;
    }

    // =================================================================================
    public Map<String, String> search(List<String> adverseNameList, String drugName) {
        Map<String, String> typeNameList = new HashMap<>();
        Map<String, String> serviceUrlList = Map.of(
                "노인주의", SeniorUrl,
                "특정연령대금기", SpecificUrl,
                "용량주의", DosageUrl,
                "투여기간주의", PeriodUrl,
                "임부금기", PregnantUrl
        );

        for( String adverseName : adverseNameList){
            String targetUrl = serviceUrlList.get(adverseName); // api에 없으면 null
            if( targetUrl == null ) {
                typeNameList.put(adverseName,"");
                continue;
            }
            String adverseDescription = readyOpenApi(targetUrl, drugName);
            typeNameList.put(adverseName, adverseDescription == null ? "" : adverseDescription); // 부작용이 있는데 설명 없는 것은 ""으로 통일했어요
        }

        return typeNameList;
    }
    public Map<String, String> otherSearch(Set<String> medicAdverseList, String drugName) {
        Map<String, String> typeNameList = new HashMap<>();
        Map<String, String> serviceUrlList = Map.of(
//                "병용금기", CombineUrl,
                "효능군중복", DuplicateUrl
//                "서방정분할주의", DivideUrl
        );
        serviceUrlList.forEach((key,value) -> {
            String adverse = readyOpenApi(value,drugName);
            if(medicAdverseList.contains(adverse)){
                typeNameList.put(key,drugName);
            }
        });

        return typeNameList;
    }
    public String readyOpenApi(String adverseUrl, String drugName){
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
            int totalCount = body.get("totalCount").asInt(); // boolean items = body.has("items");
            if(totalCount == 0)
                return "";
            String value = body.get("items").get(0).get(key).asText(); // itemsArray == Json 배열
            if(value == "null") return "";
            return value.replaceAll("[\\\\\"]", "");
        } catch (JsonProcessingException e) {
            System.out.println("JsonProcessingException = " + e);
            return null;
        }
    }

    public Set<String> Dupl(Member targetMember){ // 복용 중인 약들의 효능군 중복 확인
        Set<String> medicationList = planRepository.findUniqueMedicineName(targetMember).orElse(null);
//                new HashSet<>();
//        medicationList.add("닉신정");
//        medicationList.add("코페낙주3밀리리터");
//        medicationList.add("딜라젠정12.5mg");
        Set<String> sersNameList = new HashSet<>();
        for(String drugName : medicationList) {
            String s = readyOpenApi(DuplicateUrl, drugName);
            if(!s.equals(""))
                sersNameList.add(readyOpenApi(DuplicateUrl, drugName));
        }
        if(sersNameList == null)
            sersNameList.add("");
        return sersNameList;
    }
}
