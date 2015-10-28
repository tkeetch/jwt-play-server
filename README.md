## What?

This project implements a Token-based authentication service using JWT (RFC 7519). It uses the jose4j JWT library.

## Why?

I wrote this code to familiarise myself with Scala, Play and GitHub.

## How?

Build & run using SBT:
> sbt run

By default the web app runs on port 9000. Visit http://localhost:9000/login to test the app. Default credentials are tom/tom.

The login is a POST /login.json with this JSON body (Content-Type: text/json):
> { "userid":"tom", "credential":"tom" }

