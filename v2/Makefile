.PHONY: build deploy deploy-frontend local clean

build:
	sam build

deploy: build
	sam deploy

deploy-frontend: build
	cd frontend && npm run build
	aws s3 sync frontend/dist s3://$$(aws cloudformation describe-stacks --stack-name expense-tally-v2 --query 'Stacks[0].Outputs[?OutputKey==`FrontendBucketName`].OutputValue' --output text) --delete
	aws cloudfront create-invalidation --distribution-id $$(aws cloudformation describe-stacks --stack-name expense-tally-v2 --query 'Stacks[0].Outputs[?OutputKey==`CloudFrontDistributionId`].OutputValue' --output text) --paths "/*"

local:
	sam local start-api

clean:
	rm -rf .aws-sam
	rm -f backend/bootstrap
