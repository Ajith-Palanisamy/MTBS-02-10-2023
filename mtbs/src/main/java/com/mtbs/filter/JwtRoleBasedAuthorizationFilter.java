package com.mtbs.filter;
import java.util.*;

import com.mtbs.jwt.AuthToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;
import java.security.Key;



@Provider//it is jersey annotation which automatically identify..it is a filter
@Priority(value = 1000) // we can adjust the priority based on your needs
public class JwtRoleBasedAuthorizationFilter implements ContainerRequestFilter {
    
	@Context
    private ResourceInfo resourceInfo;
	List<String> UNAUTHORIZED_PATHS=List.of("user/login","user/register");

    @Override
    public void filter(ContainerRequestContext requestContext) {
    	String path = requestContext.getUriInfo().getPath();
    	System.out.println("Path : "+path);
    	
    	if(UNAUTHORIZED_PATHS.contains(path))
    		return;
    	
        // Get the Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            //No token or invalid format, abort the request with Unauthorized status
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized access..Please login").build());
            return;
        }

        // Extract the token from the header
        String token = authorizationHeader;

        try {
        	//Generate the key dynamically using the same algorithm (HS256)
        	System.out.println(token);
            Key key = AuthToken.key;//Keys.secretKeyFor(SignatureAlgorithm.HS256);
            System.out.println("key-"+key);
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            System.out.println("claimsJws"+claimsJws);
            // Token is valid, you can access the claims if needed
            Claims claims = claimsJws.getBody();
            String userEmail = claims.getSubject();
            Integer userId =claims.get("user_id",Integer.class);
            Integer userRoleId = claims.get("user_role_id", Integer.class);
            String userRole = claims.get("user_role",String.class);
            
            Method method = resourceInfo.getResourceMethod();
	    	
	        //Access allowed for all
	        if( ! method.isAnnotationPresent(PermitAll.class))
	        {
	            //Access denied for all
	            if(method.isAnnotationPresent(DenyAll.class))
	            {
	                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
	                         .entity("Access blocked for all users !!").build());
	                return;
	            }
	            
	              
	            //Verify user access
	            if(method.isAnnotationPresent(RolesAllowed.class))
	            {
	                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
	                Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
	                  
	                //Is user valid?
	                if( ! isUserAllowed(userRole, rolesSet))
	                {
	                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
	                        .entity("You cannot access this resource").build());
	                    return;
	                }
	            }
	        }
        } 
        catch (Exception e) {
        	System.out.println(e);
        	e.printStackTrace();
        	System.out.println("Token is invalid, abort the request with Unauthorized status");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Session expired..Please login").build());
        }
    }
    private boolean isUserAllowed(final String userRole, final Set<String> rolesSet)
    {
        boolean isAllowed = false;
        if(rolesSet.contains(userRole))
        {
            isAllowed = true;
        }
        
        return isAllowed;
    }
}
