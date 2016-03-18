package uk.co.tkeetch.sso.data

import scala.collection.immutable.Map

class AuthenticatorError(msg:String)
{
  val message:String = msg

  def toMap() = Map(
    "msg" -> message )
}

