package org.avm.device.girouette;

public interface Girouette {

	public void destination(String code);

	public int getStatus() throws Exception;

}
