package com.igot.cb.util;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.cb.exceptions.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Component
@Slf4j
public class DataTransformUtility {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CbServerProperties cbServerProperties;

    @Autowired
    private ObjectMapper mapper;

    public JsonNode callContentPartnerReadAPIByPartnerCode(String partnerCode) {
        log.info("DataTransformUtility :: callContentPartnerReadAPIByPartnerCode");
        String url = cbServerProperties.getContentPartnerBaseUrl() + cbServerProperties.getContentPartnerReadApiUrl() + partnerCode;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                JsonNode.class
        );
        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode jsonNode = response.getBody();
            return jsonNode.path("result");
        } else {
            throw new CustomException(Constants.ERROR,"Failed to retrieve data. Status code: " + response.getStatusCodeValue(), HttpStatus.BAD_REQUEST);
        }
    }

    public Object transformData(Object source, List<Object> responseFormat) throws JsonProcessingException {
        String inputJson = "";
        Object transformedOutput;
        try {
            inputJson = mapper.writeValueAsString(source);
            Chainr chainr = Chainr.fromSpec(responseFormat);
            transformedOutput = chainr.transform(JsonUtils.jsonToObject(inputJson));
            return transformedOutput;
        } catch (Exception e) {
            ObjectNode response = mapper.createObjectNode();
            ObjectNode errorMessage = mapper.createObjectNode();
            errorMessage.put("message", "Jolt Transformation Spec Error, Check in responseFormat");
            ObjectNode extResponse = mapper.valueToTree(source);
            response.set("error", errorMessage);
            response.set("response", extResponse);
            return response;
        }
    }
}
