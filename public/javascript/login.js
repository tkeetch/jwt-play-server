//JavaScript functions for /login
"use strict";

var AuthServiceClient = AuthServiceClient || {};

AuthServiceClient.authServiceRequest = function(url, payload, onSuccess, onError) {
    $.ajax({
        type: 'POST',
        url: url,
        crossDomain: true,
        data: JSON.stringify(payload),
        headers: {
            'Content-Type': 'text/json'
        },
        jsonp: false,
        success: onSuccess,
        error: onError
    });
};

AuthServiceClient.authenticate = function(action, userid, credential, onSuccess, onError) {

    function getRelativeUrl(action) {
        return ('/' + action + '.json');
    }

    AuthServiceClient.authServiceRequest(
        getRelativeUrl(action),
        {
            "userid": userid,
            "credential": credential
        },
        onSuccess,
        onError);
};

AuthServiceClient.decodeTokenClaims = function(jwtToken) {
    var b64Claims = jwtToken.split('.')[1];
    if (b64Claims !== undefined) {
        var jsonClaims = atob(b64Claims);
        return jsonClaims.replace(/\{/g, '\{\n  ').replace(/,"/g, ',\n  "').replace(/\}/g, '\n\}');
    }
    return '';
};

$("document").ready(function() {

    function decodeNewTokens() {
        var decodeToken = AuthServiceClient.decodeTokenClaims;
        var b64AuthToken = $("#authToken").val();
        var b64RefreshToken = $("#refreshToken").val();
        $("#decodedAuthToken").val(decodeToken(b64AuthToken));
        $("#decodedRefreshToken").val(decodeToken(b64RefreshToken));
    }

    function authenticationSuccessCallback(responseData) {
        $("#errorMsg").html("");
        $("#authToken").val(responseData.authToken);
        $("#refreshToken").val(responseData.refreshToken);
        $("#csrfToken").val(responseData.csrfToken);

        decodeNewTokens();
    }

    function loginFailure(msg) {
        $("#errorMsg").text(msg).css("color", "red");
        $("#authToken").val("");
        $("#refreshToken").val("");
        $("#csrfToken").val("");
        $("#decodedAuthToken").val("");
        $("#decodedRefreshToken").val("");
    }

    function loginFailureCallback() {
        loginFailure("Login Failed!");
    }

    function refreshFailureCallback() {
        loginFailure("Token Refresh Failed!");
    }

    function submitLoginOnClick() {
        AuthServiceClient.authenticate('login',
            $("#userid").val(),
            $("#password").val(),
            authenticationSuccessCallback,
            loginFailureCallback);
    }

    function submitRefreshOnClick() {
        AuthServiceClient.authenticate('refresh',
            $("#userid").val(),
            $("#refreshToken").val(),
            authenticationSuccessCallback,
            refreshFailureCallback);
    }

    //Register onClick Handler for Login
    $("#loginButton").click(submitLoginOnClick);

    //Register onClick Handler for Token Refresh
    $("#refreshButton").click(submitRefreshOnClick);
});

