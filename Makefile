app := ufutao/account
version := v1.0.0


up_service: prepare
	@docker compose -f "test-service.yml" -p "test-service" up -d;
down_service:
	@docker compose -p "test-service" down
	@docker rmi $(app):$(version)
clean: down_service
	@make -C doc/data down_data

prepare:
	@if [ -z "$(shell docker network ls -f name=test | grep -w test)" ];then docker network create test;fi
	@make -C doc/data up_data
	@if [ -z "$(shell docker image ls $(app):$(version) | grep -w $(app))" ]; then\
		if [ -z '$(shell javac --version)' ]; then echo "请安装java8+"; exit 1; fi ;\
		if [ -z '$(shell mvn --version)' ]; then echo "请安装maven3.8+"; exit 1; fi ;\
		mvn package; \
		docker build -f Dockerfile -t $(app):$(version) . ;\
		mvn clean; \
	else \
		echo "镜像已存在 将跳过构建! $(app):$(version)"; \
	fi