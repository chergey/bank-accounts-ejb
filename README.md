# Bank rest service
Project featuring REST service using Java EE.
The premise is that rest application can be developed in the same manner as Spring.
The app is kept as simple as possible.

[![Build Status](https://travis-ci.com/chergey/bank-accounts-ejb.svg?branch=master)](https://travis-ci.com/chergey/bank-accounts-ejb)

Stack
* [TomEE](http://tomee.apache.org/) Tomcat Enterprise Server
* [OpenJPA](http://openjpa.apache.org/)
* [Apache DB](https://db.apache.org) aka Derby, in memory database
* [REST Assured](http://rest-assured.io) for tests

To run
```
mvn package 
java -jar <app-name> -port=<port>
```



Sample requests
```
curl -X POST http://localhost:<port>/api/accounts/transfer?from=1&to=2&amount=10
curl http://localhost:<port>/api/accounts
curl http://localhost:<port>/api/accounts/somename?page=&size=20
curl -X DELETE http://localhost:<port>/api/accounts/2
```
