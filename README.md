# money-transfer-api overview

Simple RESTful API for money transfers between accounts

* H2 in-memory database
* Cucumber integration tests

The domain model:

* Simple Account entity consists of id and balance
* Simple Transfer entity consisting of id, originId, destinationId and amount
* Relationships between data entities are out of scope for this example


# Package and tests

To package and run unit and integration tests:

* Run the following from the root directory.

        mvn package
        
        
# Running The Application

To run the application run the following commands.

* To setup the h2 database and create 2 accounts '{"accountNumber": 1, "balance": 50}' and '{"accountNumber": 2, "balance": 10}' run

        java -jar target/money-transfer-api-1.0-SNAPSHOT.jar db migrate config.yml

* To start the server run:

        java -jar target/money-transfer-api-1.0-SNAPSHOT.jar server config.yaml

* To create transfer between between accounts 1 and 2 run:

	curl -i -H "Content-Type: application/json" -X POST -d '{"senderAccountId":1,"receiverAccountId":2,"amount": 1.00}' http://localhost:8080/moneytransfer
