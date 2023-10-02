package com.mtbs.movie;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mtbs.db.DBHandler;
import com.mtbs.screen.Screen;
import com.mtbs.screen.ScreenDAO;
import com.mtbs.show.Show;
import com.mtbs.show.ShowDAO;
import com.mtbs.theater.Theater;


public class MovieDAO 
{
	DBHandler obj=DBHandler.getInstance();
    Connection con = obj.getConnection();
    PreparedStatement ps;
    Statement st;
    ResultSet rs;
    
    int movie_id;
	String name;
	String certificate;
	String director;
	String description;
	LocalTime duration;
	String image;
	List<String> languages=null;
	LocalDateTime added_time;
	Time sqlTime;
	Timestamp sqlTimestamp;
	Date utilDate;
	
	ScreenDAO screenDao=new ScreenDAO();
	ShowDAO showDao=new ShowDAO();
    
    public List<Movie> getAllMovies()
    {
    System.out.println("Inside getmovie");
    List<Movie> list=new LinkedList<Movie>();
   	 try {
            ps = con.prepareStatement("select movie.*,STRING_AGG(language,',') as languages from movie "
            		+ "inner join movie_language_mapping ON movie_language_mapping.movie_id = movie.movie_id and  movie.\"isAvailable\"=1 and movie_language_mapping.\"isAvailable\"=1 "
            		+ "inner join language on movie_language_mapping.language_id = language.language_id group by movie.movie_id");
			ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
            	movie_id=rs.getInt("movie_id");
            	name=rs.getString("name");
            	//System.out.println("DB movie "+name);
            	certificate=rs.getString("certificate");
            	director=rs.getString("director");
            	description=rs.getString("description");
            	image=rs.getString("image");
            	
            	sqlTime=rs.getTime("duration");
            	utilDate = new Date(sqlTime.getTime());
            	duration=utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
            	
            	sqlTimestamp=rs.getTimestamp("added_time");
            	utilDate = new Date(sqlTime.getTime());
            	added_time=utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            	
            	String arr[]=rs.getString("languages").split(",");
            	
            	languages=new LinkedList<>(Arrays.asList(arr));	
            	Movie movie=new Movie(movie_id,name,certificate,languages,director,description,duration,image,added_time);
            	list.add(movie); 
            	//System.out.println("Hi "+movie.toString());
            }
            return list;          
        } catch (SQLException ex) {
        	System.out.println(ex.getMessage());
        	return null;
        }
    }
    public Movie addMovie(String name,String certificate,String director,String description,String duration,String image,List<Integer> languages)
    {
    		System.out.println("inside add movie DB");
    	 try{
             ps = con.prepareStatement("insert into movie(name,certificate,director,description,duration,image) values(?,?,?,?,?,?)");
             ps.setString(1,name);
             ps.setString(2,certificate);
             ps.setString(3,director);
             ps.setString(4,description);
             System.out.println(duration);
             ps.setTime(5,Time.valueOf(duration+":00"));
             ps.setString(6,image);
             ps.executeUpdate();
             System.out.println("Movie added");
             
             int movie_id=-1;
             ps = con.prepareStatement("select * from movie order by added_time desc limit 1");
             ResultSet rs=ps.executeQuery();
             while(rs.next())
             {
            	 movie_id=rs.getInt("movie_id");
             }
             System.out.println("Movie ID "+movie_id);
             
             ps = con.prepareStatement("insert into movie_language_mapping(movie_id,language_id) values(?,?)");
             for(int i=0;i<languages.size();i++)
             {
                 ps.setInt(1,movie_id);
                 ps.setInt(2,languages.get(i));
                 ps.addBatch();
             }
             ps.executeBatch();
             
             return getMovie(movie_id);
     } catch (SQLException ex) 
    	 {
         
             System.out.println(ex.getMessage());
             return null;
         }
    }
    
    public Movie updateMovie(int movie_id,String name,String certificate,String director,String description,String duration,String image,List<Integer> languages)
    {
    	 try{
    		 
    		 ps=con.prepareStatement("update movie set duration=?,certificate=? where "
    		 		+ "movie_id=? and (duration!=? or UPPER(certificate)!=UPPER(?))");
    		 
    		 ps.setTime(1,Time.valueOf(duration+":00"));
    		 ps.setString(2,certificate);
    		 ps.setInt(3,movie_id);
    		 ps.setTime(4,Time.valueOf(duration+":00"));
    		 ps.setString(5,certificate);
    		 int n=ps.executeUpdate();
 			 System.out.println("Number of rows affected : "+n);
    		 
    		 
             ps = con.prepareStatement("update movie set name=?,director=?,description=?,image=? where movie_id=?");
             ps.setString(1,name);
             ps.setString(2,director);
             ps.setString(3,description);
             ps.setString(4,image);
             ps.setInt(5,movie_id);
             ps.executeUpdate();
             
//             ps = con.prepareStatement("delete from movie_language_mapping where movie_id=?");
//             ps.setInt(1,movie_id);
//             ps.executeUpdate();
             
            
             ps = con.prepareStatement("insert into movie_language_mapping(movie_id,language_id) values(?,?) ON CONFLICT(movie_id,language_id) DO UPDATE set \"isAvailable\" = 1 ");
             ps.setInt(1,movie_id);
             for(int i=0;i<languages.size();i++)
             {
                 ps.setInt(2,languages.get(i));
                 ps.addBatch();
                 
             }
             ps.executeBatch();
             
             String sql="update movie_language_mapping set \"isAvailable\"=0 where movie_id=? and language_id not in (";
             
             ps=con.prepareStatement(sql);
             for (int i = 0; i < languages.size(); i++) {
                 sql += (i == 0 ? "?" : ", ?");
             }
             sql += ")";
             ps=con.prepareStatement(sql);
            
             ps.setInt(1,movie_id);
             for (int i = 0; i < languages.size(); i++) {
                ps.setInt(i + 2, languages.get(i));
             }
             ps.executeUpdate();
             
             if(n==1)
             {
            	 ps=con.prepareStatement("select show.* from "
            	 		+ "show inner join movie_language_mapping "
            	 		+ "on show.movie_language_mapping_id=movie_language_mapping.movie_language_mapping_id "
            	 		+ "and movie_language_mapping.movie_id=? and status='Booking opened' where "
            	 		+ "show_date > CURRENT_DATE OR (show_date = CURRENT_DATE and start_time >= CURRENT_TIME)");
            	 ps.setInt(1,movie_id);
            	 rs=ps.executeQuery();
            	 while(rs.next())
            	 {
            		 int show_id=rs.getInt("show_id");
                	 showDao.deleteShow(show_id);
            	 }
            	 
            	 System.out.println("Inside cancelling shows while update in duration or certificate");
            	 return getMovie(movie_id);
            	 
             }
             
             //Long[] langArray =languages.stream().map(Long::valueOf).toArray(Long[]::new);
             
//             for (int i = 0; i < languages.size(); i++) {
//                 langArray[i] = (long) languages.get(i);
//             }
             //Array sqlArray = con.createArrayOf("INTEGER", langArray);
             //Array sqlArray = con.createArrayOf("INTEGER",languages.toArray());
             
             //String values = String.join(",", languages.toString());
             //System.out.println("values "+values);
             sql="select show.* from show "
		           		+ "inner join movie_language_mapping on show.movie_language_mapping_id=movie_language_mapping.movie_language_mapping_id and movie_language_mapping.movie_id=? and status='Booking opened' "
		           		+ "where show_date > CURRENT_DATE OR (show_date = CURRENT_DATE and start_time >= CURRENT_TIME) "
		           		+ "and language_id not in (";
             for (int i = 0; i < languages.size(); i++) {
                 sql += (i == 0 ? "?" : ", ?");
             }
             sql += ")";
             ps=con.prepareStatement(sql);
            
             ps.setInt(1,movie_id);
             for (int i = 0; i < languages.size(); i++) {
                ps.setInt(i + 2, languages.get(i));
             }
             System.out.println(ps);
             rs=ps.executeQuery();
            
             while(rs.next())
             {
            	 int show_id=rs.getInt("show_id");
            	 showDao.deleteShow(show_id);
            	
             }
             
        
             System.out.println(ps);
             
             System.out.println("Movie updated");
            
             return getMovie(movie_id);
     } catch (SQLException ex) 
    	 {
         
             System.out.println("Exception in updating movie "+ex);
             ex.printStackTrace();
             return null;
         }
    }
    
    
    public Movie getMovie(int id)
    {
    	Movie movie = null;
   	 try {
            ps = con.prepareStatement("select movie.*,STRING_AGG(language,',') as languages from movie "
            		+ "inner join movie_language_mapping ON movie_language_mapping.movie_id = movie.movie_id and  movie.\"isAvailable\"=1 and movie_language_mapping.\"isAvailable\"=1 and movie.movie_id=?  "
            		+ "inner join language on movie_language_mapping.language_id = language.language_id group by movie.movie_id");
            ps.setInt(1,id);
			ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
            	movie_id=rs.getInt("movie_id");
            	name=rs.getString("name");
            	certificate=rs.getString("certificate");
            	director=rs.getString("director");
            	description=rs.getString("description");
            	image=rs.getString("image");
            	
            	sqlTime=rs.getTime("duration");
            	utilDate = new Date(sqlTime.getTime());
            	duration=utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
            	
            	sqlTimestamp=rs.getTimestamp("added_time");
            	utilDate = new Date(sqlTime.getTime());
            	added_time=utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            	
                String arr[]=rs.getString("languages").split(",");
            	
            	languages=new LinkedList<>(Arrays.asList(arr));	
            	
            	movie=new Movie(movie_id,name,certificate,languages,director,description,duration,image,added_time);
            }
            return movie;          
        } catch (SQLException ex) {
        	System.out.println(ex.getMessage());
        	return null;
        }
    }
    
    public void cancelMovie(int movie_id)
    {
    	try {
    		 ps = con.prepareStatement("select show.show_id from show "
    		 		+ "inner join movie_language_mapping "
    		 		+ "on show.movie_language_mapping_id=movie_language_mapping.movie_language_mapping_id and movie_language_mapping.movie_id=? and status='Booking opened' "
    		 		+ "where show_date > CURRENT_DATE OR (show_date = CURRENT_DATE and start_time >= CURRENT_TIME)");
    		 
             ps.setInt(1,movie_id);
             rs=ps.executeQuery();
             while(rs.next())
             {
            	 int show_id=rs.getInt(1);
            	 showDao.deleteShow(show_id);
             }
            
	    	 ps = con.prepareStatement("update movie set \"isAvailable\"=0 where movie_id=?");
	         ps.setInt(1,movie_id);
	         ps.executeUpdate();
       } 
    	catch (SQLException ex) {
       	System.out.println(ex.getMessage());
       	
       }
    	
    }
    
    public List<Movie> getLatestMovies()
    {
     List<Movie> list=new LinkedList<Movie>();
   	 try {
            ps = con.prepareStatement("select movie.* from movie "
            		+ "inner join movie_language_mapping ON movie_language_mapping.movie_id = movie.movie_id "
            		+ "inner join show on show.movie_language_mapping_id=movie_language_mapping.movie_language_mapping_id and status='Booking opened' "
            		+ "where show_date > CURRENT_DATE OR (show_date = CURRENT_DATE and start_time >= CURRENT_TIME) "
            		+ "group by movie.movie_id  order by name;");
			ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
            	movie_id=rs.getInt("movie_id");
            	name=rs.getString("name");
            	//System.out.println("DB movie "+name);
            	certificate=rs.getString("certificate");
            	director=rs.getString("director");
            	description=rs.getString("description");
            	image=rs.getString("image");
            	
            	sqlTime=rs.getTime("duration");
            	utilDate = new Date(sqlTime.getTime());
            	duration=utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
            	
            	sqlTimestamp=rs.getTimestamp("added_time");
            	utilDate = new Date(sqlTime.getTime());
            	added_time=utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            	
            	languages=new LinkedList<String>();
          
            	Movie movie=new Movie(movie_id,name,certificate,languages,director,description,duration,image,added_time);
            	list.add(movie); 
            }
            return list;          
        } catch (SQLException ex) {
        	System.out.println(ex.getMessage());
        	return null;
        }
    }
    
    public List<Movie> getUpcomingMovies()
    {
     List<Movie> list=new LinkedList<Movie>();
   	 try {
            ps = con.prepareStatement("select movie.* from movie where \"isAvailable\"=1 and  movie_id not in (select movie.movie_id "
            		+ "from movie inner join movie_language_mapping ON movie_language_mapping.movie_id = movie.movie_id "
            		+ "inner join show on show.movie_language_mapping_id=movie_language_mapping.movie_language_mapping_id and status='Booking opened' "
            		+ "where show_date > CURRENT_DATE OR (show_date = CURRENT_DATE and start_time >= CURRENT_TIME) "
            		+ "group by movie.movie_id  order by name) order by name");
			ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
            	movie_id=rs.getInt("movie_id");
            	name=rs.getString("name");
            	//System.out.println("DB movie "+name);
            	certificate=rs.getString("certificate");
            	director=rs.getString("director");
            	description=rs.getString("description");
            	image=rs.getString("image");
            	
            	sqlTime=rs.getTime("duration");
            	utilDate = new Date(sqlTime.getTime());
            	duration=utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
            	
            	sqlTimestamp=rs.getTimestamp("added_time");
            	utilDate = new Date(sqlTime.getTime());
            	added_time=utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            	
            	languages=new LinkedList<String>();
            	Movie movie=new Movie(movie_id,name,certificate,languages,director,description,duration,image,added_time);
            	list.add(movie); 
            }
            return list;          
        } catch (SQLException ex) {
        	System.out.println(ex.getMessage());
        	return null;
        }
    }
    
    public JSONArray getShows(int movie_id)
    {
    	JSONArray arr=new JSONArray();
    	try
    	{
    		ps=con.prepareStatement("select movie.movie_id,movie.name,show.*,theater.name as theaterName,theater.theater_id,theater.district,"
    				+ "language.language,screen.screen_id,screen_name from show inner join movie_language_mapping on "
    				+ "(show.movie_language_mapping_id=movie_language_mapping.movie_language_mapping_id "
    				+ " and movie_language_mapping.movie_id=? and show.status='Booking opened') "
    				+ "inner join screen on screen.screen_id=show.screen_id "
    				+ "inner join movie on movie.movie_id=movie_language_mapping.movie_id "
    				+ "inner join language on language.language_id=movie_language_mapping.language_id "
    				+ "inner join theater on theater.theater_id=screen.theater_id "
    				+ "where show_date > CURRENT_DATE OR (show_date = CURRENT_DATE and start_time >= CURRENT_TIME) order by show_date,show.start_time");
    		
    		ps.setInt(1, movie_id);
    		rs=ps.executeQuery();
    		while(rs.next())
    		{
    			JSONObject s=new JSONObject();
    			//s.put("movie_id",rs.getInt("movie_id"));
    			s.put("movie_name",rs.getString("name"));
    			s.put("show_id",rs.getInt("show_id"));
    			java.sql.Date t=(java.sql.Date)rs.getDate("show_date");
    			s.put("show_date",t.toLocalDate().toString());
    			Time tt=rs.getTime("start_time");
    			s.put("start_time",tt.toLocalTime().toString());
//    			tt=rs.getTime("end_time");
//    			s.setEnd_time(tt.toLocalTime());
    			s.put("language",rs.getString("language"));
    			s.put("theater_name",rs.getString("theaterName"));
    			s.put("district",rs.getString("district"));
    			s.put("screen_id",rs.getInt("screen_id"));
    			s.put("screen_name",rs.getString("screen_name"));
    			
    			arr.add(s);
    		}
    	}
    	catch (SQLException ex) 
 	    { 
     
          System.out.println("Exception in retriving shows "+ex);
        }
    	return arr;
    }
    
}
