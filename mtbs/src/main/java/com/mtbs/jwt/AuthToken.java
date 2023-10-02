package com.mtbs.jwt;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
public class AuthToken 
{
	public static SecretKey key;
	static
	{
		if(key==null)
			key = Keys.secretKeyFor(SignatureAlgorithm.HS256);//due to making changes in java file,Every time the server reloads automatically, that time the class files are loaded again.so in the static block again new secret key generated,this new key is used to check for token validation.so all the tokens become invalid,if you change any java file once you started server..as a result all the users are asked to login again 
			
	}
	public static String generateToken(String email,int user_id,int user_role_id,String user_role)
	{
		//SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);//what algorithm should be used
		System.out.println("Login key-"+key);
		return Jwts.builder()
                .setSubject(email)//unique string
                .claim("user_id", user_id)//some addition optional key values belongs to subject
                .claim("user_role_id", user_role_id)
                .claim("user_role", user_role)
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
                .signWith(key)
                .compact();
	}
    //You can decode the generated token at https://jwt.io/
}
