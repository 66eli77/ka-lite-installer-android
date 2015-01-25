all: build run

build:
	docker build --no-cache --rm -t androidinstaller .

run:
	docker run -v $(shell pwd)/..:/root/ -t -i androidinstaller
