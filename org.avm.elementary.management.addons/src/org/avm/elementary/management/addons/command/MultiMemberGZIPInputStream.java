/**
 * 
 */
package org.avm.elementary.management.addons.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author lbr
 * 
 */
public class MultiMemberGZIPInputStream extends GZIPInputStream {
	public MultiMemberGZIPInputStream(InputStream in, int size)
			throws IOException {
		// Wrap the stream in a PushbackInputStream...
		super(new PushbackInputStream(in, size), size);
		this.size = size;
	}

	public MultiMemberGZIPInputStream(InputStream in) throws IOException {
		// Wrap the stream in a PushbackInputStream...
		super(new PushbackInputStream(in, 1024));
		this.size = -1;
	}

	private MultiMemberGZIPInputStream(MultiMemberGZIPInputStream parent)
			throws IOException {
		super(parent.in);
		this.size = -1;
		this.parent = parent.parent == null ? parent : parent.parent;
		this.parent.child = this;
	}

	private MultiMemberGZIPInputStream(MultiMemberGZIPInputStream parent,
			int size) throws IOException {
		super(parent.in, size);
		this.size = size;
		this.parent = parent.parent == null ? parent : parent.parent;
		this.parent.child = this;
	}

	private MultiMemberGZIPInputStream parent;

	private MultiMemberGZIPInputStream child;

	private int size;

	private boolean eos;

	public int read(byte[] inputBuffer, int inputBufferOffset,
			int inputBufferLen) throws IOException {

		if (eos) {
			return -1;
		}
		if (this.child != null)
			return this.child.read(inputBuffer, inputBufferOffset,
					inputBufferLen);
		int charsRead = super.read(inputBuffer, inputBufferOffset,
				inputBufferLen);
		if (charsRead == -1) {
			// Push any remaining buffered data back onto the stream
			// If the stream is then not empty, use it to construct
			// a new instance of this class and delegate this and any
			// future calls to it...
			int n = inf.getRemaining() - 8;
			if (n > 0) {
				// More than 8 bytes remaining in deflater
				// First 8 are gzip trailer. Add the rest to
				// any un-read data...
				((PushbackInputStream) this.in).unread(buf, len - n, n);
			} else {
				// Nothing in the buffer. We need to know whether or not
				// there is unread data available in the underlying stream
				// since the base class will not handle an empty file.
				// Read a byte to see if there is data and if so,
				// push it back onto the stream...
				byte[] b = new byte[1];
				int ret = in.read(b, 0, 1);
				if (ret == -1) {
					eos = true;
					return -1;
				} else
					((PushbackInputStream) this.in).unread(b, 0, 1);
			}
			/*
			 * //LBR Notre fichier GZIP contient en fin de fichier les lignes :
			 * 000054E0 0D 99 BC 1F D4 D6 2D 67 87 7F 06 91 C1 3F C3 6F 41 43 72
			 * B5 07 10 00 00 E2 81 64 2B 59 F3 64 D0
			 * ......-g.....?.oACr.......d+Y.d. 00005500 B0 BA 23 24 32 77 C4 59
			 * A1 C3 EA CD A0 0A 67 64 92 99 84 85 0A 05 40 CD 19 59 A6 A2 56 A8
			 * 5B E7 ..#$2w.Y......gd......@..Y..V.[. 00005520 32 C4 6C 51 D3 94
			 * 4C 0B 34 18 D4 A2 A6 F1 30 75 46 7A A1 40 CD 31 25 A3 68 94 BE 34
			 * 8F 95 C2 7B 2.lQ..L.4.....0uFz.@.1%.h..4...{ 00005540 A3 C0 48 E7
			 * DC C8 76 25 0B 6F 85 A4 B5 DF 5D 01 3A DA F9 78 86 23 EA CA DE D1
			 * 6E 06 7A AD F0 4D ..H...v%.o....].:..x.#....n.z..M 00005560 3B 05
			 * 85 1B 6D D4 51 E1 D8 65 95 22 1B C5 F4 70 56 B8 CD 89 12 03 5E 07
			 * 66 4E 3A C1 70 2A 5B E4 ;...m.Q..e."...pV.....^.fN:.p[. 00005580
			 * FA 65 06 EA 0A C1 B1 89 2D AA EE DA E1 0B 05 D4 C7 36 AC 0C FB 2C
			 * C3 C5 AB 25 B1 E5 BF CF B8 5C .e......-........6...,...%.....\
			 * 000055A0 78 4B C2 99 7A 03 F1 59 CE 17 EB 02 CF 06 D7 21 BC AB 20
			 * 57 FD 01 0D 5B E4 A9 87 A4 42 03 5A 9E xK..z..Y.......!..
			 * W...[....B.Z. 000055C0 72 04 74 EA F5 5A 90 D6 B6 6C 8E EF A6 DB
			 * C4 94 42 0E 48 B5 76 E5 39 9B C9 B6 18 93 20 5D E7 E6
			 * r.t..Z...l......B.H.v.9..... ].. 000055E0 2A 95 E2 E0 D0 E4 11 D5
			 * 49 13 D5 66 43 5B B8 58 B9 0E 1A BB 76 AD 00 EE 72 0B B7 77 44 D1
			 * 5C C7.......I..fC[.X....v...r..wD.\. 00005600 La ligne 07 10 00
			 * 00 E2 81 fait lever une exception IOException("Not in GZIP
			 * format") car 00 00 provoque le charsRead==-1 ; on croit donc
			 * qu'un nouveau membre suit, mais ce dernier ne commence pas par
			 * GZIP_MAGIC (1F8B) (il commence par E281)
			 */
			byte[] gzipMagic = new byte[4];
			int ret = in.read(gzipMagic, 0, 2);
			int value = 0;
			if (ret == -1) {
				return charsRead;
			} else {
				int j = 0;
				for (int i = 3; i >= 0; i--) {
					int shift = (4 - 1 - j++) * 8;
					value += (gzipMagic[i] & 0x000000FF) << shift;
				}
			}

			if (value == GZIP_MAGIC) {
				((PushbackInputStream) this.in).unread(gzipMagic, 0, 2);
				// Zcat2csv.debug("Reading GZIP_MAGIC");
			} else {
				// On est dans le cas qui pose problème ...
				// on continue la lecture ...
				return 0;
			}

			MultiMemberGZIPInputStream child;
			if (this.size == -1)
				child = new MultiMemberGZIPInputStream(this);
			else
				child = new MultiMemberGZIPInputStream(this, this.size);
			return child.read(inputBuffer, inputBufferOffset, inputBufferLen);
		} else
			return charsRead;
	}

}
