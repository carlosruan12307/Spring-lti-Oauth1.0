package com.example.demo.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.security.MessageDigest;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.http.HttpParameters;
import org.apache.http.client.methods.HttpPost;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;

import java.net.URLEncoder;
import org.apache.http.Header;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Service
public class LtiOutcomeService {

    private final String consumerKey = "ABC"; // Adicionado aspas
    private final String consumerSecret = "secret";
    private final String outcomeServiceUrl = "http://localhost/mod/lti/service.php";
  // Método para ler o arquivo XML
  private String readXmlFile(String filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
}
// Method to percent-encode a value
private String percentEncode(String value) {
    String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
    return encoded.replace("+", "%20")
                  .replace("*", "%2A")
                  .replace("%7E", "~");
                //   .replace("_", generateNonce(1))
                //   .replace("-", generateNonce(1));
}
    public ResponseEntity<String> getLtiGrades(String lisResultSourcedId) {
        try {
            // XML Payload (for example, to get grades)
            String xmlPayload = readXmlFile("src\\main\\resources\\teste.xml");
    
            // Calculate the body hash (oauth_body_hash)
            String bodyHash = calculateBodyHash(xmlPayload);
    
            // Create OAuth consumer with key and secret
            OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
    
            // Generate and percent-encode the nonce
            String oauthNonce = generateNonce(10); // exemplo de 10 caracteres
            String encodedNonce = percentEncode(oauthNonce);
    
            // Add oauth_body_hash and oauth_nonce manually
            HttpParameters additionalParams = new HttpParameters();
            additionalParams.put("oauth_body_hash", bodyHash);
            additionalParams.put("oauth_nonce", encodedNonce); // Adicione o nonce codificado
            consumer.setAdditionalParameters(additionalParams);
    
            // Create the HTTP POST request
            HttpPost request = new HttpPost(outcomeServiceUrl);
            StringEntity entity = new StringEntity(xmlPayload);
            request.setEntity(entity);
            request.setHeader("Content-Type", "application/xml; charset=UTF-8");
    
            // Sign the request with OAuth
            consumer.sign(request);
            System.out.println(request);
    
            // Imprime o cabeçalho Authorization
            String authorizationHeader = request.getFirstHeader("Authorization").getValue();
            System.out.println("Authorization Header: " + authorizationHeader);
    
            // Executa a requisição HTTP
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpResponse response = httpClient.execute(request);
                // Extrai o corpo da resposta
                String responseBody = EntityUtils.toString(response.getEntity());
    
                // Retorna a resposta como ResponseEntity
                return new ResponseEntity<>(responseBody, HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static final SecureRandom random = new SecureRandom();

    public static String generateNonce(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (random.nextInt(26) + 'a')); // Adiciona caracteres aleatórios
        }
        return sb.toString();
    }

    // Function to calculate the oauth_body_hash
    private  String calculateBodyHash(String body) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        byte[] digest = md.digest(bodyBytes);
        return percentEncode(Base64.getUrlEncoder().encodeToString(digest));
    }
    private String buildReadResultXml(String sourcedId) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<imsx_POXEnvelopeRequest xmlns=\"http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0\">\n" +
                "<imsx_POXHeader>\n" +
                "<imsx_POXRequestHeaderInfo>\n" +
                "<imsx_version>V1.0</imsx_version>\n" +
                "<imsx_messageIdentifier>" + generateUniqueMessageId() + "</imsx_messageIdentifier>\n" +
                "</imsx_POXRequestHeaderInfo>\n" +
                "</imsx_POXHeader>\n" +
                "<imsx_POXBody>\n" +
                "<readResultRequest>\n" +
                "<resultRecord>\n" +
                "<sourcedGUID>\n" +
                "<sourcedId>" + sourcedId + "</sourcedId>\n" +
                "</sourcedGUID>\n" +
                "</resultRecord>\n" +
                "</readResultRequest>\n" +
                "</imsx_POXBody>\n" +
                "</imsx_POXEnvelopeRequest>\n";
    }
    
    private String buildReplaceResultRequestXml(String sourcedId, Double score) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<imsx_POXEnvelopeRequest xmlns=\"http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0\">\n" +
                "<imsx_POXHeader>\n" +
                "<imsx_POXRequestHeaderInfo>\n" +
                "<imsx_version>V1.0</imsx_version>\n" +
                "<imsx_messageIdentifier>" + 32 + "</imsx_messageIdentifier>\n" + // Método para gerar um ID de mensagem único
                "</imsx_POXRequestHeaderInfo>\n" +
                "</imsx_POXHeader>\n" +
                "<imsx_POXBody>\n" +
                "<replaceResultRequest>\n" +
                "<resultRecord>\n" +
                "<sourcedGUID>\n" +
                "<sourcedId>" + "{\"data\":{\"instanceid\":\"4\",\"userid\":\"2\",\"typeid\":\"1\",\"launchid\":1089994207},\"hash\":\"5785f73c201877de8338f0ae557a794dddf5e61db0692a321b77257bb83d0e21\"}" + "</sourcedId>\n" +
                "</sourcedGUID>\n" +
                "<result>\n" +
                "<resultScore>\n" +
                "<language>en</language>\n" +
                "<textString>" + score + "</textString>\n" + // Aqui você insere a nota
                "</resultScore>\n" +
                "</result>\n" + 
                "</resultRecord>\n" +
                "</replaceResultRequest>\n" +
                "</imsx_POXBody>\n" +
                "</imsx_POXEnvelopeRequest>\n";
    }

     private String generateUniqueMessageId() {
        return UUID.randomUUID().toString(); // Gera um UUID único como uma String
    }

}
