# Task Tracker backend


Backend for the Task tracker application. 
It is a microservice application. 
The next step in development is the creation of a frontend (planned stack - ReactJS).    
All services are launched in Docker using docker compose.
### Build project
```
mvn clean install
``` 

### Run project
```
docker-compose up -d
``` 

Stack:

- Kotlin
- Spring Boot 3
- Liquibase
- JSON Web Token
- Springdoc OpenAPI
- TestContainers, JUnit 5, Mockito
