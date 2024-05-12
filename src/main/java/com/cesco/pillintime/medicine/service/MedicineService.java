package com.cesco.pillintime.medicine.service;

import com.cesco.pillintime.medicine.dto.MedicineDto;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicineService {

    @Value("${EASY_DRUG_INFO_SERVICE_URL}")
    private String serviceUrl;

    @Value("${EASY_DRUG_INFO_SERVICE_KEY}")
    private String serviceKey;

    public List<MedicineDto> getMedicineInfoByName(String name) {
        try {
            StringBuilder result = new StringBuilder();

            String encodedName = URLEncoder.encode(name, "UTF-8");
            String apiUrl = serviceUrl + "serviceKey=" + serviceKey + "&itemName=" + encodedName + "&type=json";

            return getMedicineDtoList(result, apiUrl);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    public Optional<List<MedicineDto>> getMedicineByMedicineId(Long medicineId) {
        try {
            StringBuilder result = new StringBuilder();

            String apiUrl = serviceUrl + "serviceKey=" + serviceKey + "&itemSeq=" + medicineId + "&type=json";

            return Optional.of(getMedicineDtoList(result, apiUrl));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    // ===========================================================================

    private List<MedicineDto> getMedicineDtoList(StringBuilder result, String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        urlConnection.disconnect();

        return parseJsonResponse(result.toString());
    }

    public static List<MedicineDto> parseJsonResponse(String jsonResponse) throws JsonProcessingException {
        List<MedicineDto> medicineDtoList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        JsonNode bodyNode = jsonNode.get("body");

        JsonNode itemsArray = bodyNode.get("items");
        for (JsonNode item : itemsArray) {
            MedicineDto medicineDto = new MedicineDto();

            medicineDto.setCompanyName(removeNewLines(item.get("entpName").asText()));
            medicineDto.setMedicineName(removeNewLines(item.get("itemName").asText()));
            medicineDto.setMedicineCode(removeNewLines(item.get("itemSeq").asText()));

            String itemImage = ("null".equals(item.get("itemImage").asText())) ? "" : removeNewLines(item.get("itemImage").asText());
            medicineDto.setMedicineImage(itemImage);

            String medicineEffect = removeNewLines(item.get("efcyQesitm").asText());
            medicineEffect = medicineEffect.replaceAll("이 약은 ", "");
            medicineEffect = medicineEffect.replaceAll("에 사용합니다.", "");
            medicineDto.setMedicineEffect(medicineEffect);

            medicineDto.setUseMethod(removeNewLines(item.get("useMethodQesitm").asText()));
            medicineDto.setUseWarning(removeNewLines(item.get("atpnWarnQesitm").asText()));
            medicineDto.setUseSideEffect(removeNewLines(item.get("seQesitm").asText()));
            medicineDto.setDepositMethod(removeNewLines(item.get("depositMethodQesitm").asText()));

            medicineDtoList.add(medicineDto);
        }

        return medicineDtoList;
    }

    private static String removeNewLines(String text) {
        return text.replaceAll("\n", "");
    }
}