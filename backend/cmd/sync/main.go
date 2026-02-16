package main

import (
	"context"
	"log"
	"os"

	"expense-tally-v2/internal/provider"
	"expense-tally-v2/internal/repository"
	"expense-tally-v2/internal/service"
)

func main() {
	ctx := context.Background()
	ddb, err := repository.NewDynamoClient(ctx)
	if err != nil {
		log.Fatalf("failed to create DynamoDB client: %v", err)
	}

	transactionsTable := os.Getenv("TRANSACTIONS_TABLE")
	keywordTable := os.Getenv("KEYWORD_ASSOCIATIONS_TABLE")
	connectionsTable := os.Getenv("PROVIDER_CONNECTIONS_TABLE")
	syncLogsTable := os.Getenv("SYNC_LOGS_TABLE")

	txnRepo := repository.NewTransactionRepository(ddb, transactionsTable)
	kwRepo := repository.NewKeywordRepository(ddb, keywordTable)
	connRepo := repository.NewConnectionRepository(ddb, connectionsTable)
	syncRepo := repository.NewSyncLogRepository(ddb, syncLogsTable)

	keywordSvc := service.NewKeywordService(kwRepo)
	syncSvc := service.NewSyncService(connRepo, txnRepo, syncRepo, keywordSvc, provider.NewProviderAdapter)

	if err := syncSvc.SyncAll(ctx); err != nil {
		log.Fatalf("sync failed: %v", err)
	}
	log.Println("sync completed successfully")
}
