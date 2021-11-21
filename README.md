# hotels-data-merge
How to build and run application

With Docker:
1. Install JDK 17: https://knasmueller.net/how-to-install-java-openjdk-17-on-macos-big-sur
2. I think you already have docker installed.
3. Compile, run tests, build jar:  `./mvnw clean install`
4. Build docker file: `docker build -t exercise/hotel-data-merge .`
5. Run app: `docker run -p 8080:8080 exercise/hotel-data-merge`

With Java 17 installed only
1. Install JDK 17: https://knasmueller.net/how-to-install-java-openjdk-17-on-macos-big-sur
2. Start app: `./mvnw spring-boot:run`


Curl:
1. Filter by destinationId: `curl -X GET 'http://localhost:8080/api/hotels?destinationId=5432'`
2. Filter by hotelId: `curl -X GET 'http://localhost:8080/api/hotels?hotelId=SjyX'`
3. Get all: `curl -X GET 'http://localhost:8080/api/hotels'`

Basic rules to merge hotels:
- Numberic/String fields are selected based on the including frequency from 3 sources. If the frequency of all values are the same, last one will be used.
- String Collection field will be united from all sources and sorted ascendingly.
- Image collection will be united from all sources and sorted by description ascendingly.

