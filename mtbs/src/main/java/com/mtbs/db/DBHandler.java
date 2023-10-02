package com.mtbs.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBHandler 
{
	private static DBHandler obj;
	private DBHandler(){	}
	private static Connection con=null;
	private static String db="mtbs";
	private static String username="postgres";
	private static String password="root";
	
	
	public static DBHandler getInstance()
	{
		if(obj==null)
			obj=new DBHandler();
		 
		return obj;
	}
	
	public static Connection getConnection()
	{
		try
		{
			//System.out.println("inside create connection");
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+db,username,password);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("connection");
		return con;
		
	}
	
	
}
