package com.mtbs.theater;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mtbs.screen.Screen;
import com.mtbs.screen.ScreenDAO;

import com.mtbs.seatingArrangement.seatingArrangementDAO;
import com.mtbs.show.Show;
import com.mtbs.show.ShowDAO;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("theater")
public class TheaterResource 
{
	TheaterDAO theaterDao=new TheaterDAO();
	ScreenDAO screenDao=new ScreenDAO();
	seatingArrangementDAO seatingArrangementDao =new seatingArrangementDAO();
	ShowDAO showDao=new ShowDAO();
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public String getTheaters()
	{
		
		return theaterDao.getAllTheaters().toString();
	}
	
	@POST
	public String addTheater(String jsonData)
	{
		System.out.println(jsonData);
		JSONObject jsonObj=null;
		try {
			jsonObj = (JSONObject)new JSONParser().parse(jsonData);
			
		} catch (ParseException e) 
		{
			System.out.println("Exception in parsing json data "+e);
		}
		
		return theaterDao.addTheater(jsonObj );
	}
	
	@PUT
	@Path("/{theater_id}")
	public String addTheater(@PathParam("theater_id") int theater_id,String jsonData)
	{
		System.out.println(jsonData);
		JSONObject jsonObj=null;
		try {
			jsonObj = (JSONObject)new JSONParser().parse(jsonData);
			
		} catch (ParseException e) 
		{
			System.out.println("Exception in parsing json data "+e);
		}
		return theaterDao.updateTheater(theater_id,jsonObj );
	}
	
	@PUT
	@Path("/{theater_id}/cancel")
	public String removeTheater(@PathParam("theater_id") int theater_id)
	{
		return theaterDao.cancelTheater(theater_id );
	}
	
	
	@GET
	@Path("/{theater_id}")
	@Produces({MediaType.APPLICATION_JSON})
	public String getTheater(@PathParam("theater_id") int theater_id)
	{
		
		return theaterDao.getTheater(theater_id).toString();
	}
	
	@GET
	@Path("/{theater_id}/screen")
	@Produces({MediaType.APPLICATION_JSON})
	public List<Screen> getScreens(@PathParam("theater_id") int theater_id)
	{
		
		return screenDao.getScreens(theater_id);
	}
	
	@GET
	@Path("/{theater_id}/screen/{screen_id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Screen getScreens(@PathParam("theater_id") int theater_id,@PathParam("screen_id") int screen_id)
	{
		
		return screenDao.getScreen(theater_id,screen_id);
	}
	
	@POST
	@Path("/{theater_id}/screen/{screen_id}/seatingArrangement")
	@Produces({MediaType.APPLICATION_JSON})
	public String updateSeatingArrangement(@PathParam("theater_id") int theater_id,@PathParam("screen_id") int screen_id,String jsonData)
	{
		System.out.println(jsonData);
		JSONObject jsonObj=null;
		try {
			jsonObj = (JSONObject)new JSONParser().parse(jsonData);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return seatingArrangementDao.updateSeatingArrangement(theater_id,screen_id,jsonObj).toString();
		
	}
	
	@GET
	@Path("/{theater_id}/screen/{screen_id}/seatingArrangement")
	@Produces({MediaType.APPLICATION_JSON})
	public String getSeatingArrangement(@PathParam("theater_id") int theater_id,@PathParam("screen_id") int screen_id)
	{
		return seatingArrangementDao.getSeatingArrangement(screen_id).toString();
	}
	
	
	@POST
	@Path("/{theater_id}/screen/{screen_id}/show")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public List<String> addShow(@PathParam("theater_id") int theater_id,@PathParam("screen_id") int screen_id,String jsonData)
	{
		System.out.println(jsonData);
		JSONObject jsonObj=null;
		try {
			jsonObj = (JSONObject)new JSONParser().parse(jsonData);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return showDao.addShow(theater_id,screen_id,jsonObj);
		
		
	}
	
	
	@GET
	@Path("/{theater_id}/screen/{screen_id}/show")
	@Produces({MediaType.APPLICATION_JSON})
	public String getAllShows(@PathParam("theater_id") int theater_id,@PathParam("screen_id") int screen_id)
	{
		System.out.println("inside get shows");
		return showDao.getAllShows(theater_id,screen_id).toString();
		
	}
	
	@GET
	@Path("/{theater_id}/collection")
	@Produces({MediaType.APPLICATION_JSON})
	public String getTheaterCollection(@PathParam("theater_id") int theater_id)
	{
		return theaterDao.getTheaterCollection(theater_id).toString();
	}
	
}
