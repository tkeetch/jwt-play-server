package uk.co.tkeetch.sso.controllers

import javax.inject._
import play.api.Configuration

import play.api.mvc._
import play.api.http._
import play.api.libs.json._
import uk.co.tkeetch.sso._
import uk.co.tkeetch.sso.controllers.authenticators._
import uk.co.tkeetch.sso.data._

class Auth @Inject() (config:Configuration) extends Controller 
{
  val userConfig = config.getConfig("users").getOrElse(Configuration.empty)
  val loginAuthenticator = new PasswordAuthenticator(userConfig)

  val refreshAuthenticator = RefreshTokenAuthenticator

  def getLoginPage() = Action { Ok(views.html.login()) }

  def redirectToLoginPage() = Action { Redirect(routes.Auth.getLoginPage()) }

  def doAuth(auth:Authenticator, userid:String, credential:String):Result = {
    auth.getResponse(userid, credential) match {
      case Left(err)   => Unauthorized(Json.toJson(err.toMap()))
      case Right(resp) => Ok(Json.toJson(resp.toMap()))
    }
  }

  def doAuthJson(auth:Authenticator, request:JsValue) = {
    val authResponse = for {
      userid <- (request \ "userid").asOpt[JsString]
      credential <- (request \ "credential").asOpt[JsString]
    } yield doAuth(auth, userid.value, credential.value)

    authResponse getOrElse BadRequest("Missing Parameters")
  }

  def doLoginJson() = Action(parse.json) { implicit request =>
    doAuthJson(loginAuthenticator, request.body)
  }

  def doRefreshJson() = Action(parse.json) { implicit request =>
    doAuthJson(refreshAuthenticator, request.body)
  }

  def getPublicSigningKey() = Action { implicit request =>
    Ok(Json.toJson(AuthTokenProvider.getPublicSigningKey()))
  }
}

