//JavaScript functions for /login

function authServiceRequest(url, payload, onSuccess, onError) {
  $.ajax({
    type: 'POST',
    url: url,
    crossDomain: true,
    data: JSON.stringify(payload),
    headers: { 'Content-Type': 'text/json' },
    jsonp: false,
    success: onSuccess,
    error: onError
  })
}

function getRelativeUrl(action) {
  return '/' + action + '.json';
}

function authenticate(path, userid, credential, onSuccess, onError) {
  authServiceRequest(
    getRelativeUrl(path),
	{ "userid": userid, "credential": credential },
    onSuccess,
	onError);
}

function authenticationSuccessCallback(responseData, textStatus, jqXHR) {
  $("#errorMsg").html("")
  $("#authToken").val(responseData.authToken);
  $("#refreshToken").val(responseData.refreshToken);
  $("#csrfToken").val(responseData.csrfToken);

  decodeNewTokens();
}

function loginFailure(msg) {
  $("#errorMsg").text(msg).css("color","red")
  $("#authToken").val("");
  $("#refreshToken").val("");
  $("#csrfToken").val("");
  $("#decodedAuthToken").val("");
  $("#decodedRefreshToken").val("");

}

function loginFailureCallback(responseData, textStatus, errorThrown) {
  loginFailure("Login Failed!");
}

function refreshFailureCallback(responseData, textStatus, errorThrown) {
  loginFailure("Token Refresh Failed!");
}

function decodeToken(jwt) {
  var b64Claims = jwt.split('.')[1];
  if(b64Claims != undefined)
  {
    var jsonClaims = atob( b64Claims );
    return jsonClaims.replace(/\{/g, '\{\n  ').replace(/,"/g, ',\n  "').replace(/\}/g, '\n\}')
  }
  return '';
}

function decodeNewTokens() {
  $("#decodedAuthToken").val(decodeToken($("#authToken").val()))
  $("#decodedRefreshToken").val(decodeToken($("#refreshToken").val()))
}

function submitLoginOnClick() {
  authenticate('login',
               $("#userid").val(),
			   $("#password").val(),
			   authenticationSuccessCallback,
			   loginFailureCallback);
}

function submitRefreshOnClick() {
  authenticate('refresh',
               $("#userid").val(),
			   $("#refreshToken").val(),
			   authenticationSuccessCallback,
			   refreshFailureCallback);
}

function init() {
  //Register onClick Handler for Login
  $("#loginButton").click(submitLoginOnClick)

  //Register onClick Handler for Token Refresh
  $("#refreshButton").click(submitRefreshOnClick)
}

init()
