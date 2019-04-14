# Bank rest service
Project featuring REST service <strong> without making use of Spring </strong>.
The premise that rest application can be developed in the same manner as Spring.
The app is kept as simple as possible, with only one method /account/transfer.

Stack
* [TomEE](http://tomee.apache.org/) Tomcat Enterprise Server
* [OpenJPA](http://openjpa.apache.org/)
* [Apache DB](https://db.apache.org) aka Derby, in memory database
* [REST Assured](http://rest-assured.io) for tests

To run
```
mvn package 
java -jar <app-name> -port=<port>
curl http://localhost:<port>/api/account/transfer?from=1&to=2&amount=10
```

