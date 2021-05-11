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

backup-wso2:
	sudo chmod -R 777 wso2/.
	docker container cp sw_comp_project_identity-server_1:/home/wso2carbon/wso2is-5.11.0/repository/. wso2/repository
	docker container cp sw_comp_project_identity-server_1:/home/wso2carbon/wso2is-5.11.0/backup/. wso2/backup
	sudo chmod -R 777 wso2/.

start:
	sudo chmod -R 777 wso2/.
	sudo docker-compose up --build

	
