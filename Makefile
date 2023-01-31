build-jar:
	./mvnw package -DskipTests

build-image:
	docker build -t inventory-management .

run-image:
	docker run -e "SPRING_DATA_MONGO_URI=mongodb://localhost/test" \
	-e "AWS_REGION=us-east-2" \
	-e "AWS_S3_ENDPOINT=http://inventory-management.s3.localhost.localstack.cloud:9000" \
	-e "AWS_S3_BUCKET=inventory-management" \
	-e "SPRINGDOC_SWAGGER_UI_PATH=/docs" \
	-e "AWS_ACCESS_KEY_ID=test" \
 	-e "export AWS_SECRET_ACCESS_KEY=test" \
 	--network=host inventory-management