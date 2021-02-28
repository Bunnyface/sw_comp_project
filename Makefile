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

build-all:
	make build-scala
	make build-postgres
	make build-angular

