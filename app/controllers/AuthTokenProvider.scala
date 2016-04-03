package uk.co.tkeetch.sso.tokens

import play.api.Configuration
import uk.co.tkeetch.sso.util.RandomStringGenerator 

class AuthTokenProvider (tokenProviderConfig:Option[Configuration]) {

  val config = tokenProviderConfig getOrElse Configuration.empty

  private val privateSigningKey = Jose4j.newRsaJsonWebKey(config.getString("privateKey"))
  private val jwtConsumer = Jose4j.newJwtConsumer(privateSigningKey)

  def getPublicSigningKeyJson() = Jose4j.publicJsonWebKeyToJson(privateSigningKey)

  def getPrivateSigningKeyJsonB64() = Jose4j.base64Encode(Jose4j.privateJsonWebKeyToJson(privateSigningKey))

  def parseToken(token:String):Map[String,Any] = Jose4j.parseToken(jwtConsumer, token)

  def signToken(token:Map[String,Any]):String = Jose4j.signToken(privateSigningKey, token) 

  private def getConfigInt(key:String, default:Int):Int = config.getInt(key).getOrElse(default)

  def generateJti():String = RandomStringGenerator.getString(getConfigInt("jti-length", 10))

  def generateCsrfToken():String = RandomStringGenerator.getString(getConfigInt("csrf-length", 10))

}

