# axual

Input files: records.csv & records.xml are placed under axual/src/main/recources/

Detected errors are persisted in in-memory database (hsqldb) in this case. Errors are logged as well in the end.

To build and run the application with Maven: mvn package && java -jar target/axual-0.0.1-SNAPSHOT.jar
