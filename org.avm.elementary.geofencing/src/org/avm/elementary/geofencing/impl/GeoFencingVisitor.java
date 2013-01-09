/**
 * 
 */
package org.avm.elementary.geofencing.impl;

import java.util.HashSet;

import org.avm.elementary.geofencing.Balise;

import spatialindex.spatialindex.IData;
import spatialindex.spatialindex.INode;
import spatialindex.spatialindex.IVisitor;

class GeoFencingVisitor implements IVisitor {
	public HashSet _zone;
	public int _indexIO;
	public int _leafIO;

	public GeoFencingVisitor() {
		_zone = new HashSet();
	}

	public void visitNode(INode n) {
		if (n.isLeaf())
			_leafIO++;
		else
			_indexIO++;
	}

	public void visitData(IData d) {
		Balise b = new Balise(d.getIdentifier(), true, d.getData());
		_zone.add(b);
	}
}