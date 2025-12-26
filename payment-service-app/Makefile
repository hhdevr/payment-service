.PHONY: package build run clean

IMAGE_NAME ?= pay
TAG ?= 1.0
JAR_FILE ?= target/app.jar

package:
	mvn clean package

build: clean package
	docker build -t $(IMAGE_NAME):$(TAG) \
		--build-arg JAR_FILE=$(JAR_FILE) .

run:
	docker run -p 8080:8080 $(IMAGE_NAME):$(TAG)

clean:
	docker compose down -v
	docker rmi $(IMAGE_NAME):$(TAG) || true
