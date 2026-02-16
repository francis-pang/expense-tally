package main

import (
	"context"
	"log"
	"os"

	"expense-tally-v2/internal/handler"
	"expense-tally-v2/internal/provider"
	"expense-tally-v2/internal/repository"
	"expense-tally-v2/internal/service"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	chiadapter "github.com/awslabs/aws-lambda-go-api-proxy/chi"
)

var proxy *chiadapter.ChiLambda

func init() {
	ctx := context.Background()
	ddb, err := repository.NewDynamoClient(ctx)
	if err != nil {
		log.Fatalf("failed to create DynamoDB client: %v", err)
	}

	transactionsTable := os.Getenv("TRANSACTIONS_TABLE")
	categoriesTable := os.Getenv("CATEGORIES_TABLE")
	keywordTable := os.Getenv("KEYWORD_ASSOCIATIONS_TABLE")
	connectionsTable := os.Getenv("PROVIDER_CONNECTIONS_TABLE")
	syncLogsTable := os.Getenv("SYNC_LOGS_TABLE")

	txnRepo := repository.NewTransactionRepository(ddb, transactionsTable)
	catRepo := repository.NewCategoryRepository(ddb, categoriesTable)
	kwRepo := repository.NewKeywordRepository(ddb, keywordTable)
	connRepo := repository.NewConnectionRepository(ddb, connectionsTable)
	syncRepo := repository.NewSyncLogRepository(ddb, syncLogsTable)

	keywordSvc := service.NewKeywordService(kwRepo)
	catSvc := service.NewCategoryService(catRepo)
	txnSvc := service.NewTransactionService(txnRepo, catRepo, keywordSvc)
	syncSvc := service.NewSyncService(connRepo, txnRepo, syncRepo, keywordSvc, provider.NewProviderAdapter)

	healthH := handler.NewHealthHandler()
	categoriesH := handler.NewCategoriesHandler(catSvc)
	transactionsH := handler.NewTransactionsHandler(txnSvc)
	dashboardH := handler.NewDashboardHandler(txnSvc, catSvc)
	syncH := handler.NewSyncHandler(syncSvc)
	connectionsH := handler.NewConnectionsHandler(connRepo)

	r := handler.NewRouter(healthH, categoriesH, transactionsH, dashboardH, syncH, connectionsH)
	proxy = chiadapter.New(r)
}

func main() {
	lambda.Start(lambdaHandler)
}

func lambdaHandler(ctx context.Context, req events.APIGatewayProxyRequest) (events.APIGatewayProxyResponse, error) {
	return proxy.ProxyWithContext(ctx, req)
}
