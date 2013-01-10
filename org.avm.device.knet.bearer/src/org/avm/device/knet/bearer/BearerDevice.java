/**
 * 
 */
package org.avm.device.knet.bearer;

/**
 * @author lbr
 * 
 */
public interface BearerDevice {

	public String getName();

	public void changeStatus(String bearerName, String status);
}
