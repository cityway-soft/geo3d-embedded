package org.avm.hmi.swt.phony;

import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;

public class ListContactView extends ContactView  {

	private ContactModel _model;

	private Phony _phony;

	private List _contactList;
	
	private Logger _log = Logger.getInstance(this.getClass());

	private CallSelectionListener _callSelectionListener;

	public ListContactView(Composite composite, int arg1) {
		super(composite, arg1);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;

		_contactList = new List(this, SWT.NONE | SWT.SINGLE
				| SWT.V_SCROLL);
		_contactList.addSelectionListener(new ContactListSelectionListener());
		_contactList.setLayoutData(gridData);
		
		_callSelectionListener = new CallSelectionListener();

	}
	
	public void setFont(Font font) {
		if (font != null){
			super.setFont(font);
			_contactList.setFont(font);
			layout();
		}
	}

	public void ringing(final String phonenumber) {
		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				try {
					Enumeration e = _model.elements();
					boolean found=false;
					int idx=0;
					while (e.hasMoreElements()&&!found) {
						String contact = (String) e.nextElement();
						String phone = _model.getPhoneNumber(contact);
						if (phone.equals(phonenumber)){
							found=true;
						}
						idx++;
					}
					_contactList.setSelection(idx);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void hangup() {
		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				try {


				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void activate(String phonenumber) {
		if (_model != null) {
			Enumeration e = _model.elements();
			if (e != null) {
				String name = null;

				boolean found = false;
				while (e.hasMoreElements()) {
					name = (String) e.nextElement();
					String phone = _model.getPhoneNumber(name);
					if (phone.equals(phonenumber)) {
						found = true;
						break;
					}
				}

				if (!found)
					return;
			}
		}
	}

	public void update(ContactModel contacts) {
		_model = contacts;
		Display.getCurrent().syncExec(new Runnable() {
			public void run() {
				if (_model != null) {
					Enumeration e;
					_contactList.removeAll();
					e = _model.elements();
					if (e != null) {
						while (e.hasMoreElements()) {
							String name = (String) e.nextElement();
							addCallButton(name);
						}
					}
					layout();
				}
			}
		});
	}

	private void addCallButton(final String contactName) {
		_contactList.add(contactName);
		layout();
	}

	public void online() {
		Display.getCurrent().syncExec(new Runnable() {
			public void run() {

			}
		});
	}

	

	public void setPhony(Phony phony) {
		_phony = phony;
	}

	public void dialing() {
		Display.getCurrent().syncExec(new Runnable() {
			public void run() {

			}
		});
	}



	public class ContactListSelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent arg0) {
			int idx = _contactList.getSelectionIndex();
			Enumeration e = _model.elements();
			int i=0;
			String found = null;
			String contact= null;
			if (e != null) {
				while (e.hasMoreElements() && (found == null)) {	
					contact = (String) e.nextElement();
					if (idx == i){
						found = _model.getPhoneNumber(contact);
					}
					i++;
				}
			}

			if (found != null){
				_callSelectionListener.setContact(contact);				
				_selector.setMode(CallAnswerSelector.CALL_MODE,
						_callSelectionListener);
			}
		}
	}
	
	
	public class CallSelectionListener extends SelectionAdapter {
		String contact=null;
		
		public void setContact(String name){
			contact = name;
		}
		public void widgetSelected(SelectionEvent event) {
			try {
				_phony.call(contact);
			} catch (Exception e) {
				_log.error(e);
			}
		}
	}
	
}
