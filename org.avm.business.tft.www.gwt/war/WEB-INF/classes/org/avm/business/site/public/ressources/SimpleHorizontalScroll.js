/**
 * Simple Horizontal Scroll v0.1 By: me@daantje.nl last update: Sun Dec 24
 * 00:13:19 CET 2006
 * 
 * Documentation: Copy & paste License: LGPL Support: some.
 */

var scrllTmr;

var timeout;

function scroll(parentid, id) {
	// set style
	parent = document.getElementById(parentid);
	parent.style.overflow = 'hidden';

	ruler = document.getElementById(id);
	
	// get canvas
	cw = parseInt(document.getElementById(parentid).offsetWidth);
	
	scrollingText = ruler.innerHTML
	charSize=75; //A MODIFIER !!!!

	timeout=1000;

	//alert("Text size =" + text.length +",cw="+cw+ ",Text.length*charSize= "+(scrollingText.length*charSize));

	if((scrollingText.length*charSize) < cw) {
		//alert("scroll inutile");
		return;
	}
	
	
	ruler.innerHTML = scrollingText
	ruler.style.float = 'left';
	ruler.style.position = 'relative';
	
	w = parseInt(document.getElementById(id).offsetWidth);



	// start scroll
	lft = 0;
	document.getElementById(id).style.left = lft + "px";
	scrollStep(id, cw, w, lft);
}
function scrollStep(id, cw, w, lft) {
	
	// calc and do step
	if (lft <= w * -1)
		lft = cw;
	
	ruler = document.getElementById(id);
	//alert("visible:" + ruler.parent.visibility);
	ruler.style.left = lft + "px";

	// wait and do next...
	if (scrllTmr)
		clearTimeout(scrllTmr);
	s = 'scrollStep(\'' + id + '\',cw,w,' + (lft - 4) + ')';
	scrllTmr = setTimeout(s, timeout);
	timeout=10;
}

function cancelScrolling(){
	if (scrllTmr)
		clearTimeout(scrllTmr);
}
