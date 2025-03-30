package com.cryptoexchange.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cryptoExchangeOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Crypto Exchange API")
                .version("v1")
                .description("Demo cryptocurrency exchange with a simulated market, "
                        + "order matching engine and a single demo wallet."));
    }
}
