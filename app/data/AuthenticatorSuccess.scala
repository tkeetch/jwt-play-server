package uk.co.tkeetch.sso.data

import scala.collection.immutable.Map

class AuthenticatorSuccess(nonce:String,
                           auth:String,
                           refresh:String)
{
  val csrfToken:String = nonce
  val authToken:String = auth
  val refreshToken:String = refresh

  def toMap() = Map(
    "csrfToken" -> csrfToken,
    "authToken" -> authToken,
    "refreshToken" -> refreshToken)
}

