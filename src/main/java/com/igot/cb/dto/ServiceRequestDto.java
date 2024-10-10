package com.igot.cb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Data
@Setter
@Getter
public class ServiceRequestDto {
    @JsonProperty("headerMap")
    private Map<String, String> headerMap;
    @JsonProperty("urlMap")
    private Map<String, String> urlMap;
    @JsonProperty("requestMap")
    private Map<String, String> requestMap;
}
