package uk.co.tkeetch.sso.tokens

import org.jose4j._
import org.jose4j.jwk._
import org.jose4j.jwt._
import org.jose4j.jws._
import org.jose4j.jwt.consumer._
import org.jose4j.json.JsonUtil
import org.jose4j.base64url.Base64Url

import scala.collection.JavaConverters._
import scala.collection.immutable.Map
import scala.util.{Try,Success,Failure}

import java.nio.charset.StandardCharsets

object Jose4j {

  def newRsaJsonWebKey(jwkJson:Option[String]):RsaJsonWebKey = {
    jwkJson match {
      case Some(jwkJson) => new RsaJsonWebKey(JsonUtil.parseJson(new String(Base64Url.decode(jwkJson))))
      case None => RsaJwkGenerator.generateJwk(2048)
    }
  }
  
  def newJwtConsumer(publicKey:RsaJsonWebKey):JwtConsumer = {
    (new JwtConsumerBuilder().setRequireExpirationTime().setVerificationKey(publicKey.getKey()).build())
  }

  def base64Encode(str:String):String = Base64Url.encode(str.getBytes(StandardCharsets.UTF_8))
  def base64Decode(b64:String):Option[String] = Try(new String(Base64Url.decode(b64))).toOption

  def publicJsonWebKeyToJson(jwk:JsonWebKey) = jwk.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY)
  def privateJsonWebKeyToJson(jwk:JsonWebKey) = jwk.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE)

  private def claimsToMap(claims:JwtClaims):Map[String,Any] = {
    claims.getClaimsMap().asScala.toMap
  }

  private def mapToClaims(map:Map[String,Any]) = {
    val claims = new JwtClaims()
    for ((k,v) <- map) {
      claims.setClaim(k,v)
    }
    claims
  }

  def getBaseSubjectToken(subject:String):Map[String,Any] = {
    val claims = new JwtClaims()
    claims.setSubject(subject)
    claims.setExpirationTimeMinutesInTheFuture(1)
    claims.setIssuedAtToNow()
    claimsToMap(claims)
  }

  def signToken(privateKey:RsaJsonWebKey, token:Map[String,Any]):String = {
    val jws = new JsonWebSignature()
    jws.setPayload(mapToClaims(token).toJson())
    jws.setKey(privateKey.getRsaPrivateKey())
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256)
    jws.getCompactSerialization()
  }

  def parseToken(jwtConsumer:JwtConsumer, token:String):Map[String,Any] = {
    Try(claimsToMap(jwtConsumer.processToClaims(token))) match {
      case Failure(e) => Map()
      case Success(map) => map
    }
  }

}

