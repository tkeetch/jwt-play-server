package uk.co.tkeetch.sso.authenticators

import play.api.Configuration

import uk.co.tkeetch.sso._
import uk.co.tkeetch.sso.data._
import uk.co.tkeetch.sso.tokens._

abstract class Authenticator(authTokenProvider:AuthTokenProvider)
{
  def authenticate(userid:String, credential:String):Boolean

  def getResponse(userid:String, credential:String):Either[AuthenticatorError, AuthenticatorSuccess] = {
    lazy val tokens = (new UserAuthTokenProvider(authTokenProvider, userid)).tokenSet

    authenticate(userid,credential) match {
      case false => Left( new AuthenticatorError("Authentication Failed!"))
      case true  => Right( new AuthenticatorSuccess(tokens.csrf,
                                                    authTokenProvider.signToken(tokens.auth),
                                                    authTokenProvider.signToken(tokens.refresh)))
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
    (new UserAuthTokenProvider(authTokenProvider, userid)).isValidRefreshToken(token)
  }
}

class AuthTokenAuthenticator(authTokenProvider:AuthTokenProvider) extends Authenticator(authTokenProvider)
{
  def authenticate(userid:String, token:String):Boolean = {
    (new UserAuthTokenProvider(authTokenProvider, userid)).isValidAuthToken(token)
  }
}


