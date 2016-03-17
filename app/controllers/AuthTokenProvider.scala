package uk.co.tkeetch.sso

import org.jose4j._
import org.jose4j.jwk._
import org.jose4j.jwt._
import org.jose4j.jws._
import org.jose4j.jwt.consumer._
import scala.util.{Try,Success,Failure}

object AuthTokenProvider {

  val privateSigningKey = RsaJwkGenerator.generateJwk(2048)
  val jwtConsumer = new JwtConsumerBuilder().setRequireExpirationTime().setVerificationKey(privateSigningKey.getKey()).build()


  private def internalGetUserToken (username:String):JwtClaims = {
    val claims = new JwtClaims()
    claims.setSubject(username)
    claims.setExpirationTimeMinutesInTheFuture(1)
    claims.setIssuedAtToNow()
    claims
  }

  private def signToken(claims:JwtClaims):String = {
    val jws = new JsonWebSignature();
    jws.setPayload(claims.toJson());
    jws.setKey(privateSigningKey.getPrivateKey());
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
    jws.getCompactSerialization();
  }

  def getAuthToken(username:String, csrfToken:String):String = {
    val claims = internalGetUserToken(username)
    claims.setExpirationTimeMinutesInTheFuture(20)
    claims.setGeneratedJwtId()
    claims.setClaim("TokenType","Auth")
    claims.setClaim("AuthLevel","Authenticated")
    claims.setClaim("CsrfToken",csrfToken)
    signToken(claims)
  }

  def getRefreshToken(username:String):String = {
    val claims = internalGetUserToken(username)
    claims.setExpirationTimeMinutesInTheFuture(10000)
    claims.setGeneratedJwtId()
    claims.setClaim("TokenType","Refresh")
    signToken(claims) 
  }

  def parseToken(token:String) = {
    jwtConsumer.processToClaims(token)
  }

  def tokenHasExpectedUserAndType(token:String, username:String, tokenType:String):Boolean = {
    Try(parseToken(token)) match {
      case Failure(_) => false
      case Success(claims) => (claims.getSubject == username && claims.getClaimValue("TokenType") == tokenType)
    }
  }

  def isValidAuthTokenForUser(token:String, username:String) = tokenHasExpectedUserAndType(token, username, "Auth")

  def isValidRefreshTokenForUser(token:String, username:String) = tokenHasExpectedUserAndType(token, username, "Refresh")
}


