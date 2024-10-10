package com.igot.cb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IntegrationModel {
    @JsonProperty("requestBody")
    private JsonNode requestBody;
    @JsonProperty("urlMap")
    private Map<String, String> urlMap;
    @JsonProperty("serviceCode")
    private String serviceCode;
    @JsonProperty("strictCache")
    private Boolean strictCache;
    @JsonProperty("headerMap")
    private Map<String, String> headerMap;
    @JsonProperty("hostAddress")
    private String hostAddress;
    @JsonProperty("strictCacheTimeInMinutes")
    private long strictCacheTimeInMinutes;
    @JsonProperty("alwaysDataReadFromCache")
    private boolean alwaysDataReadFromCache;
    @JsonProperty("partnerCode")
    private String partnerCode;
    @JsonProperty("transformJson")
    private List<Object> transformJson;
}
