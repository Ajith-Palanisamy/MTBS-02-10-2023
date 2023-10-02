package com.mtbs.booking;

import java.util.List;

import com.mtbs.ticket.Ticket;

public class Booking 
{
	private int booking_id;
	private int show_id;
	private int user_id;
	private String status;
	private List<Ticket> tickets;
	public int getBooking_id() {
		return booking_id;
	}
	public void setBooking_id(int booking_id) {
		this.booking_id = booking_id;
	}
	public int getShow_id() {
		return show_id;
	}
	public void setShow_id(int show_id) {
		this.show_id = show_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Ticket> getTickets() {
		return tickets;
	}
	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}
	public Booking(int booking_id, int show_id, int user_id, String status, List<Ticket> tickets) {
		super();
		this.booking_id = booking_id;
		this.show_id = show_id;
		this.user_id = user_id;
		this.status = status;
		this.tickets = tickets;
	}
	public Booking() {
		super();
	}
	
}
