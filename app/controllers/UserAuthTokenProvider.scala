package uk.co.tkeetch.sso.tokens

import play.api.Configuration
import uk.co.tkeetch.sso.data._

class UserAuthTokenProvider(authTokenProvider:AuthTokenProvider, subject:String) {

  val config = authTokenProvider.config

  private lazy val timestamp:Long = System.currentTimeMillis / 1000
  private lazy val jti:String = authTokenProvider.generateJti()

  private lazy val baseUserToken:Map[String,Any] = Map(
	"jti"  -> jti,
	"sub"  -> subject,
	"iat"  -> timestamp,
	"crit" -> "tt") 

  def signToken(token:Map[String,Object]) = authTokenProvider.signToken(token)

  private val csrfToken = authTokenProvider.generateCsrfToken()

  private def authToken = {
    val authTokenClaims:Map[String,Any] = Map(
      "tt"   -> "Auth",
      "exp"  -> (timestamp + (config.getInt("auth.lifespan").getOrElse(10) * 60)),
      "csrf" -> csrfToken)
    (baseUserToken ++ authTokenClaims)
  }

  private def refreshToken = {
    val refreshTokenClaims:Map[String,Any] = Map(
      "tt"  -> "Refresh",
      "exp" -> (timestamp + (config.getInt("refresh.lifespan").getOrElse(365*24*60) * 60)))
    (baseUserToken ++ refreshTokenClaims)
  }

  def tokenSet = new TokenSet(csrfToken, authToken, refreshToken)

  private def tokenHasExpectedContents(token:String,tokenType:String):Boolean = {
    val expected = Map("tt" -> tokenType,"sub" -> subject)
    authTokenProvider.parseToken(token).filterKeys(expected.keySet.contains(_)).equals(expected)
  }

  def isValidAuthToken(token:String):Boolean = tokenHasExpectedContents(token, "Auth")

  def isValidRefreshToken(token:String):Boolean = tokenHasExpectedContents(token, "Refresh")
}





