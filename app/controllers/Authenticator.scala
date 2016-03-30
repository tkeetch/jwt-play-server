package uk.co.tkeetch.sso.controllers.authenticators

import java.security.SecureRandom
import play.api.Configuration
import uk.co.tkeetch.sso.data._
import uk.co.tkeetch.sso.AuthTokenProvider

abstract class Authenticator(authTokenProvider:AuthTokenProvider)
{
  def authenticate(userid:String, credential:String):Boolean

  protected def prng = new SecureRandom()

  protected def getNonce() = {
    val randomBytes = new Array[Byte](20)
    val unit = prng.nextBytes(randomBytes)
    randomBytes.map("%02X".format(_)).mkString
  }

  def getResponse(userid:String, credential:String):Either[AuthenticatorError, AuthenticatorSuccess] = {
    lazy val nonce = getNonce()
    authenticate(userid,credential) match {
      case false => Left( new AuthenticatorError("Authentication Failed!"))
      case true  => Right( new AuthenticatorSuccess(nonce,
                                                     authTokenProvider.getAuthToken(userid, nonce),
                                                     authTokenProvider.getRefreshToken(userid)) )
    }
  }
}

class PasswordAuthenticator(authTokenProvider:AuthTokenProvider, userConfig:Option[Configuration]) extends Authenticator(authTokenProvider)
{
  private val users = userConfig.getOrElse(Configuration.empty)

  protected def comparePasswords(p1:String, p2:String):Boolean = p1.equals(p2)

  def authenticate(userid:String, password:String):Boolean = {
    users.getString(userid) match {
      case Some(p) => comparePasswords(password, p)
      case _ => println("User not found: " + userid); false 
    }
  }
}

class RefreshTokenAuthenticator(authTokenProvider:AuthTokenProvider) extends Authenticator(authTokenProvider)
{
  def authenticate(userid:String, token:String):Boolean = {
    authTokenProvider.isValidRefreshTokenForUser(token, userid)
  }
}


