
/**
 * récupère les champs et poste la requete
 */
function dologin (){
	$.post("/authentification", { action: "login", login: $("#login").val(), password: $("#password").val()} );
}