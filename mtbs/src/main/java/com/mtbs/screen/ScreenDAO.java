package com.mtbs.screen;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mtbs.db.DBHandler;
import com.mtbs.show.ShowDAO;
import com.mtbs.theater.Theater;

public class ScreenDAO
{

DBHandler obj=DBHandler.getInstance();
Connection con = obj.getConnection();
PreparedStatement ps;
Statement st;
ResultSet rs;

private int screen_id;
private int theater_id;
private String screen_name;
private int no_of_seats;
private int normal_row;
private int premium_row;
private int vip_row;
ShowDAO showDao=new ShowDAO();

public List<Screen> getScreens(int theater_id)
{
	List<Screen> list=new LinkedList<Screen>();
	 try {
        ps = con.prepareStatement("select * from screen  where theater_id=? and \"isAvailable\"=1 order by screen_name");
        ps.setInt(1, theater_id);
		ResultSet rs=ps.executeQuery();
        while(rs.next())
        {
        	screen_id=rs.getInt("screen_id");
        	screen_name=rs.getString("screen_name");
        	theater_id=rs.getInt("theater_id");
        	
        	Screen screen=new Screen(screen_id,theater_id,screen_name);
        	list.add(screen); 
        }
        return list;          
    } catch (SQLException ex) {
    	System.out.println(ex);
    	return null;
    }
}

public Screen getScreen(int theater_id,int screen_id)
{
	 Screen screen=null;
	 try {
        ps = con.prepareStatement("select * from screen where theater_id=? and screen_id=?");
        ps.setInt(1,theater_id);
        ps.setInt(2,screen_id);
		ResultSet rs=ps.executeQuery();
        while(rs.next())
	    {	
        	screen_id=rs.getInt("screen_id");
	    	screen_name=rs.getString("screen_name");
	    	no_of_seats=rs.getInt("no_of_seats");
	    	normal_row=rs.getInt("normal_row");
	    	premium_row=rs.getInt("premium_row");
	    	vip_row=rs.getInt("vip_row");
	    	
	    	screen=new Screen(screen_id,theater_id,screen_name);
        	
        }
        return screen;          
    } catch (SQLException ex) {
    	System.out.println(ex);
    	return null;
    }
}

public String updateScreen(int screen_id,JSONObject jsonData)
{
	String screen_name=(String)jsonData.get("screen_name");
	try 
	{
		ps=con.prepareStatement("update screen set screen_name=? where screen_id=? ");
		ps.setString(1, screen_name);
		ps.setInt(2, screen_id);
		ps.executeUpdate();
		return "Screen Details updated!!";
	}
	catch (SQLException ex) 
	{ 
	
	  System.out.println("Error in updating screen : "+ex);
	  return "Error in updating screen : "+ex;
	  
	}
}

public String removeScreen(int screen_id)
{
	try 
	{
		ps=con.prepareStatement("select show.* from show inner join screen on screen.screen_id=show.screen_id "
				+ "and show.screen_id=? and status='Booking opened' where show_date > CURRENT_DATE "
				+ "OR (show_date = CURRENT_DATE and start_time >= CURRENT_TIME)");
		ps.setInt(1,screen_id);
		rs=ps.executeQuery();
		while(rs.next())
		{
			int show_id=rs.getInt("show_id");
			showDao.deleteShow(show_id);
			System.out.println("Sho_id"+show_id);
		}
		
		ps=con.prepareStatement("update screen set \"isAvailable\"=0 where screen_id=? ");
		ps.setInt(1, screen_id);
		ps.executeUpdate();
		return "Screen Removed!!";
	}
	catch (SQLException ex) 
	{ 
	
	  System.out.println("Error in removing screen : "+ex);
	  return "Error in removing screen : "+ex;
	  
	}
}


