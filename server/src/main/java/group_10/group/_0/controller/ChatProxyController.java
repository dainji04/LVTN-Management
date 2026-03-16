package group_10.group._0.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chat Service", description = "Proxy API đến NodeJS chat service")
public class ChatProxyController {

    @Value("${nodejs.url}")
    private String nodejsUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/**")
    @Operation(summary = "Proxy GET", description = "Forward GET request đến NodeJS chat service")
    public ResponseEntity<String> proxyGet(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String path = request.getRequestURI().replace("/chat", "");

        HttpHeaders headers = new HttpHeaders();
        if (authHeader != null) {
            headers.set("Authorization", authHeader);
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(nodejsUrl + path, HttpMethod.GET, entity, String.class);
    }

    @PostMapping("/**")
    @Operation(summary = "Proxy POST", description = "Forward POST request đến NodeJS chat service")
    public ResponseEntity<String> proxyPost(
            HttpServletRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) String body) {
        String path = request.getRequestURI().replace("/chat", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authHeader != null) {
            headers.set("Authorization", authHeader);
        }

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(nodejsUrl + path, HttpMethod.POST, entity, String.class);
    }
}