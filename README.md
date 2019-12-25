# expense-tally
[![Build Status (master)](https://travis-ci.com/boyshawn/expense-tally.svg?branch=master)](https://travis-ci.com/boyshawn/expense-tally.svg?branch=master)
[![Comments (%)](https://sonarcloud.io/api/project_badges/measure?project=boyshawn_expense-tally&metric=security_rating)](https://sonarcloud.io/dashboard?id=boyshawn_expense-tally)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=boyshawn_expense-tally&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=boyshawn_expense-tally)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=boyshawn_expense-tally&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=boyshawn_expense-tally)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=boyshawn_expense-tally&metric=coverage)](https://sonarcloud.io/dashboard?id=boyshawn_expense-tally)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=boyshawn_expense-tally&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=boyshawn_expense-tally)

This application will aim to reconcilate the past transaction history against my expense tracker application

**Note**
* The javadocs in this project are inspired by the guidelines in [Liferay Portal Advanced Javadoc Guidelines](https://github.com/liferay/liferay-portal/blob/master/readme/ADVANCED_JAVADOC_GUIDELINES.markdown).
* The package naming convention adopts the [Stack Exchange answer](https://softwareengineering.stackexchange.com/a/75929/88556):
> Use **plural for packages with homogeneous contents** and **singular for packages with heterogeneous contents**.
>
> For example, a package named `com.myproject.task` does not mean that each contained class is an instance of a `task`. There might be a `TaskHandler`, a `TaskFactory` and etc. However, a package named `com.myproject.tasks` would contain different types that are all tasks: `TakeOutGarbageTask`, `DoTheDishesTask` and etc.
* For code comment, double forward slashes ("//") is preferred over multiple lined comment block. This is because it is easier to commented in block and there will not be issue due to nested comments.
* For unit test, the convention is to place the method being tested, followed by the testing purpose separated with an underscore character ("_"). For example, *isPaymentCardValid_incorrectLengthFail*.