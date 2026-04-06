package group_10.group._0;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RestClientCustomizer cleanProxyHeadersCustomizer() {
        return builder -> builder.requestInterceptor((request, body, execution) -> {
            // Dọn sạch rác Proxy
            request.getHeaders().remove("X-Forwarded-Host");
            request.getHeaders().remove("X-Forwarded-Port");
            request.getHeaders().remove("X-Forwarded-Proto");
            request.getHeaders().remove("X-Forwarded-For");
            request.getHeaders().remove("Forwarded");

            // Ép cứng Host thành domain thật của Node.js (KHÔNG CÓ https://)
            request.getHeaders().set("Host", "lvtn-management-14vw.onrender.com");

            return execution.execute(request, body);
        });
    }
}