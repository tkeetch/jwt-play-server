package uk.co.tkeetch.sso

import java.lang.String
import org.jose4j._
import org.jose4j.jwk._
import org.jose4j.jwt._
import org.jose4j.jws._
import org.jose4j.jwt.consumer._

object AuthToken {

  val privateSigningKey = RsaJwkGenerator.generateJwk(2048)
  val jwtConsumer = new JwtConsumerBuilder().setRequireExpirationTime().setVerificationKey(privateSigningKey.getKey()).build()


  private[AuthToken] def internalGetUserToken (username:String):JwtClaims = {
    val claims = new JwtClaims()
    claims.setSubject(username)
    claims.setExpirationTimeMinutesInTheFuture(1)
    claims.setIssuedAtToNow()
    claims
  }

  private[AuthToken] def signToken(claims:JwtClaims):String = {
    val jws = new JsonWebSignature();
    jws.setPayload(claims.toJson());
    jws.setKey(privateSigningKey.getPrivateKey());
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
    jws.getCompactSerialization();
  }

  def getAuthToken(username:String):String = {
    val claims = internalGetUserToken(username)
    claims.setExpirationTimeMinutesInTheFuture(20)
    claims.setGeneratedJwtId()
    claims.setClaim("TokenType","Auth")
    claims.setClaim("AuthLevel","Authenticated")
    signToken(claims)
  }

  def getRefreshToken(username:String):String = {
    val claims = internalGetUserToken(username)
    claims.setExpirationTimeMinutesInTheFuture(10000)
    claims.setGeneratedJwtId()
    claims.setClaim("TokenType","Refresh")
    signToken(claims) 
  }

  def parseToken(token:String):JwtClaims = {
      jwtConsumer.processToClaims(token)
  }

  def isValidAuthTokenForUser(username:String, token:String):Boolean = {
    val claims = parseToken(token)
    (claims.getSubject == username && claims.getClaimValue("TokenType") == "Auth")
  }

  def isValidRefreshTokenForUser(username:String, token:String):Boolean = {
    val claims = parseToken(token)
    (claims.getSubject == username && claims.getClaimValue("TokenType") == "Refresh")
  }



}
