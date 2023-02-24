up_data: prepare_data
	@docker compose -f "test-data.yml" -p "test-data" up -d;
down_data:
	@docker compose -p "test-data" down
	@rm -rf v-mysql

prepare_data:
	@if [ ! -d "v-mysql" ]; then \
	    mkdir "v-mysql"; \
	fi
	@if [ ! -d "v-redis" ]; then \
		mkdir "v-redis"; \
	fi
	@if [ -z "$(shell docker network ls -f name=test | grep -w test)" ];then docker network create test;fi
