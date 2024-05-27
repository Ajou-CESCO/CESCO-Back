package com.cesco.pillintime.api.adverse.service;

import com.cesco.pillintime.api.adverse.dto.AdverseDto;
import com.cesco.pillintime.api.medicine.dto.MedicineDto;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AdverseService {

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

    public List<AdverseDto> search(String drugName) {
        if(drugName.isEmpty()) {
            throw new CustomException(ErrorCode.MEDICINE_NAME_IS_EMPTY);
        }
        List<AdverseDto> adverseDtoList = new ArrayList<>();
        List<String> serviceUrlList = Arrays.asList(
//                serviceCombineUrl,
//                getServiceSeniorUrl,
                serviceDurInfoUrl // commented out in the original code
//                serviceSpecificUrl,
//                ServiceDosageUrl,
//                servicePeriodUrl,
//                serviceDuplicateUrl,
//                serviceDivideUrl,
//                servicePregnantUrl
        );

        for (String url : serviceUrlList) {
            AdverseDto adverseDto = circle(url, drugName);
            System.out.println("adverseDto for URL = " + adverseDto);
            if (adverseDto != null) {
                adverseDtoList.add(adverseDto);
            }
        }
        System.out.println("adverseDtoList = " + adverseDtoList);

        return null;
    }

    // =================================================================================
    public AdverseDto circle (String baseUrl, String name){
        System.out.println("AdverseService.circle");
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
            AdverseDto adverseDto = parseJsonResponse(result.toString());
            System.out.println("adverseDto for parseJsonResponse = " + adverseDto);
            return adverseDto;
        } catch (Exception e) {
            System.out.println("Exception = " + e);
        }
        return null;
    }
    public static AdverseDto parseJsonResponse(String jsonResponse){
        AdverseDto adverseDto = new AdverseDto();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("AdverseService.parseJsonResponse");
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(jsonResponse);
            JsonNode bodyNode = jsonNode.get("body");

            JsonNode itemsArray = bodyNode.get("items");

            if (itemsArray == null) {
                return null;
            }

            for (JsonNode item : itemsArray) {
                adverseDto.setItemName(item.get("ITEM_NAME").asText());
                adverseDto.setTypeName(item.get("TYPE_NAME").asText());
                adverseDto.setIngrName(item.get("INGR_NAME").asText());
                System.out.println("adverseDto for in = " + adverseDto.getItemName() + adverseDto.getTypeName() );
            }

            System.out.println("adverseDto for out = " + adverseDto.getItemName() + adverseDto.getTypeName() );

            return adverseDto;
        } catch (JsonProcessingException e) {
            System.out.println("JsonProcessingException = " + e);
            return null;
        }
    }
}
