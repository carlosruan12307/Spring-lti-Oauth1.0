package com.example.demo.controllers;

import java.util.Map;
import java.util.function.Consumer;
import java.util.Arrays;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.example.demo.services.LtiOutcomeService;
import java.security.SecureRandom;
import net.oauth.OAuth;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import java.util.Base64;

import java.security.GeneralSecurityException;

import java.nio.charset.Charset;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.util.TreeMap;
@RestController
public class testeController {
    @Autowired
    private LtiOutcomeService ltiOutcomeService;

    @PostMapping(path = "/testing", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> testing(@RequestParam Map<String, String> paramMap) {
      
        return ltiOutcomeService.getLtiGrades(paramMap.get("lis_result_sourcedid"));
    }

  
}
