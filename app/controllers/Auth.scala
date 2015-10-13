package uk.co.tkeetch.sso.controllers

import play.api.mvc._
import uk.co.tkeetch.sso.AuthToken

abstract class Authenticator
{
  def authenticate(userid:String, password:String):Boolean
}

object PasswordAuthenticator extends Authenticator
{
  def authenticate(userid:String, password:String):Boolean = (userid == "tom" && password == "tom")
}

object RefreshTokenAuthenticator extends Authenticator
{
  def authenticate(userid:String, token:String):Boolean = AuthToken.isValidRefreshTokenForUser(userid,token)
}


class Auth extends Controller 
{
  val loginAuth = PasswordAuthenticator
  val refreshAuth = RefreshTokenAuthenticator

  def authenticate(authenticator:Authenticator, userid:String, credential:String):Boolean = authenticator.authenticate(userid,credential)

  def login(userid:String,password:String) = Action { implicit request =>
    Ok(views.html.login(authenticate(loginAuth, userid, password), AuthToken.getAuthToken(userid), AuthToken.getRefreshToken(userid)))
  }

  def refresh(userid:String, token:String) = Action { implicit request =>
    Ok(views.html.login(authenticate(refreshAuth, userid, token), AuthToken.getAuthToken(userid), AuthToken.getRefreshToken(userid)))
  }
}
