var clock_update_freq=1000;

function initDesktop(){
	showActivity('defaultActivity');
	updateClock();
}

function updateClock(){
	var d = new Date();
	var min = d.getMinutes();
	if (min < 10){
		min = "0" + min;
	}
	var hours = d.getHours();
	if (hours < 10){
		hours = "0" + hours;
	}
	var dp = ":";
	if (d.getSeconds () % 2 == 0){
		dp = " ";
	}
	$("#clock").html(hours + dp + min);
	setTimeout("updateClock()", clock_update_freq);
}


function showActivity(name){
	$('div[class="activity"]').hide();
	/*$(name).slideToggle( "slow" );*/
	$("#"+name).show();
	$('a[class="switchlink"]').removeAttr('style');
	$("#menu"+name).css("background-color", "#fff");
	$("#menu"+name).css("color", "#000");
}


