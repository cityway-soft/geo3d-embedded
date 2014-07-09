var update_timer;
var update_timeout = 100;
var last_state = "";

function showAvmActivity(name) {
	$('div[class="avmactivity"]').hide();
	/* $(name).slideToggle( "slow" ); */
	$(name).show();
}

function doPriseCourse(idu) {
	$.post("/avm/prisecourse", {
		idu : idu
	});
}

function doPriseService() {
	$.post("/avm/priseservice", {
		idu : $("#iduservice").val()
	});
}

function doPrisePoste() {
	$.post("/avm/priseposte");
}

function doFinService() {
	$.post("/avm/finservice");
}

function doFinCourse() {
	$.post("/avm/fincourse");
}

function doDeviation() {
	$.post("/avm/deviation");
}

function doDepart() {
	$.post("/avm/depart");
}

function initAvm() {
	$("#debugavm").html("test SAE");
	showAvmActivity("");
	doUpdate();

}

function doUpdate() {
	$.ajax({
		url : '/avm/waitstate',
		dataType : 'json',
		success : callback
	});
}

function callback(json) {
	$("#debugavm").html("State : " + json.statename);
	// reenclenche le timer
	treatState(json);
	update_timer = setTimeout("doUpdate()", update_timeout);
}

function treatState(json) {
	var state = json.statename;
	if (last_state != state) {
		if (state == "ETAT_INITIAL") {
			showAvmActivity("#avmpriseposte");
		} else if (state == "ATTENTE_SAISIE_SERVICE") {
			showAvmActivity("#avmpriseservice");
		} else if (state == "ATTENTE_SAISIE_COURSE") {
			$.ajax({
				url : '/avm/listcourses',
				dataType : 'json',
				success : listCourseCB
			});
		} else if (state == "ATTENTE_DEPART" && last_state != state) {
			attenteDepart(json);
		} else if (state == "EN_COURSE_HORS_ITINERAIRE") {
			enterEnCourse(json);
		} else if (state == "EN_COURSE_ARRET_SUR_ITINERAIRE") {
			enCourseArret(json);
		} else if (state == "EN_COURSE_INTERARRET_SUR_ITINERAIRE") {
			enCourseInterArret(json);
		}
	}
	last_state = state;
}

function enCourseInterArret(json) {
	$("#avmencoursetitle").html("<p>Prochain arret :</p>");
	$("#avmencoursecontent").html(
			"<p>" + json.prochainpoint.nom
					+ "<br/>Arriv&eacute; pr&eacute;vu : "
					+ json.prochainpoint.heurearriveetheorique + "</p>");
	showAvmActivity("#avmencourse");
}

function enCourseArret(json) {
	$("#avmencoursetitle").html("<p>Nous sommes &agrave; :</p>");
	$("#avmencoursecontent").html(
			"<p>" + json.dernierpoint.nom
					+ "<br/>D&eacute;part pr&eacute;vu : "
					+ json.dernierpoint.heuredeparttheorique + "</p>");
	// showAvmActivity("#avmencourse");
	showAvmActivity("#avmencourse");
}

function enterEnCourse(json) {
	$("#avmencourseinfos").html("<p>Course " + json.course.idu + "</p>");
	$("#avmencoursetitle").html("<p>Hors itin&eacute;raire</p>");
	$("#avmencoursecontent").html(
			"<p>Attente d&eacute;tection d'un arret ...</p>");
	showAvmActivity("#avmencourse");
}

function attenteDepart(json) {
	var points = json.course.points;
	var dep = points[0];
	var end = points[points.length - 1];
	$("#startpoint").html("<p>D&eacute;part de <br/>" + dep.nom + "</p>");
	$("#endpoint").html("<p>Destination  :<br/>" + end.nom + "</p>");
	showAvmActivity("#avmattentedepart");
}

function listCourseCB(json) {
	var items = [];
	$.each(json, function(key, val) {
		if (val.terminee == "T") {
			items.push("<tr><td>" + val.terminee + "</td><td>" + val.idu
					+ "</td><td>" + val.hdepart + "</td><td>" + val.terminus
					+ "</td></tr>");
		} else {
			items.push("<tr onclick=\"doPriseCourse(" + val.idu + ");\"><td>"
					+ val.terminee + "</td><td>" + val.idu + "</td><td>"
					+ val.hdepart + "</td><td>" + val.terminus + "</td></tr>");
		}
		$("#avmlistcourse").html($("<table/>", {
			"class" : "list",
			html : items.join("")
		}));
		showAvmActivity("#avmlistcourse");
	});

}