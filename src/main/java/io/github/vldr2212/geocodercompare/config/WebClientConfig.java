package io.github.vldr2212.geocodercompare.config;

import io.github.vldr2212.geocodercompare.properties.GeocoderHttpProperties;
import io.netty.channel.ChannelOption;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(GeocoderHttpProperties.class)
public class WebClientConfig {

    @Bean
    public WebClient geocoderWebClient(WebClient.Builder builder, GeocoderHttpProperties properties) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) properties.connectTimeout().toMillis())
                .responseTimeout(properties.responseTimeout());

        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}