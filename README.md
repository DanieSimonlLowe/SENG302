# SENG302 Team 800

## About
TAB is a Team Analytics Builder application that runs on a modern browser using modern frameworks.
TAB is built using ```gradle```, ```Spring Boot```, ```Thymeleaf```, and ```GitLab CI```.

TAB is used for athletes and sports people for all kinds of athletic purposes including tracking, analysis, and more.

TAB is built by the SENG302 Team 800 for the full year project during 2023.

## Authors

- Celia Allen
- Tom Barthelmeh
- Kahu Griffin
- Nathan Harper
- Daniel Lowe
- Sean Marriott
- Celeste Turnock

## How to run
### 1 - Dependencies
This project has the following dependencies:
- [Spring Boot](https://spring.io/projects/spring-boot) - Used to provide http server functionality
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Used to implement JPA (Java Persistence API) repositories
- [h2](https://www.h2database.com/html/main.html) - Used as an SQL JDBC and embedded database
- [Thymeleaf](https://www.thymeleaf.org/) - A templating engine to render HTML on the server, as opposed to a separate client-side application (such as React)
- [Gradle](https://gradle.org/) - A build tool that greatly simplifies getting application up and running, even managing our dependencies
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/3.0.2/gradle-plugin/reference/html/) - Allows us to more easily integrate our Spring Boot application with Gradle
- [Spring Boot Security](https://docs.spring.io/spring-security/reference/index.html) - Used for password encryption and authorization on the application

### 2 - Running the project

First ensure that you have the environment variables
DB_USERNAME, DB_PASSWORD, and MAPQUEST_API_KEY set to valid inputs.

From the root directory:

On Linux:
```
./gradlew bootRun
```

On Windows:
```
gradlew bootRun
```

By default, the application will run on local port 8080 [http://localhost:8080](http://localhost:8080)

### 3 - Using the application

To run the application navigate to the [homepage](http://locahost:8080/).
To use the application to its potential, the user must make an account using the ***Sign-Up*** button on the home page.
Once signed up, the application will redirect the user to their profile, and from there, they are able to use the application as expected.
Here are a few default user logins:

Email: tom.barthelmeh@hotmail.com
Password: Password1!

Email: notReal@uclive.ac.nz
Password: Password1!

The former has the most test data associated with it.

## How to run tests

To run the tests make sure you are in the root directory. 
Then, navigate to the testing folder located at
```agsl
/src/test/java/nz.ac.canterbury.seng302.tab

```
**From your IDE:**  

To open a test file, open it in your favourite Java IDE (*for example [IntelliJ](https://www.jetbrains.com/idea/)*).

To run a test file (from IntelliJ): *right click -> Run "filename"*.


**From the command line:**

To run the tests from the command line, use the command
```agsl
gradlew test
```


Integration and End to End tests can be run with the commands
```agsl
gradlew integration
```
```agsl
gradlew end2end
```
respectively. 

## Contributors

- SENG302 teaching team

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=17797&section=8)
