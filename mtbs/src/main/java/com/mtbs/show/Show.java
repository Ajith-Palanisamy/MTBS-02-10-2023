package com.mtbs.show;

import java.time.LocalDate;
import java.time.LocalTime;

import com.mtbs.screen.Screen;
import com.mtbs.theater.Theater;

public class Show 
{
	private int show_id;
	private String movie_name;
	private int movie_id;
	private LocalDate show_date;
	private LocalTime start_time;
	private LocalTime end_time;
	private String language;
	private Theater theater;
	private Screen screen;
	
	
	//for ticket booking
	private int vip_prize;
	private int premium_prize;
	private int normal_prize;
	
	public int getVip_prize() {
		return vip_prize;
	}


	public void setVip_prize(int vip_prize) {
		this.vip_prize = vip_prize;
	}


	public int getPremium_prize() {
		return premium_prize;
	}


	public void setPremium_prize(int premium_prize) {
		this.premium_prize = premium_prize;
	}


	public int getNormal_prize() {
		return normal_prize;
	}


	public void setNormal_prize(int normal_prize) {
		this.normal_prize = normal_prize;
	}


	public Screen getScreen() {
		return screen;
	}


	public void setScreen(Screen screen) {
		this.screen = screen;
	}


	public int getMovie_id() {
		return movie_id;
	}


	public void setMovie_id(int movie_id) {
		this.movie_id = movie_id;
	}


	public Theater getTheater() {
		return theater;
	}


	public void setTheater(Theater theater) {
		this.theater = theater;
	}

	
	
	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public int getShow_id() {
		return show_id;
	}
	
	
	public void setShow_id(int show_id) {
		this.show_id = show_id;
	}
	public String getMovie_name() {
		return movie_name;
	}
	public void setMovie_name(String movie_name) {
		this.movie_name = movie_name;
	}
	public LocalDate getShow_date() {
		return show_date;
	}
	public void setShow_date(LocalDate show_date) {
		this.show_date = show_date;
	}
	public LocalTime getStart_time() {
		return start_time;
	}
	public void setStart_time(LocalTime start_time) {
		this.start_time = start_time;
	}
	public LocalTime getEnd_time() {
		return end_time;
	}
	public void setEnd_time(LocalTime end_time) {
		this.end_time = end_time;
	}
	public Show(int show_id, String movie_name, LocalDate show_date, LocalTime start_time, LocalTime end_time,String language) {
		super();
		this.show_id = show_id;
		this.movie_name = movie_name;
		this.show_date = show_date;
		this.start_time = start_time;
		this.end_time = end_time;
		this.language=language;
	}
	
	public Show(int show_id,int movie_id,String movie_name, LocalDate show_date, LocalTime start_time, LocalTime end_time,String language,Theater theater,Screen sc) {
		super();
		this.show_id = show_id;
		this.movie_id = movie_id;
		this.movie_name = movie_name;
		this.show_date = show_date;
		this.start_time = start_time;
		this.end_time = end_time;
		this.language=language;
		this.theater=theater;
		this.screen=sc;
	}
	
	public Show(int show_id,int movie_id,int vip_prize,int premium_prize,int normal_prize)
	{
		this.show_id = show_id;
		this.movie_id = movie_id;
		this.vip_prize=vip_prize;
		this.premium_prize=premium_prize;
		this.normal_prize=normal_prize;
	}
	
	public Show() {}

}
