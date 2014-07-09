var authState = 0;
var authMat = "";
/**
 * récupère les champs et poste la requete
 */
function dologin (login, pass){
	$.post("/authentification", { action: "login", login: login, password: pass} );
}

function initAuth(){
	NumKeyboard.init("authkb", "authText", onValidate);
}

function onValidate(value){
	if (authState == 0){
		authState = 1;
		authMat = value;
		$("#authtitle").text("Code");
		$("#authText").val("");
	}else if (authState == 1){
		authState = 0;
		dologin(authMat, value);
		authMat = "";
		$("#authtitle").text("Matricule");
		$("#authText").val("");
	}
}