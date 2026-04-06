package group_10.group._0;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RestClientCustomizer cleanProxyHeadersCustomizer() {
        return builder -> builder.requestInterceptor((request, body, execution) -> {
            // 1. Tiêu diệt toàn bộ các header proxy do Gateway tự đẻ ra
            request.getHeaders().remove("X-Forwarded-Host");
            request.getHeaders().remove("X-Forwarded-Port");
            request.getHeaders().remove("X-Forwarded-Proto");
            request.getHeaders().remove("X-Forwarded-For");
            request.getHeaders().remove("Forwarded");

            // 2. Ép cứng đích đến là host của Chat Service Node.js
            request.getHeaders().set("Host", "chat-service-social-media.onrender.com");

            return execution.execute(request, body);
        });
    }
}