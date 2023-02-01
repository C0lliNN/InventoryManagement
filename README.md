# Inventory Management
A Non-Blocking REST API developed in Java 11, Spring Boot 2 and Spring WebFlux to manage the Inventory of an Industry.

![](https://img.shields.io/badge/coverage-95%25-brightgreen)

##### [API Documentation](https://c0llinn-inventory-management.herokuapp.com/docs)
##### [Live Endpoint](https://c0llinn-inventory-management.herokuapp.com/api/v1/products)

## Tools & Libraries

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
* [Onion Architecture](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/)
* [MongoDB](https://www.mongodb.com/en-us)
* [Testcontainers](https://www.testcontainers.org/) 
* [Jacoco](https://github.com/jacoco/jacoco)
* [Archunit](https://www.archunit.org/)
* [Springdoc](https://springdoc.org/)
* [Localstack](https://github.com/localstack/localstack)
* [Amazon S3](https://aws.amazon.com/s3/?nc1=h_ls)
* [Terraform](https://www.terraform.io/)

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

## How to Deploy

### Requirements
* Terraform CLI
* AWS Account
* Mongo Atlas Account
* GitHub Account


### Instructions
1. Create or fork a GitHub repository with this code
2. Clone the repository locally
3. inside the `terraform` folder:
   1. Add a new file called `prod.tfvars`
   2. Add values to all required terraform variables
   3. Run `terraform plan -var-file=prod.tfvars`
   4. Run `terraform apply -var-file=prod.tfvars -auto-approve`
4. Trigger the `Continous Deployment` workflow in GitHub
5. Access the ALB endpoint
6. Be Happy!   