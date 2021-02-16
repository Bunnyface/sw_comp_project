build-helloworld:
	docker build \
	--file docker/helloworld.dockerfile \
	--tag helloworld_test \
	.

build-scala:
	docker build \
	--file docker/scala.Dockerfile \
	--tag scala \
	.

make run-helloworld:
	docker run \
	-d \
	-v $(shell pwd)/hellotext:/usr/src/app/hellotext/ \
	helloworld_test
