# Newsflow Tomcat CI/CD Demo (Lab-4)

News publishing workflow packaged as a WAR, built by Maven, tested by JUnit 5,
and deployed by a Jenkins Pipeline to Apache Tomcat.

## Ports
- Jenkins: http://localhost:8080
- Application: http://localhost:8081/newsflow-demo/  (Tomcat set to port 8081)

## Layout
```
newsflow-tomcat-demo
├── pom.xml
├── Jenkinsfile
└── src
    ├── main
    │   ├── java/com/vit/newsflow/   Article, ArticleValidator, NewsWorkflowService, SiteGenerator
    │   └── webapp/index.jsp         renders a sample published article
    └── test
        └── java/com/vit/newsflow/NewsWorkflowTest.java   12 JUnit 5 tests
```

## Manual build and deploy (do this before Jenkins)
```bash
cd newsflow-tomcat-demo
mvn clean
mvn compile
mvn test
mvn package
```
Confirm `target\newsflow-demo.war` exists, then:
```bash
copy target\newsflow-demo.war C:\apache-tomcat-11.0.24\webapps\newsflow-demo.war
```
Open http://localhost:8081/newsflow-demo/ and confirm the page renders the article
with "News Publishing Pipeline Successful!".

## Jenkins setup
1. Manage Jenkins -> Tools: add JDK named `JDK-21`, Maven named `Maven-3` (names must
   match the Jenkinsfile's `tools` block).
2. Manage Jenkins -> Plugins: ensure Pipeline, Git, JUnit are installed.
3. New Item -> Pipeline -> name it `Newsflow-Tomcat-Pipeline`.
4. Definition: Pipeline script -> paste `Jenkinsfile`'s contents, with your GitHub URL.
5. Build Now. Stages: Checkout -> Compile -> Test -> Package -> Deploy -> Verify.

## Demonstrate a failing build
In `NewsWorkflowTest.java`, change `assertEquals(Article.Status.APPROVED, a.status)`
to expect `REJECTED` instead. Commit, push, rebuild: Jenkins should report
`CI/CD PIPELINE FAILED`. Revert to fix it.

## Level 2: Gradle or Ant instead of Maven
Add a `build.gradle`/`settings.gradle` (or `build.xml`) that also produces a WAR
(package the compiled classes plus `src/main/webapp`), then copy the Jenkins job and
swap the Compile/Test/Package `bat` commands for `gradle clean build` or `ant build`,
updating the WAR path in Deploy/Verify to match.
