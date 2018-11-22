# dscrapper
a dropwizard web scrapper, just an excuse playground for concurrency and asynchrony

## running
```bash
java -jar target/dscrapper-1.0-SNAPSHOT.jar server config.yml
```
Send request
```bash
curl -v 'http://localhost:8080/scrape/sequential/http%3A%2F%2Fwww.eldiario.es%2F'
```

## Load testing
Install vegeta: 

Run an attack:
```bash
vegeta attack -targets sequential-reqs.txt -duration 20s -rate 3 -timeout 60s | tee results.bin | vegeta report
```

## generating empty dropwizard project
```bash
mvn archetype:generate -DarchetypeGroupId=io.dropwizard.archetypes -DarchetypeArtifactId=java-simple -DarchetypeVersion=1.3.7
```
