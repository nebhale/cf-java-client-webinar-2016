# Cloud Foundry Java Client Webinar
The `cf-java-client-webinar-2016` project is a Spring Boot application that demonstrates how to use the [Cloud Foundry Java Client][c] to list the applications in a space.  In addition to doing this, it also adds additional information about bound services to the output.


## Development
The project depends on Java 8.  To build from source and start the application, run the following:

```shell
$ ./mvnw clean package
$ ./mvnw spring-boot:run
```

**IMPORTANT**
In order to run the application, some Spring Boot properties need to be set.  These can be set using any of the Spring Boot's [relaxed binding][r] strategies.

Name | Description
---- | -----------
`cf.apiHost` | The Cloud Foundry host
`cf.username` | The Cloud Foundry username
`cf.password` | The Cloud Foundry password
`cf.organization` | The Cloud Foundry organization to work against
`cf.space` | The Cloud Foundry space to work against


## License
This project is released under version 2.0 of the [Apache License][l].

[c]: https://github.com/cloudfoundry/cf-java-client
[l]: https://www.apache.org/licenses/LICENSE-2.0
[r]: http://docs.spring.io/spring-boot/docs/1.4.0.RELEASE/reference/htmlsingle/#boot-features-external-config-relaxed-binding
