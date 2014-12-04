package org.avm.device.ihmi.z8;

public class Test {
		
	public static void main(String[] args) {
		
		System.out.println("Backlight level : " +Z8Access.getBacklightLevel());
		System.out.println("Light level : " + Z8Access.getLightLevel());
		System.out.println("Temperature :"+ Z8Access.getBoardTemperature());
		System.out.println("State : "+ Z8Access.getSystemCurrentState());
		System.out.println("Voltage : " + Z8Access.getPowerSuplyLevel());
		new CheckSystemState(1000);
		try {
			synchronized (Test.class) {
				Test.class.wait();
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
