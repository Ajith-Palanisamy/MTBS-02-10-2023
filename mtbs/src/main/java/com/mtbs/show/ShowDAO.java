package com.mtbs.show;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mtbs.db.DBHandler;

public class ShowDAO 
{
	DBHandler obj=DBHandler.getInstance();
    Connection con = obj.getConnection();
    PreparedStatement ps;
    Statement st;
    ResultSet rs;
    
    
    public List<String> addShow(int theater_id,int screen_id,JSONObject jsonData)
    {
    	int movie_id=((Long) jsonData.get("movie_id")).intValue();
    	String language=(String)jsonData.get("language");
    	String start_time=(String)jsonData.get("start-time");
    	String start_date=(String)jsonData.get("start-date");
    	String end_date=(String)jsonData.get("end-date");
    	String show_duration=(String)jsonData.get("show-duration");
    	JSONArray arr=(JSONArray)jsonData.get("slot");
    	//System.out.println(no_of_shows);
    	int vip_prize =Integer.parseInt((String)jsonData.get("vip-prize"));
    	int premium_prize=Integer.parseInt((String)jsonData.get("premium-prize"));
    	int normal_prize=Integer.parseInt((String)jsonData.get("normal-prize"));
    	
    	int vip_cancel =Integer.parseInt((String)jsonData.get("vip-cancel"));
    	int premium_cancel=Integer.parseInt((String)jsonData.get("premium-cancel"));
    	int normal_cancel=Integer.parseInt((String)jsonData.get("normal-cancel"));
    	
    	
    	int movie_language_mapping_id=0;
    	List<String> message=new LinkedList<String>();
    	try
    	{
    		ps=con.prepareStatement("select movie_language_mapping_id from movie_language_mapping inner join language on movie_language_mapping.language_id=language.language_id where movie_id=? and language=?");
    		ps.setInt(1, movie_id);
    		ps.setString(2,language);
    		rs=ps.executeQuery();
    		while(rs.next())
    		{
    			movie_language_mapping_id=rs.getInt(1);
    			break;
    		}
    	}
    	catch (SQLException ex) 
 	     { 
          System.out.println("Exception in getting movie_lang id"+ex);
        }
    	try{
            
            LocalDate startDate=LocalDate.parse(start_date);
            LocalDate endDate=LocalDate.parse(end_date);
            int days = (int)ChronoUnit.DAYS.between(startDate, endDate)+1;
            LocalTime duration=LocalTime.parse(show_duration);
            
            
            ps=con.prepareStatement("insert into show(show_date,start_time,end_time,screen_id,movie_language_mapping_id,vip_prize,premium_prize,normal_prize,status,vip_cancel,premium_cancel,normal_cancel) values(?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(4,screen_id);
            ps.setInt(5,movie_language_mapping_id);
            ps.setInt(6,vip_prize);
            ps.setInt(7,premium_prize);
            ps.setInt(8,normal_prize);
            ps.setString(9,"Booking opened");
            ps.setInt(10,vip_cancel);
            ps.setInt(11,premium_cancel);
            ps.setInt(12,normal_cancel);
            
            for(int i=0;i<arr.size();i++)//Iterating through all slots arr
            {
            	String[] slot=((String)arr.get(i)).split("-");
            	LocalTime startTime=LocalTime.parse(slot[0]);
            	LocalTime endTime=LocalTime.parse(slot[1]);	
            	ps.setTime(2, Time.valueOf(startTime));
    			ps.setTime(3,Time.valueOf(endTime));
    			
    			
    			 //get unavailable show dates for the particular time slot
    			 PreparedStatement  ps1 = con.prepareStatement("select show.show_date from show "
            			  		  + "where screen_id=? AND (show_date BETWEEN ? AND ?) and show.status='booking opened' and "
            			       	  + " (((show.start_time BETWEEN ? AND ?) OR (end_time BETWEEN ? AND ?)) or "
            			     	  + " (? BETWEEN show.start_time AND end_time) ) group by show.show_date");
            	ps1.setInt(1,screen_id);
            	ps1.setDate(2,Date.valueOf(startDate));
            	ps1.setDate(3,Date.valueOf(endDate));
            	ps1.setTime(4,Time.valueOf(startTime));
            	ps1.setTime(5,Time.valueOf(endTime));
            	ps1.setTime(6,Time.valueOf(startTime));
            	ps1.setTime(7,Time.valueOf(endTime));
            	ps1.setTime(8,Time.valueOf(startTime));
            	rs=ps1.executeQuery();
            	List<String> al=new ArrayList<String>();
            	while(rs.next())
            	{
            		String t=rs.getDate(1).toString();
            		message.add("Date : "+t+", Slot : "+startTime+"-"+endTime+".");
                    al.add(t);
            	}
            	LocalDate showDate=startDate;
            	
            	//after getting list of unavailable dates iterate through all the dates,if date not present in array add show for the day
        		for(int j=days;j>0;j--)
        		{
        			if(!al.contains(showDate.toString()))
        			{
	        			ps.setDate(1,Date.valueOf(showDate));
	        			ps.addBatch();
        			}
        			
        			showDate=showDate.plusDays(1);
        		}
        		startTime=endTime.plusMinutes(10);
        		
        	}
            
            ps.executeBatch();
           
          } catch (SQLException ex) 
   	      { 
        
            System.out.println("Exception in adding show "+ex);
        
          }
          return message;
  
    }
    
    public JSONArray getAllShows(int theater_id,int screen_id)
    {
    	JSONArray arr=new JSONArray();
    	Show s;
    	try
    	{
    		ps=con.prepareStatement("select show.*,language,name from show inner join movie_language_mapping on "
    				+ "movie_language_mapping.movie_language_mapping_id=show.movie_language_mapping_id and show.screen_id=? "
    				+ "inner join movie on movie.movie_id = movie_language_mapping.movie_id "
    				+ "inner join language on movie_language_mapping.language_id=language.language_id "
    				//+ "where show_date > CURRENT_DATE OR (show_date = CURRENT_DATE and end_time >= CURRENT_TIME) "
    				+ "order by show_date ,start_time");
    		ps.setInt(1, screen_id);
    		rs=ps.executeQuery();
    		while(rs.next())
    		{
    			JSONObject show= new JSONObject();
    			show.put("show_id",rs.getInt("show_id"));
    			show.put("show_date",rs.getDate("show_date").toString());
    			show.put("start_time",rs.getTime("start_time").toString());
    			show.put("end_time",rs.getTime("end_time").toString());
    			show.put("movie_name", rs.getString("name"));
    			show.put("language", rs.getString("language"));
    			show.put("vip_prize",rs.getInt("vip_prize"));
    			show.put("premium_prize",rs.getInt("premium_prize"));
    			show.put("normal_prize",rs.getInt("normal_prize"));
    			show.put("vip_cancel",rs.getInt("vip_cancel"));
    			show.put("premium_cancel",rs.getInt("premium_cancel"));
    			show.put("normal_cancel",rs.getInt("normal_cancel"));
    			show.put("status",rs.getString("status"));
    			
    			arr.add(show);
    		}
    		
    	}
    	catch (SQLException ex) 
 	    { 
          
          System.out.println("Exception in retriving shows "+ex);
        }
    	return arr;
    }
    
    public String updateShow(int show_id,JSONObject jsonData)
    {
    	int vip_prize =Integer.parseInt((String)jsonData.get("vip_prize"));
    	int premium_prize=Integer.parseInt((String)jsonData.get("premium_prize"));
    	int normal_prize=Integer.parseInt((String)jsonData.get("normal_prize"));
    	
    	int vip_cancel =Integer.parseInt((String)jsonData.get("vip_cancel"));
    	int premium_cancel=Integer.parseInt((String)jsonData.get("premium_cancel"));
    	int normal_cancel=Integer.parseInt((String)jsonData.get("normal_cancel"));
    	
    	try
    	{
    		 ps=con.prepareStatement("update show set vip_prize=?,premium_prize=?,normal_prize=?,vip_cancel=?,premium_cancel=?,normal_cancel=? where show_id=?");
    		 ps.setInt(1,vip_prize);
             ps.setInt(2,premium_prize);
             ps.setInt(3,normal_prize);
    		 
    		 ps.setInt(4,vip_cancel);
             ps.setInt(5,premium_cancel);
             ps.setInt(6,normal_cancel);
             
             ps.setInt(7,show_id);
    		 ps.executeUpdate();
    		
    	}
    	catch (SQLException ex) 
 	    { 
          System.out.println("Exception in deleting show "+ex);
        }
    	return "Ticket Prize Updated..";
    }
    
    
    public String deleteShow(int show_id)
    {
    	try
    	{
    		
    		ps=con.prepareStatement("update booking set status='Show cancelled' where show_id=? and booking.status='Booked'");
    		ps.setInt(1,show_id);
    		ps.executeUpdate();
    		
    		ps=con.prepareStatement("update show set status='Cancelled' where show_id=?");
    		ps.setInt(1,show_id);
    		ps.executeUpdate();
    		
    		ps=con.prepareStatement("select max(user_id),booking.booking_id,sum(seat_prize) as prize from booking inner join "
    				+ " ticket on ticket.booking_id = booking.booking_id and show_id=? and booking.status='Show cancelled' group by booking.booking_id;");
    		ps.setInt(1,show_id);
    		rs=ps.executeQuery();
    		int total=0;
    		while(rs.next())
    		{
    			int user_id=rs.getInt(1);
    			int booking_id=rs.getInt(2);
    			System.out.println("booking ID : "+booking_id);
    			int amt=rs.getInt(3);
    			total=total+amt;
    			
    			PreparedStatement ps1=con.prepareStatement("update ticket set refund=seat_prize where booking_id=?");
    			ps1.setInt(1,booking_id);
    			ps1.executeUpdate();
    			
    			ps1=con.prepareStatement("update users set wallet=wallet+? where user_id=?");
    			ps1.setInt(1,amt);
    			ps1.setInt(2, user_id);
    			ps1.executeUpdate();
    			
    		}
    		
    		ps=con.prepareStatement("update theater set wallet=wallet-? where theater_id=(select theater.theater_id from theater inner join screen on theater.theater_id=screen.theater_id inner join show on show.screen_id=screen.screen_id and show_id=?)");
    		ps.setInt(1, total);
    		ps.setInt(2,show_id);
    		ps.executeUpdate();
    	}
    	catch (SQLException ex) 
 	    { 
          System.out.println("Exception in deleting show "+ex);
        }
    	return "Show deleted";
    }
    
    public String bookTicket(int show_id,JSONObject jsonData)
    {
    	int booking_id=0;
    	JSONArray arr=(JSONArray) jsonData.get("selectedSeats");
    	int n=arr.size();
    	int offer_id=-1,discount=0;
    	String offer_name="";
    	//check for any offer applicable
    	try
    	{
    		ps=con.prepareStatement("select discount,offer_id,offer_name from offer where start_DATE <= CURRENT_DATE and end_date >=CURRENT_DATE  and no_of_tickets <= ? and \"isAvailable\"=1 order by no_of_tickets,discount desc,start_date,end_date ");
    		ps.setInt(1,n);
    		rs=ps.executeQuery();
    		while(rs.next())
    		{
    			int t=rs.getInt(1);
    			if(t>discount)
    			{
    				discount=t;
    				offer_id=rs.getInt(2);
    				offer_name=rs.getString(3);
    			}
    			
    		}
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in finding offer "+e);
    		return "Exception in finding offer ";
    	}
    	
    	try
    	{
    		ps=con.prepareStatement("insert into booking(show_id,user_id,status,offer_id) values(?,?,?,?)");
    		ps.setInt(1,show_id);
    		ps.setInt(2, Integer.parseInt((String)jsonData.get("user_id")));
    		ps.setString(3,"Booked");
    		//ps.setInt(4,Integer.parseInt((String)jsonData.get("amount")));
    		if(offer_id!=-1)
    		{
    			ps.setInt(4,offer_id);
    		}
    		else
    		{
    			ps.setNull(4,Types.BIGINT);
    		}
    		ps.executeUpdate();
    		ps=con.prepareStatement("select booking_id from booking order by booked_time desc limit 1");
    		rs=ps.executeQuery();
    		while(rs.next())
    		{
    			booking_id=rs.getInt(1);
    			break;
    		}
    		System.out.println("Booking_id "+booking_id);
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in booking tickets "+e);
    		return "Exception in adding bookings table";
    	}
    	
    	try
    	{
    	ps=con.prepareStatement("insert into ticket(booking_id,seat_number,seat_type,seat_prize,discount) values(?,?,?,?,?)");
    	ps.setInt(1, booking_id);
    	
    	int total=0;
    	for(int i=0;i<arr.size();i++)
    	{
    		JSONObject t=(JSONObject) arr.get(i);
    		ps.setString(2,(String)t.get("number"));
    		ps.setString(3,(String)t.get("type"));
    		
    		int prize=((Long)t.get("prize")).intValue();
    		if(offer_id!=-1)
    		{
    			prize=prize-(int)(Math.round(prize*0.01*discount));
    		}
    		ps.setInt(4,prize);
    		total=total+prize;
    		ps.setInt(5,discount);
    		ps.addBatch();
    	}
    	 ps.executeBatch();
    	 
    	 ps=con.prepareStatement("update users set wallet= CASE WHEN (wallet - ?) < 0 THEN 0 ELSE wallet - ? END where user_id=?");
    	 ps.setInt(1, total);
    	 ps.setInt(2, total);
    	 ps.setInt(3, Integer.parseInt((String)jsonData.get("user_id")));
    	 ps.executeUpdate();
    	 
    	 ps=con.prepareStatement("update theater set wallet=wallet+? where theater_id=(select theater.theater_id from theater inner join screen on screen.theater_id = theater.theater_id inner join show ON show.screen_id = screen.screen_id and show_id=? )");
    	 ps.setInt(1, total);
    	 ps.setInt(2,show_id);
    	 ps.executeUpdate();
    	 
    	 
    	}
		catch(SQLException e)
		{
			System.out.println("Exception in booking tickets "+e);
			return "Exception in adding tickets table";
		}
    	if(offer_id!=-1)
		{
		  return "Tickets booked!!!.You got "+discount+"% offer by "+offer_name;
		}
    	return "Tickets booked!!!";
    }
    
    public JSONObject getCancellationPercentage(int show_id)
    {
    	JSONObject t=new JSONObject();
    	try
    	{
    		ps=con.prepareStatement("select * from show where show_id=?");
    		ps.setInt(1, show_id);
    		rs=ps.executeQuery();
    		if(rs.next())
    		{
    			t.put("vip_cancel",rs.getInt("vip_cancel"));
    			t.put("premium_cancel",rs.getInt("premium_cancel"));
    			t.put("normal_cancel",rs.getInt("normal_cancel"));
    		}
    	}
    	catch(SQLException e)
		{
			System.out.println("Exception in getting cancellation percentage "+e);
		}
    	return t;
    }
    
    public JSONArray getCollection(int show_id) 
    {
    	JSONArray arr=new JSONArray();
    	try
    	{
    		ps=con.prepareStatement("select booking.booking_id,STRING_AGG(ticket.seat_number,',') AS seats,COUNT(ticket.ticket_id) as count,"
    				+ "SUM(ticket.seat_prize) as prize,"
    				+ "max(booking.status) as status,SUM(ticket.refund) as refund,MAX(users.email) as user "
    				+ "from ticket inner join booking on "
    				+ "booking.booking_id=ticket.booking_id and booking.show_id=? "
    				+ "inner join show show on show.show_id=booking.show_id "
    				+ "inner join users on booking.user_id=users.user_id "
    				+ "group by booking.booking_id order by booking.booked_time "
    				+ "");
    		ps.setInt(1,show_id);
    		rs=ps.executeQuery();
    		while(rs.next())
    		{
    			JSONObject s=new JSONObject();
    			s.put("booking_id",rs.getInt("booking_id"));
    			s.put("seats",rs.getString("seats"));
    			s.put("count",rs.getInt("count"));
    			s.put("prize",rs.getInt("prize"));
    			s.put("status",rs.getString("status"));
    			s.put("refund",rs.getInt("refund"));
    			s.put("user", rs.getString("user"));
    			
    			arr.add(s);
    		}
    	}
    	catch(SQLException e)
		{
			System.out.println("Exception in getting show collection "+e);
		}
    	finally
    	{
    		if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    	
    	return arr;
    }
}
