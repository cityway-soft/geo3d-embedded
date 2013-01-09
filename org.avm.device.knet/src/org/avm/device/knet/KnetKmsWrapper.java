package org.avm.device.knet;

import org.avm.device.knet.model.KmsMarshaller;

public class KnetKmsWrapper {

	private boolean _reply;

	private KmsMarshaller _request;

	private KmsMarshaller _response;

	public KnetKmsWrapper(KmsMarshaller request) {
		this(request, false);
	}

	public KnetKmsWrapper(KmsMarshaller request, boolean reply) {
		super();
		_request = request;
		_reply = reply;
	}

	public boolean isReply() {
		return _reply;
	}

	public KmsMarshaller getRequest() {
		return _request;
	}

	public KmsMarshaller getResponse() {
		return _response;
	}

	public void setResponse(KmsMarshaller response) {
		_response = response;
	}

}
