package com.igot.service_locator.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.service_locator.entity.IntegrationModel;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.igot.service_locator.plugins.AuthPluginService;
import com.igot.service_locator.repository.CallExternalService;
import com.igot.service_locator.config.IntegrationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;


@Component
@Slf4j
public class IntegrationFrameworkUtil {

    private final IntegrationConfig config;
    private final ObjectMapper mapper;
    private final CallExternalService callExternalService;
    private final DataTransformUtility dataTransformUtility;
    private final AuthPluginService authPluginService;

    public IntegrationFrameworkUtil(IntegrationConfig config,
                                    ObjectMapper mapper,
                                    CallExternalService callExternalService,
                                    DataTransformUtility dataTransformUtility,
                                    AuthPluginService authPluginService) {
        this.config = config;
        this.mapper = mapper;
        this.callExternalService = callExternalService;
        this.dataTransformUtility = dataTransformUtility;
        this.authPluginService = authPluginService;
    }
    public Object callExternalServiceApi(
        IntegrationModel integrationModel, ServiceLocatorEntity serviceLocator) throws JsonProcessingException {

        ObjectNode requestObject = this.createRequestObject(serviceLocator, integrationModel);
        log.info("IntegrationFrameworkUtil::requestObject {} ", requestObject);

        Object responseObject = callExternalService.fetchResult(dataTransformUtility.getIntegrationFrameWorkUrl(), requestObject);
        JsonNode responseJson = mapper.convertValue(responseObject, new TypeReference<JsonNode>() {});
        JsonNode jsonNode = responseJson.get("responseData");
        log.info("Got successful response from external system service");

        if (integrationModel.getPartnerCode() != null) {
            JsonNode response = dataTransformUtility.callContentPartnerReadAPIByPartnerCode(integrationModel.getPartnerCode());
            JsonNode transformContent = response.path("transformContentViaApi");
            if (transformContent!=null && !transformContent.isEmpty()&&!transformContent.isMissingNode()) {
                log.info("Inside transformContentViaApi: {}", integrationModel.getPartnerCode());
                List<Object> contentJson = mapper.convertValue(response.get("transformContentViaApi"), new TypeReference<List<Object>>() {});
                Object transformData = dataTransformUtility.transformData(jsonNode, contentJson);
                if (transformData != null) {
                    return transformData;
                } else {
                    return jsonNode;
                }
            } else {
                return jsonNode;
            }
        } else if(serviceLocator.getPartnerCode() != null) {
            log.info("serviceLocator.getPartnerCode(): {}", serviceLocator.getPartnerCode());
            JsonNode response = dataTransformUtility.callContentPartnerReadAPIByPartnerCode(serviceLocator.getPartnerCode());
            JsonNode transformProgress = response.path("transformProgressViaApi");
            if (transformProgress!=null && !transformProgress.isEmpty()&&!transformProgress.isMissingNode()) {
                log.info("Inside transformProgressViaApi: {}", serviceLocator.getPartnerCode());
                List<Object> contentJson = mapper.convertValue(response.get("transformProgressViaApi"), new TypeReference<List<Object>>() {});
                Object transformData = dataTransformUtility.transformData(jsonNode, contentJson);
                if (transformData != null) {
                    return transformData;
                } else {
                    return jsonNode;
                }
            } else {
                return jsonNode;
            }
        } else {
            return jsonNode;
        }

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
        log.info("url {}",serviceLocator.getUrl());
        requestObject.put("requestMethod", serviceLocator.getRequestMethod().name());
        requestObject.put("operationType", serviceLocator.getOperationType());
        ObjectNode reqHeaderNode = mapper.createObjectNode();
        reqHeaderNode.put("content-type", "*/*");
        ObjectNode secureHeader = reqHeaderNode;
        if (serviceLocator.isSecureHeader()) {
            JsonNode authPayload = serviceLocator.getAuthPayload();
            String accessToken = "";
            if (authPayload != null && !authPayload.isEmpty() && !authPayload.isMissingNode()) {
                accessToken = authPluginService.generateAuthHeader(serviceLocator.getAuthPayload());
                reqHeaderNode.put("Authorization", accessToken);
            }
            if (integrationModel.getHeaderMap() != null && !integrationModel.getHeaderMap().isEmpty()) {
                ObjectNode requestHeader = getRequestHeader(integrationModel);
                reqHeaderNode = mergeHeaders(secureHeader, requestHeader);
            }
        } else {
            if (integrationModel.getHeaderMap() != null && !integrationModel.getHeaderMap().isEmpty()) {
                ObjectNode requestHeader = getRequestHeader(integrationModel);
                reqHeaderNode = mergeHeaders(secureHeader, requestHeader);
            }
        }
        requestObject.putPOJO("requestHeader", reqHeaderNode);

        if (integrationModel.getRequestBody() != null) {
            requestObject.putPOJO("requestBody", integrationModel.getRequestBody());
        }
        requestObject.put("serviceCode", serviceLocator.getServiceCode());
        requestObject.put("serviceName", serviceLocator.getServiceName());
        requestObject.put("serviceDescription", serviceLocator.getServiceDescription());
        requestObject.put("strictCache", serviceLocator.isStrictCache());
        requestObject.put("strictCacheTimeInMinutes", serviceLocator.getStrictCacheTimeInMinutes());
        requestObject.put("alwaysDataReadFromCache",integrationModel.isAlwaysDataReadFromCache());
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

}
