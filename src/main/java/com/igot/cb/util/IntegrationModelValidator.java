package com.igot.cb.util;

import com.igot.cb.entity.IntegrationModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igot.cb.exceptions.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class IntegrationModelValidator {

    @Autowired
    private ObjectMapper mapper;


    public void validateModel(IntegrationModel integrationModel) {

        if (integrationModel.getStrictCache() == null) {
            integrationModel.setStrictCache(Boolean.FALSE);
        }

        if (integrationModel.getRequestBody() != null) {
            boolean isValidJson = false;
            try {
                mapper.writeValueAsString(integrationModel.getRequestBody());
                isValidJson = true;
            } catch (JsonProcessingException e) {

            }
            if (!isValidJson) {
                throw new CustomException("REQUEST_BODY", "Request body is not a valid json object",HttpStatus.BAD_REQUEST);
            }
        }
        if (StringUtils.isBlank(integrationModel.getServiceCode())) {
            throw new CustomException("SERVICE_CODE", "Service Code is missing in the request", HttpStatus.BAD_REQUEST);
        }
    }
}
