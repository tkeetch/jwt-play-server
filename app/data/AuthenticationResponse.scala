package uk.co.tkeetch.sso.data

import play.api.libs.json._

class AuthenticatorResponse(nonce:String,
                            auth:String,
                            refresh:String)
{
  val csrfToken:String = nonce
  val authToken:String = auth
  val refreshToken:String = refresh

  def toJson() = Json.obj(
    "csrfToken" -> csrfToken,
    "authToken" -> authToken,
    "refreshToken" -> refreshToken)
}

