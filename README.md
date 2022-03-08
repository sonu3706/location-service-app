# STEPS TO BUILD THE APP
Make sure jdk 1.8 is installed along with Apache Maven 3.6.3 is installed and make sure local port 8081 is not occupied.
1. Open terminal and move to project root directory.
2. Run given command `mvn clean package` this will build and application and create uber jar in target folder.
3. Run given command to just execute test cases `mvn test`
4. Run given command to run the application `mvn cpring-boot:run` it will start the application in local port `8081`.
5. If you wish to execute uber jar then run given command `java -jar target/vodafone-demo-0.0.1-SNAPSHOT.jar`, before running this command make sure `target` folder exists in project structure.