package uk.co.tkeetch.sso.tokens

import play.api.Configuration
import java.security.SecureRandom

class AuthTokenProvider (tokenProviderConfig:Option[Configuration]) {

  val config = tokenProviderConfig getOrElse Configuration.empty
  private val authTokenLifespanMinutes = config.getInt("token.lifespan").getOrElse(10)

  private val privateSigningKey = {
    Jose4j.newRsaJsonWebKey(config.getString("privateKey"))
  }
 
  private lazy val jwtConsumer = Jose4j.newJwtConsumer(privateSigningKey)

  def getPublicSigningKeyJson() = Jose4j.publicJsonWebKeyToJson(privateSigningKey)

  def getPrivateSigningKeyJsonB64() = Jose4j.base64Encode(Jose4j.privateJsonWebKeyToJson(privateSigningKey))

  def parseToken(token:String):Map[String,Any] = Jose4j.parseToken(jwtConsumer, token)

  def signToken(token:Map[String,Any]):String = Jose4j.signToken(privateSigningKey, token) 

  protected def prng = new SecureRandom()

  protected def getRandomString(byteLen:Integer):String = {
    val bytes:Integer = if (byteLen <= 0) 20 else byteLen
    val randomBytes = new Array[Byte](bytes)
    val unit = prng.nextBytes(randomBytes)
    randomBytes.map("%02X".format(_)).mkString
  }

  def authTokenLifespanInSeconds:Long = (authTokenLifespanMinutes * 60)

  def generateJti():String = getRandomString(10)

  def generateCsrfToken():String = getRandomString(10)

}

