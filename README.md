Build status for master branch [![Build Status](https://travis-ci.org/scottkwalker/spray-akka-slick-postgres.svg?branch=master)](https://travis-ci.org/scottkwalker/spray-akka-slick-postgres)

Spray-Akka-Slick-PostgreSQL CRUD app
====================================

This app can be used to manage `Task`s in a todo list

*   It is a basic REST micro-service.
*   It abstracts CRUD operations on a database (in this case PostgreSQL)
*   It can be run standalone using a web browser and CURL
*   or it can receive web service calls from a web frontend. This is a good idea as it separates the web frontend from being able to directly access the database.  

Application stack
-----------------
*   [Akka](http://akka.io/) actors for lightweight, event-driven concurrent entities 
*   [Spray HTTPX](https://github.com/spray/spray/tree/master/spray-httpx/src) higher-level logic for working with HTTP messages, which is not specific to the server-side (spray-routing)
*   [Spray Routing](https://github.com/spray/spray/tree/master/spray-routing/src/main) DSL for defining RESTful web services
*   [Play JSON](https://github.com/playframework/playframework/tree/master/framework/src/play-json) de/serialization
*   [Slick](http://slick.typesafe.com/) Functional Relational Mapping for Scala
*   [PostgreSQL](http://www.postgresql.org/) database
*   [ScalaTest](http://scalatest.org/) testing framework
*   [Travis CI](http://travis-ci.org) build server. The file `.travis.yml` contains the settings for the build server. Currently PostgreSQL 9.1, 9.2 and 9.3 are supported.

Development prerequisites
-------------------------
1.  JDK 1.7.51 or 1.8 must be installed

2.  Install SBT.  The [current documentation][install-sbt] suggests:

    Mac: 
    
    * Install [Homebrew](http://brew.sh) 
    * Get the latest updates `brew update`
    * Install using brew  `brew install sbt`
    
3. Install PostgreSQL ([source](http://www.moncefbelyamani.com/how-to-install-postgresql-on-a-mac-with-homebrew-and-lunchy/)):

    Mac: 
    
    * Install using brew `brew install PostgreSQL`
    * Create/Upgrade a database `initdb /usr/local/var/postgres -E utf8`
    
4. Optional - Install Lunchy. Lunchy is a tool that allows you to easily start and stop the Postgres service, but you may prefer using a PostgreSQL command.

5. Start the database service:
    
    Lunchy:
    
    * `lunchy start postgres`
    
6. Create a PostgreSQL database for the user (see [stackoverflow answer](https://stackoverflow.com/questions/17633422/psql-fatal-database-user-does-not-exist))

Configuration
-------------
The file `application.conf` specifies the values for:

1. Micro-service:
    * port (currently `3000`)
    * url (currently `localhost`) 
2. Database settings:
    * port (currently `5432`)
    * database name
    * username 
    * default password 

Usage
-----
The following examples assume the `application.conf` file is configures the app to run on localhost on port 3000:

1. GET

    As a REST web service, you can perform any of the GET actions through a web browser or through CURL:

    * Count number of entries in Tasks table:

    `http://localhost:3000/api/v1/tasks/count`

    * List all tasks in the Tasks table:

    `http://localhost:3000/api/v1/tasks/`

2. POST

    Sending data to the micro-service can be performed by using CURL to a POST route:

    * Create the task "Buy milk" with assignee "Me":

    curl -i -H "Content-Type:application/json" -d '{"content":"Buy milk", "assignee":"Me"}' http://localhost:3000/api/v1/tasks/