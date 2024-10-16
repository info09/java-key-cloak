package app.profile_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfiguration {
    @Bean
    public CorsFilter corsFilter() {
        var corConfiguration = new org.springframework.web.cors.CorsConfiguration();

        corConfiguration.addAllowedOrigin("*");
        corConfiguration.addAllowedHeader("*");
        corConfiguration.addAllowedMethod("*");

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corConfiguration);

        return new CorsFilter(source);
    }
}
