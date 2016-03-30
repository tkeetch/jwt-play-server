## What?

This is a token-based authentication service using JWT (RFC 7519), using the jose4j JWT library. JWT is a much simpler, web-friendly and secure version of SAML (Security Assertion Markup Language). Check out www.jwt.io for more information

The project implements a small, but useful, non-interoperable subset of OAuth2. The interoperability provided by OAuth2 makes it an overcomplicated protocol with a number of security pitfalls that need to be avoided. See www.oathsecurity.com for some examples.

This implementation implements bearer tokens, refresh tokens, 2048 bit RSA-256 signatures and simple password authentication.

## Why?

I wrote this code to familiarise myself with Scala, the Play Framework, jQuery, HTTP Cross Origin Resource Sharing (CORS), GitHub and Amazon Web Services (AWS).

## How?

An example application can be launched as a virtual machine running in the AWS Free Tier. Everything can be automated with the provided Cloudformation template: https://raw.githubusercontent.com/tkeetch/jwt-play-server/master/conf/cloudformation/jwt-play-server.template

Note: When launched using in the free-tier by default it will take around 10-15 mins for the service to fully launch.

One the app has launched, visit the test page: https://\<public\_aws\_ip\>/. Until the app has launched, nginx will display an error. Plus, you will get a certificate error because of the use of a self-signed certificate.

User accounts can be configured by editing the /conf/users.conf file which is in the standard HOCON format used by the Play Framework. tom/tom is one valid set of credentials.

