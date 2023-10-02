package com.mtbs.seatingArrangement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mtbs.db.DBHandler;
import com.mtbs.screen.Screen;
import com.mtbs.show.Show;
import com.mtbs.show.ShowDAO;

public class seatingArrangementDAO 
{
	DBHandler obj=DBHandler.getInstance();
    Connection con = obj.getConnection();
    PreparedStatement ps;
    Statement st;
    ResultSet rs;
    ShowDAO showDao=new ShowDAO();
    
    public JSONObject updateSeatingArrangement(int theater_id,int screen_id,JSONObject jsonObj)
    {
    	
    	JSONArray arr=(JSONArray)jsonObj.get("path");
    	int total_rows=((Long) jsonObj.get("total_rows")).intValue();
    	int total_columns=((Long) jsonObj.get("total_columns")).intValue();
    	int vip=((Long) jsonObj.get("vip")).intValue();
    	int premium=((Long) jsonObj.get("premium")).intValue();
    	int normal=((Long)  jsonObj.get("normal")).intValue();
    	int pathCount=arr.size();
    	int seatCount=(total_rows*total_columns)-pathCount;
    	
    	
    	
    	
    	
    	try {
    		ps = con.prepareStatement("delete from seating_arrangement where screen_id=?");
    		ps.setInt(1,screen_id);
    		ps.executeUpdate();
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in deleting the existing seat arrangement "+e);
    	}
    	
    	char c='A';
    	String type="VIP";
    	try
		{
    		ps=con.prepareStatement("insert into seating_arrangement(screen_id,row_name,column_number,seat_type) values(?,?,?,?)");
	    	for(int i=0;i<total_rows;i++)
	    	{
	    		if(i<vip)
	    		{
	    			type="VIP";
	    		}
	    		else if(i<(vip+premium))
	    			type="Premium";
	    		else
	    			type="Normal";
	    		
	    		for(int j=1;j<=total_columns;j++)
	    		{
	    				ps.setInt(1,screen_id);
	    				ps.setString(2,String.valueOf(c));
	    				ps.setInt(3,j);
	    				String seat=c+""+j;
	    				if(!arr.contains(seat))
	    				{
	    				  ps.setString(4,type);
	    				}
	    				else
	    				{
	    					ps.setString(4,"P");
	    				}
	    				ps.addBatch();
	    		}
	    		c++;
	    	}
	    	ps.executeBatch();
		}
		catch(SQLException e)
    	{
    		System.out.println("Exception in inserting  seat arrangement "+e);
    	}
    	
    	//cancel and refund all the upcoming shows
    	try
    	{
    		ps=con.prepareStatement("select show_id from show where screen_id=? and show.status='Booking opened' and (show_date > CURRENT_DATE OR (show_date = CURRENT_DATE and start_time >= CURRENT_TIME))");
    		ps.setInt(1,screen_id);
    		rs=ps.executeQuery();
    		while(rs.next())
    		{
    			showDao.deleteShow(rs.getInt(1));
    		}
    		
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in cancelling for shows while updating seat arrangement "+e);
    	}
    	  	
    	return getSeatingArrangement(screen_id);
    }
    
    public JSONObject getSeatingArrangement(int screen_id)
    {
    	//List<Seat> list=new LinkedList<Seat>();
    	JSONArray seats = new JSONArray();
        Screen sc=null;
        int columns=0;
    	try
    	{
    		ps = con.prepareStatement("select * from seating_arrangement where screen_id=? order by row_name,column_number");
            ps.setInt(1,screen_id);
			ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
            	JSONObject seat=new JSONObject();
            	seat.put("seating_id",rs.getInt("seating_id"));
            	seat.put("screen_id",rs.getInt("screen_id"));
            	seat.put("row_name", rs.getString("row_name"));
            	seat.put("column_number", rs.getString("column_number"));
            	seat.put("seat_type",rs.getString("seat_type"));
            	seats.add(seat);
            }
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in retriving  seat arrangement "+e);
    	}
    	columns=getColumnCount(screen_id);
        JSONObject result=new JSONObject();
        result.put("seats", seats);
        result.put("columns", columns);
        
        return result;
    }
    
    
    public Screen getScreenDetails(int screen_id,int columns )
    {
    	Screen sc=null;
    	try
    	{
    		ps = con.prepareStatement("select * from screen where screen_id=?");
            ps.setInt(1,screen_id);
			ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
            	sc=new Screen(rs.getInt("screen_id"),rs.getInt("theater_id"),rs.getString("screen_name"));
            	break;
            }
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in retriving screen "+e);
    	}
    	return sc;
    }
    
    public int getColumnCount(int screen_id)
    {
    	int columns=0;
    	try
    	{
    		ps = con.prepareStatement("select count(distinct(column_number)) from seating_arrangement where screen_id=? and row_name='A'");
            ps.setInt(1,screen_id);
			ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
            	columns=rs.getInt(1);
            }
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in retriving screen column count "+e);
    	}
    	return columns;
    }
    
    
    
    
    
    
    public JSONObject getShowSeatingArrangement(int show_id)
    {
    	JSONArray booked = new JSONArray();
        int columns=0;
        int screen_id=0;
        int vip_prize=0,premium_prize=0,normal_prize=0;
    	try
    	{
    		ps = con.prepareStatement("select screen_id,vip_prize,premium_prize,normal_prize from show where show_id=?");
            ps.setInt(1,show_id);
			ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
            	screen_id=rs.getInt("screen_id");
            	vip_prize=rs.getInt("vip_prize");
            	premium_prize=rs.getInt("premium_prize");
            	normal_prize=rs.getInt("normal_prize");
            }
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in retriving  screen_id for the show "+e);
    	}
    	
    	JSONObject result=getSeatingArrangement(screen_id);
    	result.put("vip_prize",vip_prize);
    	result.put("premium_prize",premium_prize);
    	result.put("normal_prize",normal_prize);
    	try
    	{
    		ps = con.prepareStatement("select seat_number from ticket inner join booking on ticket.booking_id=booking.booking_id and booking.show_id=? and booking.status='Booked'");
            ps.setInt(1,show_id);
			ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
            	booked.add(rs.getString(1));
            }
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in retriving  booked seats for the show "+e);
    	}
    	result.put("booked",booked);
    	
    	return result;
    }
    
}
