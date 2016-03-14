package com.citywebtechnologies.smsconnect;

public class Sms {
	private String _id;
	private String _RecId;
	private String _address;
	private String _msg;
	private String _readState; // "0" for not read sms and "1" for read sms
	private String _time;
	private String _folderName;

	public String getId() {
		return _id;
	}
	public String getRec() {
		return _RecId;
	}
	public void setRec(String rec) {
		_RecId = rec;
	}

	public String getAddress() {
		return _address;
	}

	public void setMsg(String msg) {
		_msg = msg;
	}

	public String getMsg() {
		return _msg;
	}

	public String getReadState() {
		return _readState;
	}

	public String getTime() {
		return _time;
	}

	public String getFolderName() {
		return _folderName;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setAddress(String address) {
		_address = address;
	}

	public void setReadState(String readState) {
		_readState = readState;
	}

	public void setTime(String time) {
		_time = time;
	}

	public void setFolderName(String folderName) {
		_folderName = folderName;
	}

}
