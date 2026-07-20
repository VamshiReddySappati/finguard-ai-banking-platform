.PHONY: build test up down logs clean frontend

build:
	mvn -B clean verify
	cd frontend && npm ci && npm run build

test:
	mvn -B test

up:
	docker compose up --build -d

logs:
	docker compose logs -f --tail=200

down:
	docker compose down -v

clean:
	mvn clean
	rm -rf frontend/node_modules frontend/dist
