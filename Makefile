ROOT_DIR:=$(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))

build-scala:
	docker build scala --tag swcomp/scala

build-postgres:
	docker build postgres --tag swcomp/postgres

build-angular:
	docker build angular --tag swcomp/angular_build

build-angular_nginx:
	docker build angular_dev \
	--tag swcomp/angular_nginx

build-all:
	make build-scala
	make build-postgres
	make build-angular

update-angular_build:
	docker run --name angular_build -v $(ROOT_DIR)/angular:/app swcomp/angular_build npm run build
	docker cp angular_build:/app/dist/hello-world-angular ./angular_dev
	docker rm angular_build

remove-all-images:
	docker rmi -f $(docker images -a -q)
	docker volume rm $(docker images ls -q)

integration-test:
	docker build tests/integration/scala/ --tag scala_test
	docker-compose build
	docker-compose up -d
	docker-compose --file docker-compose.yml --file docker-compose.test.yml run scala_test

build-test-image:
	docker rmi scala_test
	docker build tests/integration/scala/ --tag scala_test
