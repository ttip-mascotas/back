.DEFAULT_GOAL := dev

.PHONY: dev
dev:
	docker compose --profile dev up -d

.PHONY: dev-down
dev-down:
	docker compose --profile dev down

.PHONY: up
up:
	docker compose --profile all up -d --build

.PHONY: down
down:
	docker compose down
