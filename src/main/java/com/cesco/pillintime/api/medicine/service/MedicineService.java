package com.cesco.pillintime.api.medicine.service;

import com.cesco.pillintime.api.adverse.service.AdverseService;
import com.cesco.pillintime.api.medicine.dto.MedicineDto;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.security.SecurityUtil;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final AdverseService adverseService;

    private final PlanRepository planRepository;
    private final SecurityUtil securityUtil;
    private final MemberRepository memberRepository;

    @Value("${EASY_DRUG_INFO_SERVICE_URL}")
    private String serviceUrl;

    @Value("${EASY_DRUG_INFO_SERVICE_KEY}")
    private String serviceKey;

    public List<MedicineDto> getMedicineInfoByName(String name, Long memberId) {
        if (name.isEmpty()) {
            throw new CustomException(ErrorCode.MEDICINE_NAME_IS_EMPTY);
        }

        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        try {
            StringBuilder result = new StringBuilder();

            // 내가 복용중인 약물 이름, 종류 조회
            List<Map<String, String>> takingMedicineList = planRepository.findTakingMedicine(targetMember);

            // 검색한 키워드에 대한 약물 조회
            String encodedName = URLEncoder.encode(name, "UTF-8");
            String apiUrl = serviceUrl + "serviceKey=" + serviceKey + "&itemName=" + encodedName + "&type=json";

            List<MedicineDto> medicineDtoList = getMedicineDtoList(result, apiUrl);
            for (MedicineDto medicineDto : medicineDtoList) {
                String medicineName = medicineDto.getMedicineName();

                Map<String, String> adverseMap = adverseService.DURSearch(medicineName, takingMedicineList);
                medicineDto.setAdverseMap(adverseMap);
            }

            return medicineDtoList;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EXTERNAL_SERVER_ERROR);
        }
    }

    public Optional<List<MedicineDto>> getMedicineByMedicineId(Long medicineId, Member targetMember) {
        try {
            StringBuilder result = new StringBuilder();

            String apiUrl = serviceUrl + "serviceKey=" + serviceKey + "&itemSeq=" + medicineId + "&type=json";

            List<MedicineDto> medicineDtoList = getMedicineDtoList(result, apiUrl);
            List<Map<String,String>> takingMedicineList = planRepository.findTakingMedicine(targetMember);

            for (MedicineDto medicineDto : medicineDtoList) {
                Map<String, String> a = adverseService.DURSearch(medicineDto.getMedicineName(), takingMedicineList);
                if (a == null) continue;
                medicineDto.setAdverseMap(a);
            }

            return Optional.of(medicineDtoList);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EXTERNAL_SERVER_ERROR);
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

    public List<MedicineDto> parseJsonResponse(String jsonResponse) throws JsonProcessingException {
        List<MedicineDto> medicineDtoList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        JsonNode bodyNode = jsonNode.get("body");

        JsonNode itemsArray = bodyNode.get("items");
        if (itemsArray == null) {
            return medicineDtoList;
        }

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