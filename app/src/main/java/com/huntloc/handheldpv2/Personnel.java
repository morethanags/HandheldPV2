package com.huntloc.handheldpv2;

import java.util.Date;

import android.util.Log;

public class Personnel {
	private String internalCode;
	private String printedCode;
	private String portrait;
	private String name;
	private String PBIP;
	private String hazardousGoods;
	private String portSecurity;
	
	private String PBIPColor;
	private String PBIPCode;
	
	public String getPBIPColor() {
		return PBIPColor;
	}
	public void setPBIPColor(String pBIPColor) {
		PBIPColor = pBIPColor;
	}
	public int getPBIPColorCode(){
		int toReturn = android.R.color.transparent;
		if(getPBIPColor()!=null){
			if(getPBIPColor().equalsIgnoreCase("ROJO")){
				toReturn = R.color.red;
			}
			else if(getPBIPColor().equalsIgnoreCase("NARANJA")){
				toReturn = R.color.amber;
				}
			else if(getPBIPColor().equalsIgnoreCase("VERDE")){
				toReturn = R.color.green;
			}
			else if(getPBIPColor().equalsIgnoreCase("BLANCO")){
				toReturn = R.color.white;
			}
		}
		return toReturn;
		
	}
	public String getPBIPCode() {
		return PBIPCode;
	}
	public void setPBIPCode(String pBIPCode) {
		PBIPCode = pBIPCode;
	}
	private String clearance;
	
	public Personnel(String internalCode, String printedCode, String portrait, String name, 
			String PBIP, String hazardousGoods, String portSecurity, String PBIPColorName, String PBIPCode,
			String clearance) {
		this.internalCode = internalCode;
		this.printedCode = printedCode;
		this.portrait = portrait;
		this.name = name;
		this.PBIP = PBIP;
		this.hazardousGoods = hazardousGoods;
		this.portSecurity = portSecurity;
		this.setPBIPColor(PBIPColorName);
		this.PBIPCode = PBIPCode;
		this.clearance = clearance;
		//Log.d("New Personnel", this.toString());
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInternalCode() {
		return internalCode;
	}
	public void setInternalCode(String internalCode) {
		this.internalCode = internalCode;
	}
	public String getPrintedCode() {
		return printedCode;
	}
	public void setPrintedCode(String printedCode) {
		this.printedCode = printedCode;
	}
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	
	public String getPBIP() {
		return PBIP;
	}
	public void setPBIP(String pBIP) {
		PBIP = pBIP;
	}
	public String getHazardousGoods() {
		return hazardousGoods;
	}
	public void setHazardousGoods(String hazardousGoods) {
		this.hazardousGoods = hazardousGoods;
	}
	public String getPortSecurity() {
		return portSecurity;
	}
	public void setPortSecurity(String portSecurity) {
		this.portSecurity = portSecurity;
	}
	public String getClearance() {
		return clearance;
	}
	public void setClearance(String clearance) {
		this.clearance = clearance;
	}

	public String toString() {
		return "Personnel [internalCode=" + internalCode + ", printedCode="
				+ printedCode + ", name=" + name + ", PBIP=" + PBIP
				+ ", hazardousGoods=" + hazardousGoods + ", portSecurity="
				+ portSecurity + ", PBIPColor=" + PBIPColor + ", PBIPCode="
				+ PBIPCode + ", clearance=" + clearance + "]";
	}
}
