package com.cesco.pillintime.api.adverse.service;

import com.cesco.pillintime.api.adverse.dto.AdverseDto;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        circle(serviceCombineUrl,drugName);
        circle(getServiceSeniorUrl,drugName);
        circle(serviceDurInfoUrl,drugName);
        circle(serviceSpecificUrl,drugName);
        circle(ServiceDosageUrl,drugName);
        circle(servicePeriodUrl,drugName);
        circle(serviceDuplicateUrl,drugName);
        circle(serviceDivideUrl,drugName);
        circle(servicePregnantUrl,drugName);

        return null;
    }

    // ========
    public void circle (String baseUrl, String name){
        try {
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
            String apiUrl = baseUrl + "serviceKey=" + serviceKey + "&itemName=" + encodedName + "&type=json";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                System.out.println("Response: " + response);
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("e = " + e);
        }
    }
}
