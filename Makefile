build-jar:
	./mvnw package && java -jar target/inventory-management.jar

build-image:
	docker build -t inventory-management .