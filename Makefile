.PHONY: clean shutdown pull build deploy reload

clean:
	rm -rf build

pull:
	git pull

build:
	./gradlew --no-daemon publishImageToLocalRegistry

shutdown:
	docker-compose down

deploy: shutdown pull build
	docker-compose up -d

reload: shutdown
	docker-compose up -d

.DEFAULT_GOAL := production
