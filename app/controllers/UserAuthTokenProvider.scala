package uk.co.tkeetch.sso.tokens

import play.api.Configuration
import uk.co.tkeetch.sso.data._

class UserAuthTokenProvider(authTokenProvider:AuthTokenProvider, subject:String) {

  val config = authTokenProvider.config

  private lazy val timestamp:Long = System.currentTimeMillis / 1000
  private lazy val jti:String = authTokenProvider.generateJti()
  private lazy val csrfToken:String = authTokenProvider.generateCsrfToken()

  private lazy val baseUserToken:Map[String,Any] = Map(
	JwtToken.CLAIM_JWT_ID_KEY -> jti,
	JwtToken.CLAIM_SUBJECT_KEY -> subject,
	JwtToken.CLAIM_ISSUED_AT_KEY -> timestamp) 

  private def authToken = {
    val authTokenClaims:Map[String,Any] = Map(
      JwtToken.CLAIM_TOKEN_TYPE_KEY -> JwtToken.TOKEN_TYPE_AUTHENTICATION,
      JwtToken.CLAIM_EXPIRATION_TIME_KEY -> (timestamp + (config.getInt("auth.lifespan").getOrElse(10) * 60)),
      JwtToken.CLAIM_CSRF_TOKEN_KEY -> csrfToken)
    (baseUserToken ++ authTokenClaims)
  }

  private def refreshToken = {
    val refreshTokenClaims:Map[String,Any] = Map(
      JwtToken.CLAIM_TOKEN_TYPE_KEY -> JwtToken.TOKEN_TYPE_REFRESH,
      JwtToken.CLAIM_EXPIRATION_TIME_KEY -> (timestamp + (config.getInt("refresh.lifespan").getOrElse(365*24*60) * 60)))
    (baseUserToken ++ refreshTokenClaims)
  }

  private def tokenHasExpectedContents(token:String,tokenType:String):Boolean = {
    val expected = Map(
      JwtToken.CLAIM_TOKEN_TYPE_KEY -> tokenType,
      JwtToken.CLAIM_SUBJECT_KEY  -> subject)
    authTokenProvider.parseToken(token).filterKeys(expected.keySet.contains(_)).equals(expected)
  }

  def isValidAuthToken(token:String):Boolean = tokenHasExpectedContents(token, JwtToken.TOKEN_TYPE_AUTHENTICATION)

  def isValidRefreshToken(token:String):Boolean = tokenHasExpectedContents(token, JwtToken.TOKEN_TYPE_REFRESH)

  def signToken(token:Map[String,Object]) = authTokenProvider.signToken(token)

  val tokenSet = new TokenSet(csrfToken, authToken, refreshToken)

}

