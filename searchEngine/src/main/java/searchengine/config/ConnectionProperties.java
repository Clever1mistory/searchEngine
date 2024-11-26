package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "connection")
@Getter
@Setter
public class ConnectionProperties {
    private String url;
    private String userAgent;
    private String referrer;
    private Delay delay;

    @Getter
    @Setter
    public static class Delay {
        private int min;
        private int max;
    }
}