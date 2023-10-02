package com.mtbs.screen;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("screen")
public class ScreenResource 
{
	ScreenDAO screenDao=new ScreenDAO();
	
	@POST
	@Path("/{screen_id}/availableSlots")
	@Produces({MediaType.APPLICATION_JSON})
	public String getAvailableSlots(@PathParam("screen_id") int screen_id,String jsonObject)
	{
		JSONObject jsonData=null;
		try {
			jsonData = (JSONObject)new JSONParser().parse(jsonObject);
		} catch (Exception e) {
			System.out.println("Exption in parsing jsonObject in getAvaliableSlots "+e);
		}
		System.out.println(jsonData);
		return screenDao.getAvailableSlots(screen_id,jsonData).toString();
		
	}
	
	@POST
	public String addScreen(String jsonObject)
	{
		JSONObject jsonData=null;
		try {
			jsonData = (JSONObject)new JSONParser().parse(jsonObject);
		} catch (Exception e) {
			System.out.println("Exption in parsing jsonObject in addScreen "+e);
		}
		System.out.println(jsonData);
		return screenDao.addScreen(jsonData);
	}
	
	@PUT
	@Path("/{screen_id}")
	public String updateScreen(@PathParam("screen_id") int screen_id,String jsonObject)
	{
		JSONObject jsonData=null;
		try {
			jsonData = (JSONObject)new JSONParser().parse(jsonObject);
		} catch (Exception e) {
			System.out.println("Exption in parsing jsonObject in addScreen "+e);
		}
		System.out.println(jsonData);
		return screenDao.updateScreen(screen_id,jsonData);
	}
	
	@GET
	@Path("/{screen_id}/collection")
	@Produces({MediaType.APPLICATION_JSON})
	public String getScreenCollection(@PathParam("screen_id") int screen_id)
	{
		return screenDao.getScreenCollection(screen_id).toString();
	}
	
	@PUT
	@Path("/{screen_id}/cancel")
	public String removeScreen(@PathParam("screen_id") int screen_id)
	{
		return screenDao.removeScreen(screen_id);
	}
	
}
