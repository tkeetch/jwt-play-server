package uk.co.tkeetch.sso.data

import scala.language.implicitConversions

object JwtToken {
  implicit def fromMap(map:Map[String,Any]):JwtToken = new JwtToken(map)
  implicit def toMap(token:JwtToken):Map[String,Any] = token.claims
}

class JwtToken(claimsMap:Map[String,Any]) {

  val claims = claimsMap

}

