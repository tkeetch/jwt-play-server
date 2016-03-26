package uk.co.tkeetch.sso.controllers

import javax.inject._
import play.api.Configuration

import play.api.mvc._
import play.api.http._
import play.api.libs.json._
import uk.co.tkeetch.sso._
import uk.co.tkeetch.sso.AuthTokenProvider
import uk.co.tkeetch.sso.controllers.authenticators._
import uk.co.tkeetch.sso.data._

class Auth @Inject() (config:Configuration) extends Controller 
{
  val authTokenProvider = new AuthTokenProvider(config.getConfig("sso.authtoken"))
  val loginAuthenticator = new PasswordAuthenticator(authTokenProvider, config.getConfig("sso.users"))
  val refreshAuthenticator = new RefreshTokenAuthenticator(authTokenProvider)

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

  def postLoginJson() = Action(parse.json) { implicit request =>
    doAuthJson(loginAuthenticator, request.body)
  }

  def postRefreshJson() = Action(parse.json) { implicit request =>
    doAuthJson(refreshAuthenticator, request.body)
  }

  def getPublicSigningKey() = Action { implicit request =>
    Ok(authTokenProvider.getPublicSigningKeyJson())
  }

  def getPrivateSigningKey() = Action { implicit request =>
    println("Private Signing Key Config: " + authTokenProvider.getPrivateSigningKeyJsonB64())
    Forbidden(Json.toJson(Map("msg" -> "The private key is displayed on the application console.")))
  }
}

