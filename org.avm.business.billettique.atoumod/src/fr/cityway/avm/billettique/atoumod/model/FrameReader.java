package fr.cityway.avm.billettique.atoumod.model;

public class FrameReader {
	int offset = 0;
	int index = 0;
	private String frame;
	private String[] names;
	private int[] sizes;

	public FrameReader(String frame, String[] names, int[] sizes) {
		this.frame = frame;
		this.names = names;
		this.sizes = sizes;
	}

	public String getNextField() throws Exception {
		String result = null;
		try {
			int size = sizes[index]*2;
			result = frame.substring(offset, offset+size);
			offset += size;
			index++;
		} catch (Throwable t) {
			throw new Exception ("Error on field "+ names[index] + " :" + t.getMessage());
		}
		return result;
	}
}