.PHONY: clean pull build deploy production reload

clean:
	rm -rf build

pull:
	git pull

build:
	./gradlew --no-daemon publishImageToLocalRegistry

deploy: pull build
	docker-compose down
	docker-compose up chess-server

reload:
	docker-compose down
	docker-compose up -d

production: clean pull deploy
.DEFAULT_GOAL := production
