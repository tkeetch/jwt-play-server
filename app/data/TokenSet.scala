package uk.co.tkeetch.sso.data

class TokenSet(
  csrfToken:String,
  authToken:JwtToken,
  refreshToken:JwtToken) {

  val csrf = csrfToken
  val auth = authToken
  val refresh = refreshToken
}
