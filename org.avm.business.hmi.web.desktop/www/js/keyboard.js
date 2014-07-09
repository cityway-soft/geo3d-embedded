function initKB() {
	NumKeyboard.init("test", "textfield", onValidate);
}

function onValidate(val) {
	alert(val);
}

var NumKeyboard = new function() {
	this.div = "";
	this.textfield = "";
	this.callback;

	this.init = function(div, textfield, callback) {
		this.div = div;
		this.textfield = textfield;
		this.callback = callback;
		var items = [];
		for (i = 1; i <= 9; i++) {
			items.push("<div onclick='NumKeyboard.press(" + i
					+ ")' class='numkeyboard'>" + i + "</div>");
			if (i % 3 == 0) {
				items.push("<div class='clear'/>");
			}
		}
		items
				.push("<div onclick='NumKeyboard.press(-1)' class='numkeyboard redbutton'>Corriger</div>")
		items
				.push("<div onclick='NumKeyboard.press(0)' class='numkeyboard'>0</div>")
		items
				.push("<div onclick='NumKeyboard.press(-2)' class='numkeyboard greenbutton'>Valider</div>")
		$('#' + this.div).html(items.join(""));
	}

	this.press = function(val) {
		var data = $("#" + this.textfield).val();
		// suppression char
		if (val == -1) {
			$("#" + this.textfield).val(data.substring(0, data.length - 1));
		} else if (val == -2) {
			this.callback($('#' + this.textfield).val());
		} else {
			$("#" + this.textfield).val(data + "" + val);
		}
	}

}
