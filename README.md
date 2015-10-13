* What?

This project implements a Token-based authentication service using JWT (RFC 7519). It uses the jose4j JWT library.

* Why?

I wrote this code to familiarise myself with Scala, Play and GitHub.

* How?

Build & run using SBT:
> sbt run

Login with test username and password:
> http://localhost:9000/login/tom/tom

Get a fresh access token using the refresh token:
> http://localhost:9000/refresh/tom/<refresh-token>

