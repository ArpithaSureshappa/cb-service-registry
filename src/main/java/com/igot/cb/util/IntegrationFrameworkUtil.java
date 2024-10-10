package com.igot.cb.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.cb.entity.IntegrationModel;
import com.igot.cb.entity.ServiceLocatorEntity;
import com.igot.cb.exceptions.CustomException;
import com.igot.cb.plugins.ContentPartnerPluginService;
import com.igot.cb.plugins.ContentSource;
import com.igot.cb.plugins.config.ContentPartnerServiceFactory;
import com.igot.cb.repository.CallExternalService;
import com.igot.cb.config.IntegrationConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Slf4j
public class IntegrationFrameworkUtil {

    @Autowired
    private IntegrationConfig config;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CallExternalService callExternalService;

    @Autowired
    private DataTransformUtility dataTransformUtility;

    @Autowired
    private ContentPartnerServiceFactory contentPartnerServiceFactory;


    public Object callExternalServiceApi(
            IntegrationModel integrationModel, ServiceLocatorEntity serviceLocator) throws JsonProcessingException {

        ObjectNode requestObject = this.createRequestObject(serviceLocator, integrationModel);
        log.info("IntegrationFrameworkUtil::requestObject {} ", requestObject);

        Object responseObject = callExternalService.fetchResult(getIntegrationFrameWorkUrl(), requestObject);
        log.info("Got successful response from external system service");
        if (integrationModel.getPartnerCode() != null) {
            JsonNode response = dataTransformUtility.callContentPartnerReadAPIByPartnerCode(integrationModel.getPartnerCode());
            if (!response.path("trasformContentJson").isMissingNode()) {
                List<Object> contentJson = mapper.convertValue(response.get("trasformContentJson"), new TypeReference<List<Object>>() {});
                Object transformData = dataTransformUtility.transformData(responseObject, contentJson);
                if (transformData != null) {
                    return transformData;
                } else {
                    return responseObject;
                }
            }
        } else {
            return responseObject;
        }
        return responseObject;
    }

    private ObjectNode mergeHeaders(ObjectNode secureHeader, ObjectNode reqHeader) {
        ObjectNode mergedHeader = mapper.createObjectNode();
        mergedHeader.setAll(secureHeader);
        mergedHeader.setAll(reqHeader);
        return mergedHeader;
    }

    private ObjectNode createRequestObject(ServiceLocatorEntity serviceLocator, IntegrationModel integrationModel) {
        ObjectNode requestObject = mapper.createObjectNode();
        requestObject.put("url", serviceLocator.getUrl());
        requestObject.put("isFormData", serviceLocator.isFormData());
        log.info("url {}", serviceLocator.getUrl());
        requestObject.put("requestMethod", serviceLocator.getRequestMethod().name());
        requestObject.put("operationType", serviceLocator.getOperationType());
        ObjectNode reqHeaderNode = mapper.createObjectNode();
        reqHeaderNode.put("content-type", "*/*");
        if (serviceLocator.isSecureHeader()) {
            if (integrationModel.getPartnerCode() != null) {
                String accessToken = "";
                ContentSource contentSource = ContentSource.fromPartnerCode(integrationModel.getPartnerCode());
                if (contentSource != null) {
                    ContentPartnerPluginService service = contentPartnerServiceFactory.getContentPartnerPluginService(contentSource);
                    JsonNode jsonNode=mapper.createObjectNode();
                    ((ObjectNode)jsonNode).put("urlSegment",serviceLocator.getUrlSegment());
                    accessToken=service.generateAuthHeader(jsonNode);
                    reqHeaderNode.put("Authorization", accessToken);
                }
            }
            ObjectNode secureHeader = reqHeaderNode;
            if (integrationModel.getHeaderMap() != null && !integrationModel.getHeaderMap().isEmpty()) {
                ObjectNode requestHeader = getRequestHeader(integrationModel);
                reqHeaderNode = mergeHeaders(secureHeader, requestHeader);
            } else {
                reqHeaderNode = secureHeader;
            }
        }
        requestObject.putPOJO("requestHeader", reqHeaderNode);

        if (integrationModel.getRequestBody() != null) {
            requestObject.putPOJO("requestBody", integrationModel.getRequestBody());
        }
        requestObject.put("serviceCode", serviceLocator.getServiceCode());
        requestObject.put("serviceName", serviceLocator.getServiceName());
        requestObject.put("serviceDescription", serviceLocator.getServiceDescription());
        requestObject.put("strictCache", integrationModel.getStrictCache());
        requestObject.put("strictCacheTimeInMinutes", integrationModel.getStrictCacheTimeInMinutes());
        requestObject.put("alwaysDataReadFromCache", integrationModel.isAlwaysDataReadFromCache());
        return requestObject;
    }

    public ObjectNode getRequestHeader(IntegrationModel integrationModel) {
        Map<String, String> headerMap = integrationModel.getHeaderMap();
        // Create the ObjectNode to store the request header
        ObjectNode reqHeaderNode = mapper.createObjectNode();
        // Iterate over the headerMap and add key-value pairs to reqHeaderNode
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                reqHeaderNode.put(key, value);
            }
        }
        return reqHeaderNode;
    }

    private StringBuilder getIntegrationFrameWorkUrl() {
        StringBuilder uriBuilder = new StringBuilder();
        return (uriBuilder.append(config.getIntegrationFwHost())
                .append(config.getIntegrationFwPath()));
    }

}
