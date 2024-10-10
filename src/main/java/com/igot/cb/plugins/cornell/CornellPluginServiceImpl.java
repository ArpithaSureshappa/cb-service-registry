package com.igot.cb.plugins.cornell;

import com.fasterxml.jackson.databind.JsonNode;
import com.igot.cb.plugins.ContentPartnerPluginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
public class CornellPluginServiceImpl implements ContentPartnerPluginService {

    @Value("${cornell.client.code}")
    private String cornellClientCode;

    @Value("${cornell.client.secret}")
    private String cornellClientSecret;

    @Override
    public String generateAuthHeader(JsonNode jsonNode) {
        String urlSegment = jsonNode.get("urlSegment").asText();
        if (jsonNode.get("urlSegment") == null) {
            urlSegment = "";
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        String toHash = urlSegment + cornellClientCode + timestamp + cornellClientSecret;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashInBytes = null;
        try {
            hashInBytes = md.digest(toHash.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashInBytes) {
            hashString.append(String.format("%02x", b));
        }
        String authHash = hashString.toString();
        return cornellClientCode + "." + timestamp + "." + authHash;
    }
}
