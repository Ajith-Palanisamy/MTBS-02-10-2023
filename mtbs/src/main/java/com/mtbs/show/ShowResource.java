package com.mtbs.show;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mtbs.booking.Booking;
import com.mtbs.movie.Movie;

import com.mtbs.seatingArrangement.seatingArrangementDAO;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("show")
public class ShowResource 
{
	ShowDAO showDao=new ShowDAO();
	seatingArrangementDAO seatingArrangementDao =new seatingArrangementDAO();
	
	@PUT
	@Path("/{show_id}")
	public String updateShow(@PathParam("show_id") int show_id,String jsonObject)
	{
		JSONObject jsonData=null;
		try {
			jsonData = (JSONObject)new JSONParser().parse(jsonObject);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return showDao.updateShow(show_id,jsonData);
	}
	
	@PUT
	@Path("/{show_id}/cancel")
	public String deleteShow(@PathParam("show_id") int show_id)
	{
	    return showDao.deleteShow(show_id);
	}
	
	@GET
	@Path("/{show_id}/seatingArrangement")
	@Produces({MediaType.APPLICATION_JSON})
	public String getSeatingArrangement(@PathParam("show_id") int show_id)
	{
		return seatingArrangementDao.getShowSeatingArrangement(show_id).toString();
	}
	
	@POST
	@Path("/{show_id}/bookTicket")
	public String bookTicket(@PathParam("show_id") int show_id,String jsonObject) 
	{
		JSONObject jsonData=null;
		try {
			jsonData = (JSONObject)new JSONParser().parse(jsonObject);
		} catch (ParseException e) 
		{
			System.out.println("Exception in bookTicket() "+e);
		}
		return showDao.bookTicket(show_id,jsonData);
	}
	
	@GET
	@Path("/{show_id}/cancellationPercentage")
	@Produces({MediaType.APPLICATION_JSON})
	public String getCancellationPercentage(@PathParam("show_id") int show_id)
	{
		return showDao.getCancellationPercentage(show_id).toString();
	}
	
	@GET
	@Path("/{show_id}/collection")
	@Produces({MediaType.APPLICATION_JSON})
	public String getCollection(@PathParam("show_id") int show_id)
	{
		return showDao.getCollection(show_id).toString();
	}
	
	
}
