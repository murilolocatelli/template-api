# template-api

> Template API project

## Config lombok for Eclipse

https://projectlombok.org/setup/eclipse

## Config lombok for IntelliJ

https://projectlombok.org/setup/intellij

## Building with tests

```sh
mvn clean package
```

## Coverage

After running the tests, see the reports in target/site/jacoco/index.html

## Run docker-compose

Development:

```sh
docker-compose -p template_api_dev -f docker/docker-compose-dev.yml up -d
```

Test:

```sh
docker-compose -p template_api_test -f docker/docker-compose-test.yml up -d
```

## Run local Sonarqube

Run docker Sonarqube:

```sh
docker run -d --name sonarqube -p 9000:9000 sonarqube:<sonarqube-version>
```

Generate token in sonarqube to use in below command

```sh
mvn clean package sonar:sonar -Dsonar.login=<sonarqube-token>
```

## Run application

```sh
java -jar target/template-api-X.X.X.jar
```

## Swagger

```sh
http://<host>:<port>/swagger-ui/index.html
```
