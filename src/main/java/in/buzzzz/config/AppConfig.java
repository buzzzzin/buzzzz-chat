package in.buzzzz.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;

/**
 * @author jitendra on 26/9/15.
 */
@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CloseableHttpAsyncClient clientHttpRequestFactory() {
        return new HttpComponentsAsyncClientHttpRequestFactory().getHttpAsyncClient();
    }
}
