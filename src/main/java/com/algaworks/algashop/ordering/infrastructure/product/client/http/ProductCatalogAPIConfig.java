package com.algaworks.algashop.ordering.infrastructure.product.client.http;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = "algashop.integrations.product-catalog.provider", havingValue = "HTTP", matchIfMissing = true)
public class ProductCatalogAPIConfig {

    @Bean
    public ProductCatalogAPIClient productCatalogAPIClient(
            RestClient.Builder builder,
            ProductCatalogIntegrationProperties properties,
            OAuth2AuthorizedClientManager manager
        ) {

        var interceptor = new OAuth2ClientHttpRequestInterceptor(manager);
        interceptor.setClientRegistrationIdResolver(request -> properties.getOauth2ClientRegistrationId());

        RestClient restClient = builder.baseUrl(properties.getUrl())
                .requestFactory(generateClientHttpRequestFactory())
                .requestInterceptor(interceptor)
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
        return proxyFactory.createClient(ProductCatalogAPIClient.class);
    }

    private ClientHttpRequestFactory generateClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(5));
        factory.setConnectTimeout(Duration.ofSeconds(2));
        return factory;
    }

}