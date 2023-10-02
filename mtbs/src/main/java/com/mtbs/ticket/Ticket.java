package com.mtbs.ticket;

public class Ticket 
{
	private int ticket_id;
	private int booking_id;
	private String seat_number;
	private String seat_type;
	private String seat_prize;
	
	
	
	public int getTicket_id() 
	{
		return ticket_id;
	}
	public void setTicket_id(int ticket_id) {
		this.ticket_id = ticket_id;
	}
	public int getBooking_id() {
		return booking_id;
	}
	public void setBooking_id(int booking_id) {
		this.booking_id = booking_id;
	}
	public String getSeat_number() {
		return seat_number;
	}
	public void setSeat_number(String seat_number) {
		this.seat_number = seat_number;
	}
	public String getSeat_type() {
		return seat_type;
	}
	public void setSeat_type(String seat_type) {
		this.seat_type = seat_type;
	}
	public String getSeat_prize() {
		return seat_prize;
	}
	public void setSeat_prize(String seat_prize) {
		this.seat_prize = seat_prize;
	}
	public Ticket(int ticket_id, int booking_id, String seat_number, String seat_type, String seat_prize) {
		super();
		this.ticket_id = ticket_id;
		this.booking_id = booking_id;
		this.seat_number = seat_number;
		this.seat_type = seat_type;
		this.seat_prize = seat_prize;
	}
	public Ticket() {
		super();
	}
	
	
}
