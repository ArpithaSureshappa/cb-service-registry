package com.igot.service_locator.plugins.cornell;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
public class CornellAuth {

    @Value("${cornell.client.code}")
    private String cornellClientCode;

    @Value("${cornell.client.secret}")
    private String cornellClientSecret;

    public String generateAuthHeader(String urlSegment){
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
