# dscrapper
a dropwizard web scrapper, just an excuse playground for concurrency and asynchrony

## running
```bash
java -jar target/dscrapper-1.0-SNAPSHOT.jar server config.yml
```
Send request
```bash
curl http://localhost:8080/hello-world
```

## generating empty dropwizard project
```bash
mvn archetype:generate -DarchetypeGroupId=io.dropwizard.archetypes -DarchetypeArtifactId=java-simple -DarchetypeVersion=1.3.7
```
