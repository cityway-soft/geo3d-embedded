package org.avm.elementary.geofencing.impl;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.geofencing.Balise;
import org.avm.elementary.geofencing.GeoFencing;
import org.avm.elementary.geofencing.bundle.Activator;
import org.avm.elementary.geofencing.bundle.ConfigImpl;
import org.osgi.util.position.Position;

import spatialindex.rtree.RTree;
import spatialindex.spatialindex.ISpatialIndex;
import spatialindex.spatialindex.Point;
import spatialindex.storagemanager.DiskStorageManager;
import spatialindex.storagemanager.IStorageManager;
import spatialindex.storagemanager.PropertySet;
import spatialindex.storagemanager.RandomEvictionsBuffer;

public class GeoFencingImpl implements GeoFencing, ConfigurableService,
		ManageableService, ProducerService, ConsumerService {
	private Logger _log;
	private ConfigImpl _config;
	private ProducerManager _producer;
	private ISpatialIndex _tree;
	private IStorageManager _storage;
	private DiskStorageManager _disk;
	private HashSet _zone = new HashSet();
	private boolean _initialized;

	public GeoFencingImpl() {
		_log = Activator.getDefault().getLogger();
	}

	public void start() {
		initialize();
	}

	public void stop() {
		_initialized = false;
		if (_disk != null) {
			_disk.close();
		}
		_storage = null;
		_tree = null;
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void configure(Config config) {
		_config = (ConfigImpl) config;
	}

	public void notify(Object o) {
		if (!_initialized)
			return;
		if (o instanceof Position) {
			Position p = (Position) o;
			double lon = p.getLongitude().getValue();
			double lat = p.getLatitude().getValue();
			if (lon != 0d || lat != 0d) {
				localize(p);
			}
		}
	}

	public Set getZone() {
		Set result;
		synchronized (_zone) {
			result = (Set) _zone.clone();
		}
		return result;
	}

	private IStorageManager getStorageManager() throws Exception {
		PropertySet properties = new PropertySet();
		Object[] arguments = { System.getProperty("org.avm.home") };
		String text = MessageFormat.format(_config.getFileName(), arguments);
		properties.setProperty("FileName", text);
		_disk = new DiskStorageManager(properties);
		_storage = new RandomEvictionsBuffer(_disk, _config.getBufferCapacity()
				.intValue(), false);
		return _storage;
	}

	private ISpatialIndex getSpatialIndex() throws Exception {
		RTree tree = null;
		PropertySet ps = new PropertySet();
		ps.setProperty("IndexIdentifier", new Integer(1));
		_storage = getStorageManager();
		tree = new RTree(ps, _storage);
		return tree;
	}

	private void initialize() {
		try {
			_tree = getSpatialIndex();
			_initialized = true;
		} catch (Exception e) {
			_initialized = false;
			_log.error(e.getMessage(), e);
		}
	}

	public void publish(Object obj) {
		_producer.publish(obj);
	}

	protected void localize(Position position) {

		final int LON = 0;
		final int LAT = 1;

		HashSet zone = null;
		synchronized (_zone) {
			zone = (HashSet) _zone.clone();
		}
		double lon = position.getLongitude().getValue();
		double lat = position.getLatitude().getValue();
		double[] value = new double[2];
		value[LON] = lon;
		value[LAT] = lat;
		try {
			Point p = new Point(value);
			GeoFencingVisitor v = new GeoFencingVisitor();
			_tree.pointLocationQuery(p, v);
			_log.debug("[DSU] Tree :  index " + v._indexIO + " leaf = "
					+ v._leafIO);
			for (Iterator iter = zone.iterator(); iter.hasNext();) {
				Balise b = (Balise) iter.next();
				if (!v._zone.contains(b)) {
					// _log.debug("[DSU] previous zone " + zone.toString());
					// _log.debug("[DSU] current zone " +
					// v._zone.toString());
					b.setInside(false);
					_log.debug(b);
					_producer.publish(b);
				}
			}
			for (Iterator iter = v._zone.iterator(); iter.hasNext();) {
				Balise b = (Balise) iter.next();
				if (!zone.contains(b)) {
					// _log.debug("[DSU] previous zone " + zone.toString());
					// _log.debug("[DSU] current zone " +
					// v._zone.toString());
					b.setInside(true);
					_log.debug(b);
					_producer.publish(b);
				}
			}
			synchronized (_zone) {
				_zone.clear();
				_zone.addAll(v._zone);
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

	}
}
