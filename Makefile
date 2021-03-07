ROOT_DIR:=$(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))

build-scala:
	docker build \
	--file docker/scala.Dockerfile \
	--tag scala_main \
	.

build-postgres:
	docker build \
	--file docker/postgres.dockerfile \
	--tag postgres \
	.

build-angular:
	docker build \
	--file docker/angular.Dockerfile \
	--tag angular_main \
	.

build-angular_build:
	docker build \
	--file docker/angular_build.Dockerfile \
	--tag angular_build \
	.

build-all:
	make build-scala
	make build-postgres
	make build-angular

update-angular_build:
	docker run --name angular_build -v $(ROOT_DIR)/angular:/app angular_build npm run build
	docker cp angular_build:/app/dist/hello-world-angular ./angular_dev
	docker rm angular_build

remove-all-images:
	docker rmi -f $(docker images -a -q)