	public JSONArray getAvailableSlots(int screen_id,JSONObject jsonData)
	{
		JSONArray arr=new JSONArray();
		
    	String start_time=(String)jsonData.get("start_time");
    	String start_date=(String)jsonData.get("start_date");
    	String end_date=(String)jsonData.get("end_date");
    	String show_duration=(String)jsonData.get("show_duration");
    	String max_start_time=(String)jsonData.get("max_start_time");
    	try{
       
            LocalDate startDate=LocalDate.parse(start_date);
            LocalDate endDate=LocalDate.parse(end_date);
            int days = (int)ChronoUnit.DAYS.between(startDate, endDate)+1;
            
            
            
            LocalTime startTime=LocalTime.parse(start_time);
            LocalTime duration=LocalTime.parse(show_duration);
            LocalTime maxStartTime=LocalTime.parse("23:59:00");
            
            boolean isWrongData=false;
            
            while(((startTime.compareTo(maxStartTime))<=0))
        	{
            	System.out.println(startTime);
            	
            	LocalTime endTime=startTime.plusHours(duration.getHour())
                                  .plusMinutes(duration.getMinute())
                                  .plusSeconds(duration.getSecond());
            	
            	ps = con.prepareStatement("select show.show_date from show "
            			+ "where screen_id=? AND (show_date BETWEEN ? AND ?) and show.status='Booking opened' and "
            			+ " (((show.start_time BETWEEN ? AND ?) OR (end_time BETWEEN ? AND ?)) or"
            			+ " (? BETWEEN show.start_time AND end_time) ) group by show.show_date");
            	//checking condition if the new time period is completely lies inside existing show
            	ps.setInt(1,screen_id);
            	ps.setDate(2,Date.valueOf(startDate));
            	ps.setDate(3,Date.valueOf(endDate));
            	ps.setTime(4,Time.valueOf(startTime));
            	ps.setTime(5,Time.valueOf(endTime));
            	ps.setTime(6,Time.valueOf(startTime));
            	ps.setTime(7,Time.valueOf(endTime));
            	ps.setTime(8,Time.valueOf(startTime));
            	
            	rs=ps.executeQuery();
            	int cnt=0;
            	while(rs.next())
            	{
            		cnt++;
            	}
            	if(cnt<days)
            	{
            		
            			JSONObject s=new JSONObject();
            			//while parsing ajax response to json it shows error.because json cannot parse Date..so convert to string
            			s.put("start_time", startTime.toString());
            			s.put("end_time",endTime.toString());
            			arr.add(s);
            		
            	}
                
            	//System.out.println("Start Time : "+startTime+" - End : "+endTime);
            	//System.out.println("is end time is less than start  : "+endTime.compareTo(startTime));
            	
            	
            	if((endTime.compareTo(startTime)) < 0)//if end-time of current show is 00:30:00 then that is the last show for that day
        		break;
            	
            	
        		startTime=endTime.plusMinutes(10);
        		
        		if(startTime.compareTo(endTime)<0) //if end time of previous show is 23:55 then startTime of next show 00:05
        		break;
        	
        	}
          } 
    	  catch (SQLException ex) 
   	      { 
        
            System.out.println("Error : "+ex);
            
          }
    	return arr;
	}
	
	public String addScreen(JSONObject jsonData)
	{
		int theater_id=Integer.parseInt((String)jsonData.get("theater_id"));
		String screen_name=(String)jsonData.get("screen_name");
		try
		{
			ps=con.prepareStatement("insert into screen(screen_name,theater_id) values(?,?)");
			ps.setString(1, screen_name);
			ps.setInt(2, theater_id);
			ps.executeUpdate();
			return "Screen Added!!";
		}
		catch (SQLException ex) 
 	    { 
      
          System.out.println("Error in adding screen : "+ex);
          return "Error in adding screen : "+ex;
          
        }
		
	}
	
	public JSONArray getScreenCollection(int screen_id)
	{
		JSONArray arr=new JSONArray();
		try
		{
			ps=con.prepareStatement("select show.show_id,Max(show.screen_id) AS screen_id,"
					+ "MAX(show.show_date) as show_date,MAX(start_time) as start_time,MAX(show.status) as show_status,"
					+ "MAX(movie.name) as movie_name,max(language) as language,"
					+ "(SUM(ticket.seat_prize) - SUM(ticket.refund))as collection from ticket "
					+ "inner join booking on booking.booking_id=ticket.booking_id "
					+ "inner join show on show.show_id=booking.show_id and show.screen_id=? "
					+ "inner join movie_language_mapping on show.movie_language_mapping_id=movie_language_mapping.movie_language_mapping_id "
					+ "inner join movie on movie.movie_id = movie_language_mapping.movie_id "
					+ "inner join language on movie_language_mapping.language_id=language.language_id "
					+ "group by show.show_id order by show_date,start_time");
			ps.setInt(1,screen_id);
			rs=ps.executeQuery();
			while(rs.next())
			{
				JSONObject s=new JSONObject();
				s.put("screen_id",rs.getInt("screen_id"));
				s.put("show_id",rs.getInt("show_id"));
				s.put("show_date",rs.getDate("show_date").toString());
				s.put("start_time",rs.getTime("start_time").toString());
				s.put("movie_name",rs.getString("movie_name"));
				s.put("show_status", rs.getString("show_status"));
				s.put("language",rs.getString("language"));
				s.put("collection",rs.getInt("collection"));
				
				arr.add(s);
				
			}
		}
		catch (SQLException ex) 
 	    { 
      
          System.out.println("Error in retriving collection for screen : "+ex);
          
        }
		return arr;
	}

}
