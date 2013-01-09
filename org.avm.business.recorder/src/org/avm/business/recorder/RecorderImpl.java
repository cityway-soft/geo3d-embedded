package org.avm.business.recorder;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.recorder.action.DoorAction;
import org.avm.business.recorder.action.MotionAction;
import org.avm.business.recorder.action.PositionAction;
import org.avm.business.recorder.action.SpeedAction;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.database.Database;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.avm.elementary.variable.Variable;

import EDU.oswego.cs.dl.util.concurrent.DirectExecutor;

public class RecorderImpl implements Recorder, ManageableService,
		ConsumerService, AvmInjector, JDBInjector, ProducerService {

	private Logger _log = Logger.getInstance(this.getClass());

	private DirectExecutor _pool;

	private JDB _jdb;

	public RecorderImpl() {
		// _log.setPriority(Priority.DEBUG);
		new MotionAction();
		new SpeedAction();
		new DoorAction();
		new PositionAction();
		// new CO2Action();
	}

	public void start() {
		_pool = new DirectExecutor();
	}

	public void stop() {
		_pool = null;
	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
		ActionFactory.inject(jdb);
	}

	public void setAvm(Avm avm) {
		ActionFactory.inject(avm);
	}

	public void setVariable(Variable var) {
		ActionFactory.inject(var);
	}

	public void setDatabase(Database database) {
		ActionFactory.inject(database);
	}

	public void unsetAvm(Avm avm) {

	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}

	public void notify(Object o) {
		try {
			if (_pool != null) {
				_pool.execute(new RecorderCallable(o));
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	class RecorderCallable implements Runnable {
		private Object _obj;

		public RecorderCallable(Object o) {
			_obj = o;
		}

		public void run() {
			try {
				ActionFactory.compute(_obj);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public void setProducer(ProducerManager producer) {
		ActionFactory.inject(producer);
	}

	public void syncJdbLight() {
		_jdb.sync();
		String filename = _jdb.getScheduledFilename(new Date());
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			DataInputStream zip = new DataInputStream(
					new MultiMemberGZIPInputStream(
							new FileInputStream(filename)));
			String line;
			while ((line = zip.readLine()) != null) {
				if (line.indexOf("FINSERVICE") != -1) {
					out.write(line.getBytes());
					out.write('\n');
				}
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("------------");
		System.out.print(out.toString());
		System.out.println("------------");

	}

}
