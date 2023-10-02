package com.mtbs.movie;

import java.util.LinkedList;
import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.mtbs.show.Show;
import com.sun.jersey.multipart.FormDataParam;

@Path("movie")
public class MovieResource 
{
	MovieDAO movieDao=new MovieDAO();
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public List<Movie> getMovies()
	{
		
		return movieDao.getAllMovies();
	}
	
	@GET
	@Path("/{movie_id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Movie getMovie(@PathParam("movie_id") int movie_id)
	{
		System.out.println("getMovie Called..");
		return movieDao.getMovie(movie_id);
	}
	
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED})
	public Movie addMovie(@FormParam("name") String name,@FormParam("certificate") String certificate,@FormParam("director") String director,
			@FormParam("description") String description,@FormParam("duration") String duration,@FormParam("image") String image,
			@FormParam("language1") String language1,@FormParam("language2") String language2,@FormParam("language3") String language3)
	{
		System.out.println("Languages : "+language1+" "+language2+" "+language3);
		List<Integer> languages=new LinkedList<Integer>();
		if(language1!=null)
		{
			languages.add(Integer.parseInt(language1));
		}
		if(language2!=null)
		{
			languages.add(Integer.parseInt(language2));
		}
		if(language3!=null)
		{
			languages.add(Integer.parseInt(language3));
		}
		return movieDao.addMovie(name,certificate,director,description,duration,image,languages);
	}
	
	
	@PUT
	@Path("/{movie_id}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED})
	public Movie updateMovie(@PathParam("movie_id") int movie_id,@FormParam("name") String name,@FormParam("certificate") String certificate,@FormParam("director") String director,
			@FormParam("description") String description,@FormParam("duration") String duration,@FormParam("image") String image,
			@FormParam("language1") String language1,@FormParam("language2") String language2,@FormParam("language3") String language3)
	{
		
        System.out.println("Languages : "+language1+" "+language2+" "+language3);
		List<Integer> languages=new LinkedList<Integer>();
		if(language1!=null)
		{
			languages.add(Integer.parseInt(language1));
		}
		if(language2!=null)
		{
			languages.add(Integer.parseInt(language2));
		}
		if(language3!=null)
		{
			languages.add(Integer.parseInt(language3));
		}
		return movieDao.updateMovie(movie_id,name,certificate,director,description,duration,image,languages);
		
		 
	}
	

	
	@PUT
	@Path("cancel/{movie_id}")
	public String cancelMovie(@PathParam("movie_id") int movie_id)
	{
		Movie m=movieDao.getMovie(movie_id);
	    movieDao.cancelMovie(movie_id);
	    return ("Movie '"+m.getName()+"' deleted..");
	}
	
//	@DELETE
//	@Path("/{movie_id}")
//	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
//	public Student deleteStudent(@PathParam("id") int id)
//	{
//		Student s=repo.getStudent(id);
//		System.out.println("deleteStudent Called..");
//	    repo.deleteStudent(id);
//	    
//	    return s;
//	}
	
	@GET
	@Path("/latest")
	@Produces({MediaType.APPLICATION_JSON})
	@RolesAllowed({"manager","user","admin"})
	public List<Movie> getLatestMovies()
	{
		return movieDao.getLatestMovies();
	}
	
	@GET
	@Path("/upcoming")
	@Produces({MediaType.APPLICATION_JSON})
	@PermitAll
	public List<Movie> getUpcomingMovies()
	{
		return movieDao.getUpcomingMovies();
	}
	
	@GET
	@Path("/{movie_id}/show")
	@Produces({MediaType.APPLICATION_JSON})
	public String getShows(@PathParam("movie_id") int movie_id)
	{
		
		return movieDao.getShows(movie_id).toString();
	}
	
	
	
}
