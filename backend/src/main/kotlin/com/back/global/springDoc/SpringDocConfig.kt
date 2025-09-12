package com.back.global.springDoc

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(info = Info(title = "API 서버", version = "beta", description = "API 서버 문서입니다."))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
class SpringDocConfig {
    @Bean
    fun groupApiV1(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("apiV1")
            .pathsToMatch("/api/v1/**")
            .build()
    }

    @Bean
    fun groupController(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("controller")
            .pathsToExclude("/api/**")
            .build()
    }
}
