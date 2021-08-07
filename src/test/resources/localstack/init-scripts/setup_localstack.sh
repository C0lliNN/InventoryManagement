echo "Starting localstack initialization..."

awslocal s3api create-bucket --bucket inventory-management --region us-east-2

echo "Initialization has finished!"