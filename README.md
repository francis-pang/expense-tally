# expense-tally #
[![Build Status](https://travis-ci.com/francis-pang/expense-tally.svg?branch=master)](https://travis-ci.com/francis-pang/expense-tally)
[![Comments (%)](https://sonarcloud.io/api/project_badges/measure?project=boyshawn_expense-tally&metric=security_rating)](https://sonarcloud.io/dashboard?id=boyshawn_expense-tally)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=boyshawn_expense-tally&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=boyshawn_expense-tally)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=boyshawn_expense-tally&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=boyshawn_expense-tally)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=boyshawn_expense-tally&metric=coverage)](https://sonarcloud.io/dashboard?id=boyshawn_expense-tally)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=boyshawn_expense-tally&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=boyshawn_expense-tally)

# Overview #
This application will aim to reconcilate the past transaction history against my expense tracker application

The command line version of the application needs to be provided 2 information before it is able to execute. They are:

| Option             | Description |
|--------------------|-------------|
|  database-filepath | The absolute or relative path to a database file which stores the transaction of the Expense Manager |
| csv-filepath       | The absolute or relative path to a comma-separated value file which stores the bank transactions |

The application accepts the parameters in 3 different formats:
1. option=XXXX
2. option = XXXX
3. option XXXX

Any other format of input is unacceptable, and will result in system error. 

# Architecture Diagram #
![Expense Tally Architecture Diagram](docs/architecture-diagram.svg)

There are a few processes that expense tally serves to fulfill the need to reconcile the data inside the Expense Manager
application against the bank provided data. Below is a brief overview of the typical user process:

1. User downloads the comma-separated values file from the bank website. He will upload the file to the S3 bucket. This 
   will trigger the parsing of the file, and perform a expense reconciliation.
2. User update the Android app Expense Manager, then the corresponding database file is updated on Dropbox. This trigger 
   an update to the server database.
3. User enters a URL to view all the discrepant transaction entries. He can resolve those discrepant entries, and the 
   database file in Dropbox will be updated.

# Database #
Due to the requirement, there is a Docker file inside the _expense-tally-expense-manager_ module. This Docker file 
define all the necessary ingredient to set up a new database server used by the _expense-tally-expense-manager_ module 
to persist SQLite exported data file. Developer/ operator can use the following command to run the created Docker image:
```Shell
# Run this at the root directory
# This set the following option:
# -d detached
# --name name of the container
# -p  Use the -p flag to explicitly map a single port or range of ports. The port number inside the container (where 
# the service listens) does not need to match the port number exposed on the outside of the container (where clients 
# connect).
docker container run -d=true --name=em-db -p 3306:3306/tcp expense-tally/em-db:latest
mysql --host=172.27.53.120 --port=3306 --user=expensetally --database=expense_manager --password
```

# Coding Standard #
* The javadocs in this project are inspired by the guidelines in 
  [Liferay Portal Advanced Javadoc Guidelines](https://github.com/liferay/liferay-portal/blob/master/readme/ADVANCED_JAVADOC_GUIDELINES.markdown).
* The package naming convention adopts this 
  [Stack Exchange answer](https://softwareengineering.stackexchange.com/a/75929/88556):
> Use **plural for packages with homogeneous contents** and **singular for packages with heterogeneous contents**.
>
> For example, a package named `com.myproject.task` does not mean that each contained class is an instance of a `task`. 
> There might be a `TaskHandler`, a `TaskFactory` and etc. However, a package named `com.myproject.tasks` would contain
> different types that are all tasks: `TakeOutGarbageTask`, `DoTheDishesTask` and etc.
* For code comment, double forward slashes ("//") is preferred over multiple lined comment block. This is because it is 
  easier to commented in block and there will not be issue due to nested comments.
* For unit test, the convention is to write the method name under test, followed by the testing purpose separated with 
  an underscore character ("_"). For example, *isPaymentCardValid_incorrectLengthFail*.
* There will not be be any JavaDocs documentation for getter and setter methods.
* All assertJ method calls are statically imported.

## Short form ##

| Short form | Long form |
|------------|-----------|
| csv        | comma separated values |
| err        | error |
| msg        | message | 
| app        | application |
