# Inventory Management

A Non-Blocking REST API developed in Java 11, Spring Boot 2 and Spring WebFlux to manage the Inventory of an Industry.

##### [API Documentation]()
##### [Live Endpoint]()

## Tools & Libraries

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
* [MongoDB](https://www.mongodb.com/en-us)
* [Testcontainers](https://www.testcontainers.org/) 
* [Jacoco](https://github.com/jacoco/jacoco)
* [Archunit](https://www.archunit.org/)
* [Springdoc](https://springdoc.org/)
* [Localstack](https://github.com/localstack/localstack)
* [Amazon S3](https://aws.amazon.com/s3/?nc1=h_ls)

## How to run

1. Execute docker containers

```bash
docker-compose up -d
```

2. In order to execute AWS S3 locally, it is necessary to export the following environment variables:
```bash
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
```

