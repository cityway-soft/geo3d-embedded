package org.avm.business.tad.bundle;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.avm.business.tad.Mission;
import org.avm.business.tad.TADConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements TADConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		return result;
	}

	protected String getPid() {
		return Activator.getDefault().getPid();
	}

	public void add(Mission mission) {
		Long id = mission.getId();
		Properties p = new Properties();
		p.put("destination", mission.getDestination());
		p.put("description", mission.getDescription());
		p.put("state", Integer.toString(mission.getState()));
		p.put("type", Integer.toString(mission.getType()));
		p.put("date", Mission.formatDate(mission.getDate()));

		String text = save(p);
		_config.put(id.toString(), text);
	}

	public Mission getMission(Long id) {
		Mission mission = null;
		Properties p = (Properties) _config.get(id.toString());
		if (p != null) {
			mission = createMission(id, p);
		}
		return mission;
	}
	
	private Mission createMission(Long id, Properties p){
		Mission mission = new Mission(id.longValue(),
				Integer.parseInt((String)p.get("type")),
				(String) p.get("destination"), (String) p
						.get("description"), Mission.parseDate( (String) p.get("date") ));
		mission.setState(Integer.parseInt((String)p.get("state")));
		return mission;
		
	}

	public void remove(Long id) {
		_config.remove(id.toString());
	}

	public Enumeration elements() {
		Vector v = new Vector();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))
				continue;

			Properties p = (Properties)load((String)_config.get(key));
			Long id=new Long(Long.parseLong(key));
			Mission mission = createMission(id, p);
			v.add(mission);
		}
		return v.elements();
	}


}
