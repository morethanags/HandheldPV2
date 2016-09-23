package com.huntloc.handheldpv2;

import java.util.UUID;

public class Journal {
	private String guid;
	private String credential;
	private long time;
	private String log;
	private String door;
	private boolean sent;
	private String personnel;
	private String descLog;
	public Journal(String credential, String log, String door, long time, String personnel, String descLog) {
		this.guid = UUID.randomUUID().toString();
		this.credential = credential;
		this.time = time;
		this.log = log;
		this.door = door;
		this.sent = false;
		this.personnel =  personnel;
		this.descLog = descLog;
	}

	public Journal(String guid, String credential, String log, String door, long time, boolean isSent, String personnel, String descLog) {
		this.guid = guid;
		this.credential = credential;
		this.time = time;
		this.log = log;
		this.door = door;
		this.sent = isSent;
		this.personnel =  personnel;
		this.descLog =  descLog;
	}

	public String getDescLog() {
		return descLog;
	}

	public void setDescLog(String descLog) {
		this.descLog = descLog;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public long getTime() {
		return time;
	}

	public String getPersonnel() {
		return personnel;
	}

	public void setPersonel(String personnel) {
		this.personnel = personnel;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getDoor() {
		return door;
	}

	public void setDoor(String door) {
		this.door = door;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public String getGuid() {
		return guid;
	}

	public String toString() {
		return "Journal{" +
				"credential='" + credential + '\'' +
				", guid='" + guid + '\'' +
				", time=" + time +
				", log='" + log + '\'' +
				", door='" + door + '\'' +
				", sent=" + sent +
				", personnel='" + personnel + '\'' +
				", descLog='" + descLog + '\'' +
				'}';
	}
}
