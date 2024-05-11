package com.cesco.pillintime.medicine.service;

import com.cesco.pillintime.medicine.dto.MedicineDto;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
            String apiUrl = serviceUrl + "serviceKey=" + serviceKey + "&itemName=" + encodedName;

            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String returnLine;
            while((returnLine = bufferedReader.readLine()) != null) {
                result.append(returnLine).append("\n");
            }

            urlConnection.disconnect();

            return parseXmlResponse(result.toString());
        } catch (Exception e) {
            System.out.print(e);
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    private List<MedicineDto> parseXmlResponse(String xmlResponse) throws Exception {
        System.out.println(xmlResponse);

        List<MedicineDto> medicineDtoList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(xmlResponse));
        Document document = builder.parse(inputSource);

        NodeList itemNodes = document.getElementsByTagName("item");
        for (int i = 0; i < itemNodes.getLength(); i++) {
            Element itemElement = (Element) itemNodes.item(i);
            MedicineDto medicineDto = new MedicineDto();

            medicineDto.setCompanyName(getTagValue(itemElement, "entpName"));
            medicineDto.setMedicineName(getTagValue(itemElement, "itemName"));
            medicineDto.setMedicineCode(getTagValue(itemElement, "itemSeq"));
            medicineDto.setMedicineImage(getTagValue(itemElement, "itemImage"));
            medicineDto.setMedicineEffect(getTagValue(itemElement, "efcyQesitm"));
            medicineDto.setUseMethod(getTagValue(itemElement, "useMethodQesitm"));
            medicineDto.setUseWarning(getTagValue(itemElement, "atpnWarnQesitm"));
            medicineDto.setUseSideEffect(getTagValue(itemElement, "seQesitm"));
            medicineDto.setDepositMethod(getTagValue(itemElement, "depositMethodQesitm"));

            medicineDtoList.add(medicineDto);
        }

        return medicineDtoList;
    }

    private String getTagValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            String tagValue = node.getTextContent();
            tagValue = tagValue.replaceAll("\\n", "");

            if (tagName.equals("efcyQesitm")) {
                tagValue = tagValue.replaceAll("이 약은 ", "");
                tagValue = tagValue.replaceAll("에 사용합니다.", "");
            }
            return tagValue;
        }
        return null;
    }


}