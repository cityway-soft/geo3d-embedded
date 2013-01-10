/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.avm.device.knet.parser;

/**
 * 
 * Define Constants
 */

interface ParserConstants {

	// Define the StateMachine tags
	static final String SM_NAMESPACE = "http://www.osgi.org/xmlns/sm/v1.0.0";

	static final String KMS_ELEMENT = "kms";
	static final String AUTH_ELEMENT = "auth";
	static final String STOP_ELEMENT = "stop";
	static final String POSITION_ELEMENT = "position";
	static final String AREATRIG_ELEMENT = "areatrig";
	static final String CALLTRIG_ELEMENT = "calltrig";
	static final String SMSTRIG_ELEMENT = "smstrig";
	static final String CALL_ELEMENT = "call";
	static final String SMS_ELEMENT = "sms";
	static final String RSSI_ELEMENT = "rssi";
	static final String OUTPUT_ELEMENT = "output";
	static final String INPUT_ELEMENT = "input";
	static final String INPUTTRIG_ELEMENT = "inputtrig";
	static final String CAN_ELEMENT = "can";
	static final String CANTRIG_ELEMENT = "cantrig";
	static final String ALARM_ELEMENT = "alarm";
	static final String LOG_ELEMENT = "log";
	static final String STATS_ELEMENT = "stats";
	static final String SYSTEM_ELEMENT = "system";
	static final String LIST_ELEMENT = "list";
	static final String DOTA_ELEMENT = "dota";
	static final String BEEP_ELEMENT = "beep";
	static final String POWEROFF_ELEMENT = "poweroff";
	static final String CONNECT_ELEMENT = "connect";
	static final String DISCONNECT_ELEMENT = "disconnect";
	static final String MSG_ELEMENT = "msg";
	static final String MMI_ELEMENT = "mmi";
	static final String NEW_ELEMENT = "new";
	static final String LABEL_ELEMENT = "label";
	static final String UPDATE_ELEMENT = "update";
	static final String RSP_ELEMENT = "rsp";
	static final String TEXT_ELEMENT = "text";

	static final String KNETID_ATTRIBUTE = "knetid";
	static final String FROM_ATTRIBUTE = "from";
	static final String TO_ATTRIBUTE = "to";
	static final String RTO_ATTRIBUTE = "rto";
	static final String RKNETID_ATTRIBUTE = "rknetid";
	static final String REF_ATTRIBUTE = "ref";
	static final String CONF_ATTRIBUTE = "conf";
	static final String CPU_ATTRIBUTE = "cpu";
	static final String FLASH_ATTRIBUTE = "flash";
	static final String MEM_ATTRIBUTE = "mem";
	static final String KNETPOUT_ELEMENT = "knetpOUT";
	static final String XMLERROR_ELEMENT = "xmlerror";
	static final String CONF_ELEMENT = "conf";

	static final String RES_ATTRIBUTE = "res";
	static final String NAME_ATTRIBUTE = "name";
	static final String PERIOD_ATTRIBUTE = "period";
	static final String LOG_ATTRIBUTE = "log";
	static final String DATE_ATTRIBUTE = "date";
	static final String FIX_ATTRIBUTE = "fix";
	static final String LAT_ATTRIBUTE = "lat";
	static final String LONG_ATTRIBUTE = "long";
	static final String ALT_ATTRIBUTE = "alt";
	static final String COURSE_ATTRIBUTE = "course";
	static final String SPEED_ATTRIBUTE = "speed";
	static final String NSAT_ATTRIBUTE = "nsat";
	static final String HDOP_ATTRIBUTE = "hdop";
	static final String ID_ATTRIBUTE = "id";
	static final String WAY_ATTRIBUTE = "way";
	static final String RAD_ATTRIBUTE = "rad";
	static final String IDENT_ATTRIBUTE = "ident";
	static final String MSG_ATTRIBUTE = "msg";
	static final String ACT_ATTRIBUTE = "act";
	static final String STATUS_ATTRIBUTE = "status";
	static final String VALUE_ATTRIBUTE = "value";
	static final String DIGITAL_ATTRIBUTE = "digital";
	static final String ADC_ATTRIBUTE = "adc";
	static final String DAC_ATTRIBUTE = "dac";
	static final String TEMPERATURE_ATTRIBUTE = "temperature";
	static final String ACTION_ATTRIBUTE = "action";
	static final String MENUID_ATTRIBUTE = "menuid";
	static final String TIMEOUT_ATTRIBUTE = "timeout";
	static final String START_ATTRIBUTE = "start";
	static final String STOP_ATTRIBUTE = "stop";
	static final String BEARER_ATTRIBUTE = "bearer";
	static final String VOLTX_ATTRIBUTE = "voltx";
	static final String VOLRX_ATTRIBUTE = "volrx";
	static final String CLASS_ATTRIBUTE = "class";
	static final String REPORT_ATTRIBUTE = "report";
	static final String ADDPOS_ATTRIBUTE = "addpos";

	/* SAX Parser class name */
	// static final String SAX_FACTORY_CLASS =
	// "javax.xml.parsers.SAXParserFactory"; //$NON-NLS-1$
	static final String SAX_FACTORY_CLASS = "org.apache.crimson.jaxp.SAXParserFactoryImpl";
}
