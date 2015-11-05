## What?

This project implements a Token-based authentication service using JWT (RFC 7519). It uses the jose4j JWT library.

## Why?

I wrote this code to familiarise myself with Scala, Play, jQuery, GitHub and Amazon Web Services.

## How?

Launch the app using the supplied AWS CloudFormation template (./conf/cloudformation/jwt-play-server.template). The template runs the app in the free-tier by default and will compile the app for launch, which takes about 10-15 mins.

One the app has launched, visit the test page: https://<public_aws_ip>/. Until the app has launched, nginx will display an error. Plus, you will get a certificate error because of the use of a self-signed certificate.

Valid test credentials are tom/tom.

## What's Next?

* Implement a real authentication back-end.
* Run the app under a dedicated user account (instead of root).
* Accept a valid SSL certificate to replace the self-signed certificate and enable HSTS.
* Accept a JWT token signing key or generate a new one and report the public key.

