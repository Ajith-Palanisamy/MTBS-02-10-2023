package com.mtbs.offer;

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

@Path("offer")
public class OfferResource 
{
	OfferDAO offerDao=new OfferDAO();
	
	@GET
	public String getOffers()
	{
		return offerDao.getOffers().toString();
	}
	
	
	
	
	@POST
	public String addOffer(String jsonData)
	{
		System.out.println(jsonData);
		JSONObject jsonObj=null;
		try {
			jsonObj = (JSONObject)new JSONParser().parse(jsonData);
			
		} catch (ParseException e) {
			System.out.println("exception in parsing json in adding offer"+e);
		}
		return offerDao.addOffer(jsonObj);
	}
	
	@PUT
	@Path("/{offer_id}")
	public String updateOffer(@PathParam("offer_id") int offer_id,String jsonData)
	{
		JSONObject jsonObj=null;
		try {
			jsonObj = (JSONObject)new JSONParser().parse(jsonData);
			
		} catch (ParseException e) {
			System.out.println("exception in parsing json in updating offer"+e);
		}
		return offerDao.updateOffer(offer_id,jsonObj);
	}
	
	@PUT
	@Path("/{offer_id}/cancel")
	public String removeOffer(@PathParam("offer_id") int offer_id)
	{
		return offerDao.cancelOffer(offer_id);
	}
	
	@GET
	@Path("/valid")
	public String getValidOffers()
	{
		return offerDao.getValidOffers().toString();
	}
	

}
