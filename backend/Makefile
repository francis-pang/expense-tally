.PHONY: build-api build-sync test clean

build-api:
	GOOS=linux GOARCH=arm64 go build -o bootstrap ./cmd/api

build-sync:
	GOOS=linux GOARCH=arm64 go build -o bootstrap ./cmd/sync

test:
	go test ./...

clean:
	rm -f bootstrap

# SAM build Makefile - SAM sets ARTIFACTS_DIR
build-ApiFunction:
	GOOS=linux GOARCH=arm64 go build -tags lambda.norpc -o $(ARTIFACTS_DIR)/bootstrap ./cmd/api

build-SyncFunction:
	GOOS=linux GOARCH=arm64 go build -tags lambda.norpc -o $(ARTIFACTS_DIR)/bootstrap ./cmd/sync
