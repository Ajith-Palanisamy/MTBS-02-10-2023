package com.mtbs.theater;

import jakarta.xml.bind.annotation.XmlRootElement;


public class Theater 
{
	private int theater_id;
	private String name;
	private String door_no;
	private String street;
	private String city;
	private String district;
	private String state;
	private int manager_id;
	private String pin_code;
	
	public Theater()
	{
		
	}
	
	public Theater(int theater_id, String name, String door_no, String street, String city, String district,
		String state, String pin_code,int manager_id) {
		this.theater_id = theater_id;
		this.name = name;
		this.door_no = door_no;
		this.street = street;
		this.city = city;
		this.district = district;
		this.state = state;
		this.manager_id = manager_id;
		this.pin_code = pin_code;
	}
	
	public Theater(int theater_id,String name,String district)
	{
		this.theater_id = theater_id;
		this.name = name;
		this.district = district;
	}
	
	public int getTheater_id() {
		return theater_id;
	}
	public void setTheater_id(int theater_id) {
		this.theater_id = theater_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDoor_no() {
		return door_no;
	}
	public void setDoor_no(String door_no) {
		this.door_no = door_no;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getManager_id() {
		return manager_id;
	}
	public void setManager_id(int manager_id) {
		this.manager_id = manager_id;
	}
	public String getPin_code() {
		return pin_code;
	}
	public void setPin_code(String pin_code) {
		this.pin_code = pin_code;
	}
	
	
}
