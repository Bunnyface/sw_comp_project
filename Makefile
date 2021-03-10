ROOT_DIR:=$(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))

build-scala:
	docker build scala --tag scala_main

build-postgres:
	docker build postgres --tag postgres

build-angular:
	docker build angular --tag angular_main

build-angular_build:
	docker build --file build.Dockerfile \
	--tag angular_build \
	angular

build-all:
	make build-scala
	make build-postgres
	make build-angular

update-angular_build:
	docker run --name angular_build -v $(ROOT_DIR)/angular/app:/app:Z angular_build /bin/bash -c "npm install && npm run build"
	docker cp angular_build:/app/dist/hello-world-angular ./angular/angular_dev
	docker rm angular_build

remove-all-images:
	docker rmi -f $(docker images -a -q)

