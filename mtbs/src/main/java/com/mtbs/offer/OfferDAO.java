package com.mtbs.offer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mtbs.db.DBHandler;

public class OfferDAO 
{
	DBHandler obj=DBHandler.getInstance();
    Connection con = obj.getConnection();
    PreparedStatement ps;
    Statement st;
    ResultSet rs;
    
    public String addOffer(JSONObject data)
    {
    	try
    	{
    		ps=con.prepareStatement("insert into offer(offer_name,no_of_tickets,discount,start_date,end_date) values(?,?,?,?,?)");
    		ps.setString(1,(String)data.get("offer-name"));
    		ps.setInt(2,Integer.parseInt((String)data.get("no-of-tickets")));
    		ps.setInt(3,Integer.parseInt((String)data.get("discount")));
    		ps.setDate(4,Date.valueOf(LocalDate.parse((String)data.get("start-date"))));
    		ps.setDate(5,Date.valueOf(LocalDate.parse((String)data.get("end-date"))));
    		ps.executeUpdate();
    		
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in adding offer "+e);
    	}
    	return "success";
    }
    
    public String updateOffer(int offer_id,JSONObject data)
    {
    	try
    	{
    		ps=con.prepareStatement("update offer set offer_name=?,no_of_tickets=?,discount=?,start_date=?,end_date=? where offer_id=?");
    		ps.setString(1,(String)data.get("edit-offer-name"));
    		ps.setInt(2,Integer.parseInt((String)data.get("edit-no-of-tickets")));
    		ps.setInt(3,Integer.parseInt((String)data.get("edit-discount")));
    		ps.setDate(4,Date.valueOf(LocalDate.parse((String)data.get("edit-start-date"))));
    		ps.setDate(5,Date.valueOf(LocalDate.parse((String)data.get("edit-end-date"))));
    		ps.setInt(6, offer_id);
    		ps.executeUpdate();
    		
    		return "Offer updated!!";
    		
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in updating offer "+e);
    		return "Exception in updating offer "+e;
    	}
    	
    }
    
    public String cancelOffer(int offer_id)
    {
    	try
    	{
    		ps=con.prepareStatement("update offer set \"isAvailable\"=0 where offer_id=?");
    		ps.setInt(1, offer_id);
    		ps.executeUpdate();
    		return "Offer cancelled!!";
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in cancelling offer "+e);
    		return "Exception in cancelling offer "+e;
    	}
    }
    
    public JSONArray getOffers()
    {
    	JSONArray arr=new JSONArray();
    	try
    	{
    		ps=con.prepareStatement("select * from offer where \"isAvailable\"=1 order by no_of_tickets,discount desc,start_date,end_date");
    		rs=ps.executeQuery();
    		while(rs.next())
    		{
    			JSONObject s=new JSONObject();
    			s.put("offer_id",rs.getInt("offer_id"));
    			s.put("offer_name",rs.getString("offer_name"));
    			s.put("no_of_tickets",rs.getInt("no_of_tickets"));
    			s.put("discount",rs.getInt("discount"));
    			s.put("start_date",rs.getDate("start_date").toString());
    			s.put("end_date",rs.getDate("end_date").toString());
    			
    			arr.add(s);
    			
    		}
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in retriving offers "+e);
    	}
    	return arr;
    }
    
    public JSONArray getValidOffers()
    {
    	JSONArray arr=new JSONArray();
    	try
    	{
    		ps=con.prepareStatement("select * from offer where start_DATE <= CURRENT_DATE and end_date >= CURRENT_DATE and \"isAvailable\"=1 order by no_of_tickets,discount desc,start_date,end_date ");
    		rs=ps.executeQuery();
    		while(rs.next())
    		{
    			JSONObject s=new JSONObject();
    			s.put("offer_id",rs.getInt("offer_id"));
    			s.put("offer_name",rs.getString("offer_name"));
    			s.put("no_of_tickets",rs.getInt("no_of_tickets"));
    			s.put("discount",rs.getInt("discount"));
    			s.put("start_date",rs.getDate("start_date").toString());
    			s.put("end_date",rs.getDate("end_date").toString());
    			arr.add(s);
    		}
    	}
    	catch(SQLException e)
    	{
    		System.out.println("Exception in retriving offers "+e);
    	}
    	return arr;
    }
}
