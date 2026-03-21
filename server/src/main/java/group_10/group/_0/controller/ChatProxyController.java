package group_10.group._0.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chat Service", description = "Proxy API đến NodeJS chat service. Xem đầy đủ tại: https://github.com/dainji04/LVTN-Management/blob/main/chat-service/API_TESTING.md")
public class ChatProxyController {

    @Value("${nodejs.url}")
    private String nodejsUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/**")
    @Operation(summary = "Proxy GET", description = "Forward tất cả GET request đến NodeJS chat service")
    public ResponseEntity<String> proxyGet(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String path = request.getRequestURI().replace("/chat", "");
            String query = request.getQueryString();
            if (query != null) path += "?" + query;

            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null) headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            return restTemplate.exchange(nodejsUrl + path, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("{\"error\": \"Chat service không khả dụng\"}");
        }
    }

    @PostMapping("/**")
    @Operation(summary = "Proxy POST", description = "Forward tất cả POST request đến NodeJS chat service")
    public ResponseEntity<String> proxyPost(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) String body) {
        try {
            String path = request.getRequestURI().replace("/chat", "");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (authHeader != null) headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            return restTemplate.exchange(nodejsUrl + path, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("{\"error\": \"Chat service không khả dụng\"}");
        }
    }

    @PutMapping("/**")
    @Operation(summary = "Proxy PUT", description = "Forward tất cả PUT request đến NodeJS chat service")
    public ResponseEntity<String> proxyPut(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) String body) {
        try {
            String path = request.getRequestURI().replace("/chat", "");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (authHeader != null) headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            return restTemplate.exchange(nodejsUrl + path, HttpMethod.PUT, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("{\"error\": \"Chat service không khả dụng\"}");
        }
    }

    @DeleteMapping("/**")
    @Operation(summary = "Proxy DELETE", description = "Forward tất cả DELETE request đến NodeJS chat service")
    public ResponseEntity<String> proxyDelete(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) String body) {
        try {
            String path = request.getRequestURI().replace("/chat", "");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (authHeader != null) headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            return restTemplate.exchange(nodejsUrl + path, HttpMethod.DELETE, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("{\"error\": \"Chat service không khả dụng\"}");
        }
    }
}