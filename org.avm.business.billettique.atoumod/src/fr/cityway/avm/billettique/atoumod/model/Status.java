package fr.cityway.avm.billettique.atoumod.model;

class Status {

	private int status;

	public Status(int s) {
		this.status = s;
	}

	protected boolean getBit(int idx) {
		return ((status >> idx) & 0x1) == 1;
	}
	
	protected void setBit(int idx, boolean b) {
		
		int f = 0x1;
		
		f = f << idx;
		status = status | f;
	}
	
	public String toString(){
		String result = Message.toHex(status);
		return result;
	}
}