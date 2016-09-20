/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationSummary;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.util.PaginationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

@SpringBootApplication
public class ListApplicationsApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ListApplicationsApplication.class, args);
        CloudFoundryClient cloudFoundryClient = applicationContext.getBean(CloudFoundryClient.class);
        CloudFoundryOperations cloudFoundryOperations = applicationContext.getBean(CloudFoundryOperations.class);

        CountDownLatch latch = new CountDownLatch(1);

        cloudFoundryOperations.applications()
            .list()
            .flatMap(application -> Mono.just(application)
                .and(getServiceInstances(cloudFoundryClient, application)))
            .map(function(FormattingUtils::formatApplication))
            .subscribe(System.out::println, t -> {
                t.printStackTrace();
                latch.countDown();
            }, latch::countDown);

        latch.await();
    }

    @Bean
    ReactorCloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorCloudFoundryClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();
    }

    @Bean
    DefaultCloudFoundryOperations cloudFoundryOperations(CloudFoundryClient cloudFoundryClient,
                                                         @Value("${cf.organization}") String organization,
                                                         @Value("${cf.space}") String space) {
        return DefaultCloudFoundryOperations.builder()
            .cloudFoundryClient(cloudFoundryClient)
            .organization(organization)
            .space(space)
            .build();
    }

    @Bean
    DefaultConnectionContext connectionContext(@Value("${cf.apiHost}") String apiHost) {
        return DefaultConnectionContext.builder()
            .apiHost(apiHost)
            .build();
    }

    @Bean
    PasswordGrantTokenProvider tokenProvider(@Value("${cf.username}") String username,
                                             @Value("${cf.password}") String password) {
        return PasswordGrantTokenProvider.builder()
            .password(password)
            .username(username)
            .build();
    }

    private static Mono<List<String>> getServiceInstances(CloudFoundryClient cloudFoundryClient, ApplicationSummary application) {
        return requestServiceBindings(cloudFoundryClient, application)
            .map(ResourceUtils::getServiceInstanceId)
            .flatMap(serviceInstanceId -> requestServiceInstance(cloudFoundryClient, serviceInstanceId))
            .map(ResourceUtils::getServiceInstanceName)
            .collectList();
    }

    private static Flux<ServiceBindingResource> requestServiceBindings(CloudFoundryClient cloudFoundryClient, ApplicationSummary application) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceBindingsV2()
                .list(ListServiceBindingsRequest.builder()
                    .applicationId(application.getId())
                    .page(page)
                    .build()));
    }

    private static Mono<GetServiceInstanceResponse> requestServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return cloudFoundryClient.serviceInstances()
            .get(GetServiceInstanceRequest.builder()
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

}
