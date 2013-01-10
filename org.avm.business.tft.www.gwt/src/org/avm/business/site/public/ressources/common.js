<!-- 


function UTC2LocaleTime(time)
{
	var now=new Date();

	var localOffset = now.getTimezoneOffset() * 60000;
	now.setTime(time-localOffset);
	
	return now.getTime();
}

-->
