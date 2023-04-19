package hexlet.code.configs;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.spring.webmvc.RollbarSpringConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration()
@ComponentScan({
        "hexlet.code",
        "com.rollbar.spring"
})
public class RollbarConfig {
    @Bean
    public Rollbar rollbar() {
        return new Rollbar(getRollbarConfigs("${ROLLBAR_TOKEN}"));
    }

    private Config getRollbarConfigs(String accessToken) {
        return RollbarSpringConfigBuilder.withAccessToken(accessToken)
                .environment("development")
                .build();
    }
}
