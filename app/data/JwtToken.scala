package uk.co.tkeetch.sso.data

import scala.language.implicitConversions

object JwtToken {
  implicit def fromMap(map:Map[String,Any]):JwtToken = new JwtToken(map)
  implicit def toMap(token:JwtToken):Map[String,Any] = token.claims

  // Public claim names defined by RFC 7519
  val CLAIM_SUBJECT_KEY = "sub"
  val CLAIM_ISSUED_AT_KEY = "iat"
  val CLAIM_EXPIRATION_TIME_KEY = "exp"
  val CLAIM_JWT_ID_KEY = "jti"

  // Private claim names
  val CLAIM_TOKEN_TYPE_KEY = "tt"
  val CLAIM_CSRF_TOKEN_KEY = "csrf"

  // Token Types
  val TOKEN_TYPE_AUTHENTICATION = "AUTH"
  val TOKEN_TYPE_REFRESH = "REFRESH"
}

class JwtToken(claimsMap:Map[String,Any]) {

  val claims = claimsMap

}

