package com.mtbs.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.mtbs.db.DBHandler;
import com.mtbs.jwt.AuthToken;

public class UserDAO {
	 DBHandler obj=DBHandler.getInstance();
     Connection con = obj.getConnection();
     PreparedStatement ps;
     Statement st;
     ResultSet rs;
     public String addUser(String name,String mobile,String email,String password)
     {
    	JSONObject t=new JSONObject();
        try {
        	    String hashPwd = BCrypt.hashpw(password, BCrypt.gensalt(10));
                ps = con.prepareStatement("insert into users(name,mobilenumber,email,password,user_role_id) VALUES(?,?,?,?,?)");
                ps.setString(1,name);
                ps.setString(2, mobile);
                ps.setString(3, email);
                ps.setString(4, hashPwd);
                ps.setInt(5 , 2);
                ps.executeUpdate();
                System.out.println("user added");
                t.put("success", "Registration successful");
                           
        } catch (SQLException ex) {
            if(ex.getMessage().contains("duplicate")){
            	t.put("error", "User already exist.Please Login");
            }
            else
            {	
            	System.out.println(ex);
            }
        }
        
        return t.toString();
    }
     
     public List<User> getUsers()
     {
    	 List<User> list=new LinkedList<User>();
    	 User user=null;
    	 try {
             ps = con.prepareStatement("select * from users where user_role_id=2 and \"isActive\"= 1 ");
             rs=ps.executeQuery();
             while(rs.next())
             {
                 user=new User(rs.getInt("user_id"),rs.getString("name"),rs.getString("mobilenumber"),rs.getString("email"),rs.getString("password"),rs.getInt("user_role_id"));
                 list.add(user);
             }
                      
         } catch (SQLException ex) 
    	 {
             System.out.println("Exception in retriving users "+ex);
         }
    	 return list; 
     }
     
     public User getUser(String email)
     {
    	 User user=null;
    	 try {
             ps = con.prepareStatement("select users.*,user_role.role from users inner join user_role on users.user_role_id=user_role.user_role_id where \"isActive\"= 1  and email=?");
             ps.setString(1,email);
             rs=ps.executeQuery();
             while(rs.next())
             {
                 user=new User(rs.getInt("user_id"),rs.getString("name"),rs.getString("mobilenumber"),rs.getString("email"),rs.getString("password"),rs.getInt("user_role_id"),rs.getString("role"));
             }
                      
         } catch (SQLException ex) 
    	 {
             System.out.println("Exception in retriving user "+ex);
             ex.printStackTrace();
         }
    	 return user; 
     }
     
     
     public String login(String email,String password)
     {
    	 JSONObject t=new JSONObject();
    	 try {
             User user=getUser(email);
             if(user!=null)
             {
            	 boolean check = BCrypt.checkpw(password, user.getPassword());
            	 if(check)
            	 {
	                 t.put("user_id",user.getUser_id());
	                 t.put("name",user.getName());
	                 t.put("mobilenumber",user.getMobileNumber());
	                 t.put("email",user.getEmail());
	                 t.put("password",user.getPassword());
	                 t.put("user_role_id",user.getUser_role_id());
	                 t.put("user_role", user.getUser_role());
	                 t.put("token",AuthToken.generateToken(user.getEmail(),user.getUser_id(),user.getUser_role_id(),user.getUser_role()));
            	 }
            	 else
            	 {
            		 t.put("error","Password incorrect");
            	 }
             }
             else
             {
            	 t.put("error","User does not exist");
             }
         } catch (Exception ex) {
            System.out.println("Exception occured while user log in "+ex);
            ex.printStackTrace();
         }
    	 
    	 return t.toString();
     }
     
     public JSONArray getManagers()
     {
    	 JSONArray arr=new JSONArray();
    	 try {
    	 ps = con.prepareStatement("select * from users where user_role_id=3 and \"isActive\"=1 ");
         rs=ps.executeQuery();
         while(rs.next())
         {
        	 JSONObject s=new JSONObject();
        	 s.put("user_id", rs.getInt("user_id"));
        	 s.put("name",rs.getString("name") );
        	 s.put("email",rs.getString("email") );
        	 
        	arr.add(s);
         }
    	 }
    	 catch(SQLException e)
    	 {
    		 System.out.println("Exception in retriving managers "+e);
    	 }
    	 return arr;
    	 
     }
     
     public JSONArray getTheaters(int user_id)
     {
    	 JSONArray arr=new JSONArray();
    	 try 
    	 {
    	 ps = con.prepareStatement("select * from theater where manager_id=? and \"isAvailable\"=1 ");
    	 ps.setInt(1,user_id);
         rs=ps.executeQuery();
         while(rs.next())
         {
        	 JSONObject t=new JSONObject();
         	t.put("theater_id",rs.getInt("theater_id"));
         	t.put("theater_name",rs.getString("name"));
         	t.put("manager_id",rs.getInt("manager_id"));
//         	t.put("door_no",door_no);
//         	t.put("street",street);
//         	t.put("city",city);
//         	t.put("state",state);
//         	t.put("pin_code",pin_code);
         	t.put("district",rs.getString("district"));
        	 
        	arr.add(t);
         }
    	 }
    	 catch(SQLException e)
    	 {
    		 System.out.println("Exception in retriving managers "+e);
    	 }
    	 return arr;
    	 
     }
     
