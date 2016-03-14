package com.citywebtechnologies.smsconnect.model;


public class ConnectSMS {

	private long id;
	private long rec;
	private String address;
	private String message;
	private long dateReceived;
	private long dateSent;
	private int sentStatus;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRec() {
		return this.rec;
	}
	public void setRec(long rec) {
		this.rec = rec;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(long dateReceived) {
		this.dateReceived = dateReceived;
	}

	public long getDateSent() {
		return dateSent;
	}

	public void setDateSent(long dateSent) {
		this.dateSent = dateSent;
	}

	public int getSentStatus() {
		return sentStatus;
	}

	public void setSendStatus(int sent) {
		this.sentStatus = sent;
	}

	@Override
	public String toString() {
		return "ConnectSMS{" +
				"id=" + id +
				", address='" + address + '\'' +
				", message='" + message + '\'' +
				", dateReceived=" + dateReceived +
				", dateSent=" + dateSent +
				", sentStatus=" + sentStatus +
				'}';
	}
}
