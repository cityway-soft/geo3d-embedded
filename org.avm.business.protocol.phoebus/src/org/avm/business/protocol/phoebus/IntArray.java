/* @flavorc
 *
 * IntArray.java
 * 
 * This file was automatically generated by flavorc
 * from the source file:
 *     'phoebus.fl'
 *
 * For information on flavorc, visit the Flavor Web site at:
 *     http://www.ee.columbia.edu/flavor
 *
 * -- Do not edit by hand --
 *
 */

package org.avm.business.protocol.phoebus;
import java.io.IOException;

import flavor.IBitstream;
import flavor.MapResult;

public class IntArray {
    int _longueur;
    int _value[] = new int[256];
    
	public IntArray() {

	}

	public IntArray(int[] value) {
		setValue(value);
	}

	public int[] getValue() {
		return _value;
	}

	public void setValue(int[] value) {
		_longueur = value.length;
		_value = value;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < _longueur; i++) {
	        if (i > 0) {
	        	result.append(' ');
	        }
	        result.append(_value[i]);
	    }

		return result.toString();
	}
	

    public int get(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        int _F_dim0, _F_dim0_end;
        _longueur = _F_bs.sgetbits(8);
        _F_dim0_end = _longueur;
        for (_F_dim0 = 0; _F_dim0 < _F_dim0_end; _F_dim0++) {
            _value[_F_dim0] = _F_bs.getbits(32);
        }
        return _F_ret;
    }

    public int put(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        int _F_dim0, _F_dim0_end;
        _F_bs.putbits(_longueur, 8);
        _F_dim0_end = _longueur;
        for (_F_dim0 = 0; _F_dim0 < _F_dim0_end; _F_dim0++) {
            _F_bs.putbits(_value[_F_dim0], 32);
        }
        return _F_ret;
    }
}
