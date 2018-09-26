package com.example.mpcdemo;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;



@SpringBootApplication
@EnableSwagger2
public class MpcDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MpcDemoApplication.class, args);
	}
	
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.addAllowedMethod("OPTIONS");
		config.addAllowedMethod("HEAD");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("DELETE");
		config.addAllowedMethod("PATCH");
		config.addExposedHeader("Accept");
		config.addExposedHeader("Location");
		config.addExposedHeader("Content-Type");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@Configuration
	class SwaggerConfig {
		@Bean
		public Docket api() {
			return new Docket(DocumentationType.SWAGGER_2).select()
					.apis(RequestHandlerSelectors.basePackage("com.example.mpcdemo.controller")).paths(PathSelectors.any())
					.build().apiInfo(apiInfo());
		}

		private ApiInfo apiInfo() {
			return new ApiInfo("MPC Demo REST API", "REST endpoints for MPC Demo.",
					"1.0.0", "Terms of service",
					new Contact("Andrew Bauman", "https://www.redhat.com", "abauman@redhat.com"), "License of API",
					"https://opensource.org/licenses/MIT", Collections.emptyList());
		}
	}
	
	
}
