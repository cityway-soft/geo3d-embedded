package org.avm.hmi.swt.application.display;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class Application {
	private static HashMap _hashFonts;

	public static Font getFont(int deltasize, int style) {
		Font font = null;
		String key = deltasize + "_" + style;

		if (_hashFonts == null) {
			_hashFonts = new HashMap();
		}
		font = (Font) _hashFonts.get(key);
		if (font == null) {
			//System.out.println("[---APPLICATION---] create new font : " + key);
			font = new Font(Display.getDefault(), AVMDisplay.DEFAULT_FONT,
					AVMDisplay.DEFAULT_FONTSIZE + deltasize, style);
			_hashFonts.put(key, font);
		} else {
			//System.out.println("[---APPLICATION---] reuse font : " + key);
			if (font.isDisposed()) {
				_hashFonts.remove(font);
				//System.out.println("[---APPLICATION---] font previously removed, so recreate  : " + key);
				font = new Font(Display.getDefault(), AVMDisplay.DEFAULT_FONT,
						AVMDisplay.DEFAULT_FONTSIZE + deltasize, style);
				_hashFonts.put(key, font);
				
			}

		}
		return font;
	}

	protected void finalize() throws Throwable {
		super.finalize();
		if (_hashFonts != null) {
			Iterator iter = _hashFonts.keySet().iterator();
			while (iter.hasNext()) {
				Font font = (Font) iter.next();
				if (font.isDisposed() == false) {
					font.dispose();
				}
			}
			_hashFonts = null;
		}

	}

}