     public String getBookings(int user_id)
     {
    	 JSONArray arr=new JSONArray();
    	 try
    	 {
    		 ps=con.prepareStatement("select booking.booking_id,STRING_AGG(ticket.seat_number,',') AS seats,COUNT(ticket.ticket_id) as count,MAX(show.show_date) as show_date,MAX(show.start_time) as show_time, concat(MAX(theater.name),',',MAX(theater.district)) as theater,"
    		 		+ "MAX(screen.screen_name) as screen_name,MAX(movie.name) movie_name,STRING_AGG(ticket.seat_prize::text,',') as prizes,STRING_AGG(ticket.seat_type,',') as types,max(booking.status) as status,max(language) as language,max(show.show_id) as show_id,STRING_AGG(ticket.refund::text,',') as refund, max(offer.offer_name) as offer_name,max(ticket.discount) as discount "
    		 		+ "from ticket inner join booking on "
    		 		+ "booking.booking_id=ticket.booking_id and user_id=? "
    		 		+ "inner join show show on show.show_id=booking.show_id "
    		 		+ "inner join screen on screen.screen_id=show.screen_id "
    		 		+ "inner join theater on theater.theater_id = screen.theater_id "
    		 		+ "inner join movie_language_mapping on movie_language_mapping.movie_language_mapping_id=show.movie_language_mapping_id "
    		 		+ "inner join language ON language.language_id = movie_language_mapping.language_id "
    		 		+ "inner join movie on movie.movie_id= movie_language_mapping.movie_id "
    		 		+ "left join offer on booking.offer_id= offer.offer_id "
    		 		+ "group by booking.booking_id order by show_date,show_time") ;
    		 ps.setInt(1, user_id);
    		 rs=ps.executeQuery();
    		 while(rs.next())
    		 {
    			 JSONObject s=new JSONObject();
    			 LocalDate showDate;
    			 LocalTime showTime;
    			 s.put("booking_id",rs.getInt("booking_id"));
    			 s.put("show_date",rs.getString("show_date"));
    			 s.put("show_time",rs.getString("show_time"));
    			 s.put("movie_name",rs.getString("movie_name"));
    			 s.put("screen_name",rs.getString("screen_name"));
    			 s.put("theater",rs.getString("theater"));
    			 s.put("count",rs.getString("count"));
    			 s.put("seats",rs.getString("seats"));
    			 s.put("prizes",rs.getString("prizes"));
    			 s.put("types",rs.getString("types"));
    			 s.put("status",rs.getString("status"));
    			 s.put("language",rs.getString("language"));
    			 s.put("show_id", rs.getInt("show_id"));
    			 s.put("refunds",rs.getString("refund"));
    			 
    			 String t=rs.getString("offer_name");
    			 if (rs.wasNull()) 
    			 {
    				s.put("offer_name","-");
    				s.put("discount","-");
    		      } else {
    				  
    		    	  s.put("offer_name",t);
      				s.put("discount",rs.getInt("discount"));
    				}
    			 
    			 
    			 showDate=LocalDate.parse((String)s.get("show_date"));
    			 showTime=LocalTime.parse((String)s.get("show_time"));
    			 
    			 LocalDateTime dateTime = LocalDateTime.of(showDate,showTime);
    		     LocalDateTime currentDateTime = LocalDateTime.now();
    		     if (dateTime.isBefore(currentDateTime) && s.get("status").equals("Booked")) 
    		     {
    		    	
    		        s.put("status","Cancellation unavailable");
    		     }  
    			 arr.add(s);
    		 }
    	 }
	      catch (SQLException ex) 
    	 {
	         System.out.println("Exception in get bookings foR user "+ex);
	     }
    	 
    	 return arr.toString();
     }
     
     public String cancelBooking(int booking_id,int user_id,JSONObject jsonData)
     {
    	 try
    	 {
    		int vip=((Long)jsonData.get("vip")).intValue();
    		int premium=((Long)jsonData.get("premium")).intValue();
    		int normal=((Long)jsonData.get("normal")).intValue();
    		//System.out.println("vip-refund "+vip);
    		//System.out.println("premium-refund "+premium);
    		//System.out.println("normal-refund "+normal);
    		
    		int theater_wallet=((Long)jsonData.get("theater_wallet")).intValue();
    		int user_wallet=((Long)jsonData.get("user_wallet")).intValue();
    		ps=con.prepareStatement("update booking set status='Cancelled' where booking_id=?");
    		ps.setInt(1,booking_id);
    		ps.executeUpdate();
    		
    		ps=con.prepareStatement("update ticket set refund=seat_prize-? where booking_id=? and seat_type=?");
    		ps.setInt(1,vip);
    		ps.setInt(2,booking_id);
    		ps.setString(3,"VIP");
    		ps.addBatch();
    		
    		ps.setInt(1,premium);
    		ps.setInt(2,booking_id);
    		ps.setString(3,"Premium");
    		ps.addBatch();
    		
    		ps.setInt(1,normal);
    		ps.setInt(2,booking_id);
    		ps.setString(3,"Normal");
    		ps.addBatch();
    		ps.executeBatch();
    		
    		ps=con.prepareStatement("update users set wallet=wallet+? where user_id=?");
    		ps.setInt(1,user_wallet);
    		ps.setInt(2,user_id);
    		ps.executeUpdate();
    		
    		ps=con.prepareStatement("update theater set wallet=wallet-? where theater_id=(select theater.theater_id from theater inner join screen on screen.theater_id = theater.theater_id inner join show on show.screen_id=screen.screen_id inner join booking on booking.show_id=show.show_id and booking_id=?)");
    		ps.setInt(1,user_wallet);
    		ps.setInt(2,booking_id);
    		ps.executeUpdate();
    		
    		return "success"; 
    	 }
    	 catch (SQLException ex) 
    	 {
	         System.out.println("Exception in cancel booking for user "+ex);
	         return "error";
	     }
    	 
     }
}



