package com.mtbs.theater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mtbs.db.DBHandler;
import com.mtbs.movie.Movie;
import com.mtbs.show.ShowDAO;

public class TheaterDAO 
{
	DBHandler obj=DBHandler.getInstance();
    Connection con = obj.getConnection();
    PreparedStatement ps;
    Statement st;
    ResultSet rs;
    
    private int theater_id;
	private String name;
	private String door_no;
	private String street;
	private String city;
	private String district;
	private String state;
	private int manager_id;
	private String pin_code;
	
	ShowDAO showDao=new ShowDAO();
	
	public JSONArray getAllTheaters()
    {
		JSONArray list=new JSONArray();
   	 try {
            ps = con.prepareStatement("select theater.*,users.email from theater  inner join users on users.user_id=theater.manager_id where \"isAvailable\"=1  order by name");
			ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
            	theater_id=rs.getInt("theater_id");
            	name=rs.getString("name");
            	manager_id=rs.getInt("manager_id");
            	//System.out.println("DB movie "+name);
//            	door_no=rs.getString("door_no");
//            	street=rs.getString("street");
//            	city=rs.getString("city");
//            	state=rs.getString("state");
//            	pin_code=rs.getString("pin_code");
            	district=rs.getString("district");
            	String manager_email=rs.getString("email");
            	
            	JSONObject t=new JSONObject();
            	t.put("theater_id",theater_id);
            	t.put("theater_name",name);
            	t.put("manager_id",manager_id);
//            	t.put("door_no",door_no);
//            	t.put("street",street);
//            	t.put("city",city);
//            	t.put("state",state);
//            	t.put("pin_code",pin_code);
            	t.put("district",district);
            	t.put("manager_email", manager_email);
            	
           
            	
            	//Theater theater=new Theater(theater_id,name,door_no,street,city,district,state,pin_code,manager_id);
            	list.add(t); 
            }
                    
        } catch (SQLException ex) {
        	System.out.println("Exception in getting theaters "+ex);
        	
        }
     return list;
    }
	
	public String addTheater(JSONObject jsonData)
	{
		try 
		{
			String theater_name=(String)jsonData.get("theater-name");
			String email=(String)jsonData.get("theater-manager");
			String district=(String)jsonData.get("theater-district");
			//System.out.println("District "+district );
			int user_id=0;
			ps=con.prepareStatement("insert into users(email,user_role_id,name) values(?,?,?) ON CONFLICT(email,user_role_id) DO UPDATE set \"isActive\" = 1 ");
			ps.setString(1,email);
			ps.setInt(2,3);
			ps.setString(3,theater_name+" - Manager");
			ps.executeUpdate();
			
			ps=con.prepareStatement("select user_id from users where email=? and user_role_id=?");
			ps.setString(1,email);
			ps.setInt(2,3);
			rs=ps.executeQuery();
			while(rs.next())
			{
				user_id=rs.getInt(1);
			}
			
			ps=con.prepareStatement("insert into theater(name,manager_id,district) values(?,?,?)");
			ps.setString(1,theater_name);
			ps.setInt(2,user_id);
			ps.setString(3, district);
			ps.executeUpdate();
			
			return "Theater Added!!";
			
		}
		catch (SQLException e) 
		{
        	System.out.println("Exception in adding theater"+e);
        	return "Exception in adding theater"+e;
        }
	}
	
	public JSONObject getTheater(int theater_id)
    {
		JSONObject t=new JSONObject();
   	 try {
            ps = con.prepareStatement("select theater.*,users.email from theater inner join users on theater.manager_id=users.user_id where theater_id=?");
            ps.setInt(1,theater_id);
			ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
            	theater_id=rs.getInt("theater_id");
            	name=rs.getString("name");
            	manager_id=rs.getInt("manager_id");
            	//System.out.println("DB movie "+name);
//            	door_no=rs.getString("door_no");
//            	street=rs.getString("street");
//            	city=rs.getString("city");
//            	state=rs.getString("state");
//            	pin_code=rs.getString("pin_code");
            	district=rs.getString("district");
            	String manager_email=rs.getString("email");
            	
            	t.put("theater_id",theater_id);
            	t.put("theater_name",name);
            	t.put("manager_id",manager_id);
//            	t.put("door_no",door_no);
//            	t.put("street",street);
//            	t.put("city",city);
            	t.put("district",district);
//            	t.put("state",state);
//            	t.put("pin_code",pin_code);
            	t.put("manager_email", manager_email);
            	
            }
                   
        } catch (SQLException ex) {
        	System.out.println("Exception in getting theater "+ex);
        
        }
   	 return t;
    }
	
	public String updateTheater(int theater_id,JSONObject jsonData)
	{
		try 
		{
			String theater_name=(String)jsonData.get("edit-theater-name");
			String district=(String)jsonData.get("edit-theater-district");
			int user_id=0;
			if(jsonData.containsKey("edit-theater-manager"))
			{
				String email=(String)jsonData.get("edit-theater-manager");
				System.out.println("District "+district );
				
				ps=con.prepareStatement("insert into users(email,user_role_id,name) values(?,?,?) ON CONFLICT(email,user_role_id) DO UPDATE set \"isActive\" = 1 ");
				ps.setString(1,email);
				ps.setInt(2,3);
				ps.setString(3,theater_name+" - Manager");
				ps.executeUpdate();
				
				ps=con.prepareStatement("select user_id from users where email=? and user_role_id=?");
				ps.setString(1,email);
				ps.setInt(2,3);
				rs=ps.executeQuery();
				if(rs.next())
				{
					user_id=rs.getInt(1);
				}
			}
			if(jsonData.containsKey("manager_id"))
			{
				user_id=Integer.parseInt((String) jsonData.get("manager_id"));
			}
			//No need to cancel shows
			ps=con.prepareStatement("update theater set name=?,manager_id=? where theater_id=?");
			ps.setString(1,theater_name);
			ps.setInt(2,user_id);
			ps.setInt(3,theater_id);
			ps.executeUpdate();
			
			//cancel shows if the city has changed
			ps=con.prepareStatement("update theater set district=? where theater_id=? and LOWER(district)!=LOWER(?)");
			ps.setString(1,district);
			ps.setInt(2,theater_id);
			ps.setString(3,district);
			System.out.println(ps);
			int n=ps.executeUpdate();
			System.out.println("Number of rows affected : "+n);
			if(n==1)
			{
				ps=con.prepareStatement("select show.* from show inner join screen on screen.screen_id=show.screen_id inner join theater\r\n"
						+ "on screen.theater_id=theater.theater_id "
						+ "and theater.theater_id=? and status='Booking opened' where show_date > CURRENT_DATE "
						+ "OR (show_date = CURRENT_DATE and start_time >= CURRENT_TIME)");
				ps.setInt(1,theater_id);
				rs=ps.executeQuery();
				while(rs.next())
				{
					int show_id=rs.getInt("show_id");
					showDao.deleteShow(show_id);
					System.out.println("Sho_id"+show_id);
				}
			}
			
			
			return "Theater Details Updated!!";
			
		}
		catch (SQLException e) 
		{
        	System.out.println("Exception in adding theater"+e);
        	return "Exception in updating theater"+e;
        }
	}
	
	public String cancelTheater(int theater_id)
	{
		try 
		{
				ps=con.prepareStatement("select show.* from show inner join screen on screen.screen_id=show.screen_id inner join theater "
						+ "on screen.theater_id=theater.theater_id "
						+ "and theater.theater_id=? and status='Booking opened' where show_date > CURRENT_DATE "
						+ "OR (show_date = CURRENT_DATE and start_time >= CURRENT_TIME)");
				ps.setInt(1,theater_id);
				rs=ps.executeQuery();
				while(rs.next())
				{
					int show_id=rs.getInt("show_id");
					showDao.deleteShow(show_id);
					System.out.println("Sho_id"+show_id);
				}
				
				ps=con.prepareStatement("update theater set \"isAvailable\"=0 where theater_id=?");
				ps.setInt(1,theater_id);
				ps.executeUpdate();
			
			
			
			return "Theater Removed from the Application!!";
			
		}
		catch (SQLException e) 
		{
        	System.out.println("Exception in adding theater"+e);
        	return "Exception in updating theater"+e;
        }
	}
	
	public JSONArray getTheaterCollection(int theater_id)
	{
		JSONArray arr=new JSONArray();
		try
		{
			ps=con.prepareStatement("select screen.screen_id,screen.screen_name,max(theater.theater_id) as theater_id,"
					+ "(SUM(ticket.seat_prize) - SUM(ticket.refund))as collection "
					+ "from ticket "
					+ "inner join booking on booking.booking_id=ticket.booking_id "
					+ "inner join show on show.show_id=booking.show_id "
					+ "inner join screen on show.screen_id=screen.screen_id " 
					+ "inner join theater on screen.theater_id=theater.theater_id and theater.theater_id=? "
					+ "group by screen.screen_id");
			ps.setInt(1,theater_id);
			rs=ps.executeQuery();
			while(rs.next())
			{
				JSONObject s=new JSONObject();
				s.put("screen_id",rs.getInt("screen_id"));
				s.put("theater_id",rs.getInt("theater_id"));
				s.put("screen_name",rs.getString("screen_name"));
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

    

