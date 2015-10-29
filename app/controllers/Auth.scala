package uk.co.tkeetch.sso.controllers

import play.api.mvc._
import play.api.http._
import play.api.libs.json._
import uk.co.tkeetch.Cors
import uk.co.tkeetch.sso._
import uk.co.tkeetch.sso.controllers._
import uk.co.tkeetch.sso.controllers.authenticators._
import uk.co.tkeetch.sso.data._

class Auth extends Controller 
{
  val loginAuthenticator = PasswordAuthenticator
  val refreshAuthenticator = RefreshTokenAuthenticator

  def getLoginPage() = Action { implicit request => Ok("getLoginPage") }

  def doAuth(auth:Authenticator, userid:String, credential:String):Result = {
    auth.getResponse(userid, credential) match {
      case Left(err)   => Unauthorized(err)
      case Right(resp) => Ok(resp.toJson())
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
    Cors.allowAll(doAuthJson(loginAuthenticator, request.body))
  }

  def doRefreshJson() = Action(parse.json) { implicit request =>
    Cors.allowAll(doAuthJson(refreshAuthenticator, request.body))
  }
}

