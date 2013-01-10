package org.avm.business.site.client.common;

public class Page7 extends Page {

	public static native void setCurrent(int id, boolean inside, int rang)/*-{
		$wnd.setCurrentStop(id, inside, rang);
	}-*/;

	public static native void addStop(int id, String name, int rang)/*-{
		$wnd.addStop(id, name, rang);
	}-*/;

	public static native void test(String msg)/*-{
		$wnd.alert(msg);
	}-*/;

	public static native void reset()/*-{
		$wnd.reset();
	}-*/;

	public static native void setLineStyle(String style)/*-{
		$wnd.setLineStyle(style);
	}-*/;

	public static final String NAME = "page7";

	public static void clearTitle() {
		Page.setText("titre-principal", "");
	}

}
