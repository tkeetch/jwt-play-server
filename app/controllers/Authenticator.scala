package uk.co.tkeetch.sso.controllers.authenticators

import java.security.SecureRandom
import uk.co.tkeetch.sso.data._
import uk.co.tkeetch.sso.AuthTokenProvider

abstract class Authenticator
{
  def authenticate(userid:String, credential:String):Boolean

  def prng = new SecureRandom()

  def getNonce() = {
    val randomBytes = new Array[Byte](20)
    val unit = prng.nextBytes(randomBytes)
    randomBytes.map("%02X".format(_)).mkString
  }

  def getResponse(userid:String, credential:String):Either[AuthenticatorError, AuthenticatorSuccess] = {
    lazy val nonce = getNonce()
    authenticate(userid,credential) match {
      case false => Left( new AuthenticatorError("Authentication Failed!"))
      case true  => Right( new AuthenticatorSuccess(nonce,
                                                     AuthTokenProvider.getAuthToken(userid, nonce),
                                                     AuthTokenProvider.getRefreshToken(userid)) )
    }
  }
}

object PasswordAuthenticator extends Authenticator
{
  def authenticate(userid:String, password:String):Boolean = (userid == "tom" && password == "tom")
}

object RefreshTokenAuthenticator extends Authenticator
{
  def authenticate(userid:String, token:String):Boolean = AuthTokenProvider.isValidRefreshTokenForUser(token, userid)
}


