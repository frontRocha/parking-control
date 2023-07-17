package br.project.com.parkingcontrol.domain.email;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    private final RestTemplate restTemplate;
    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public void save(Email response) {
        emailRepository.save(response);
    }

    public String sendEmail(Email email,
                            String token) {
        String url = "http://localhost:8080/sending-email";
        HttpHeaders headers = createHeaders(token);
        HttpEntity<Email> requestEntity = createRequestEntity(email, headers);
        ResponseEntity<String> responseEntity = sendHttpRequest(url, HttpMethod.POST, requestEntity, String.class);
        String response = responseEntity.getBody();
        return response;
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

    private HttpEntity<Email> createRequestEntity(Email email,
                                                  HttpHeaders headers) {
        return new HttpEntity<>(email, headers);
    }

    private <T> ResponseEntity<T> sendHttpRequest(String url,
                                                  HttpMethod method,
                                                  HttpEntity<?> requestEntity,
                                                  Class<T> responseType) {
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }
}
