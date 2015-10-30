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
  $("#authToken").val(responseData.authToken);
  $("#refreshToken").val(responseData.refreshToken);
  $("#csrfToken").val(responseData.csrfToken);
}

function loginFailure() {
  $("#authToken").val("");
  $("#refreshToken").val("");
  $("#csrfToken").val("");
}

function loginFailureCallback(responseData, textStatus, errorThrown) {
  loginFailure();
  alert('Login Failed :(');
}

function refreshFailureCallback(responseData, textStatus, errorThrown) {
  loginFailure();
  alert('Token Refresh Failed :(');
}

function submitLogin() {
  authenticate('login',
               $("#userid").val(),
			   $("#password").val(),
			   authenticationSuccessCallback,
			   loginFailureCallback)
}

function submitRefresh() {
  authenticate('refresh',
               $("#userid").val(),
			   $("#refreshToken").val(),
			   authenticationSuccessCallback,
			   refreshFailureCallback)
}

function init() {
  //Register onClick Handler for Login
  $("#loginButton").click(submitLogin)

  //Register onClick Handler for Token Refresh
  $("#refreshButton").click(submitRefresh)
}

init()
