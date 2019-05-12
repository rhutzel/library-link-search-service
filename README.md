Ingest and cache library job postings.

# Building

`./gradlew clean build`

# Running

Bring up a containerized MongoDB instance:

`docker-compose up`

Run the Spring Boot server:

`./gradlew bootRun`

# Development

MongoDB shell: 
```
mongo -u root -p
use library-link;
db.requisition.stats().count;
```
