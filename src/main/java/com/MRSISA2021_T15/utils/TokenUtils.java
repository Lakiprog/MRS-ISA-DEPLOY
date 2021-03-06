package com.MRSISA2021_T15.utils;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.MRSISA2021_T15.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenUtils {

	@Value("backend")
	private String APP_NAME;

	@Value("somesecret")
	public String SECRET;

	@Value("86400000")
	private int EXPIRES_IN;
	
	
	@Value("Authorization")
	private String AUTH_HEADER;
	
	private static final String AUDIENCE_WEB = "web";
	
	private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
	
	public String generateToken(String username) {
		return Jwts.builder()
				.setIssuer(APP_NAME)
				.setSubject(username)
				.setAudience(AUDIENCE_WEB)
				.setIssuedAt(new Date())
				.setExpiration(generateExpirationDate())
				.signWith(SIGNATURE_ALGORITHM, SECRET).compact();
	}
	
	private Date generateExpirationDate() {
		return new Date(new Date().getTime() + EXPIRES_IN);
	}
	
	public int getExpiredIn() {
		return EXPIRES_IN;
	}
	
	public String getToken(HttpServletRequest request) {
		String authHeader = getAuthHeaderFromHeader(request);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}
	
	public String getUsernameFromToken(String token) {
		String username = null;
		try {
			final var claims = this.getAllClaimsFromToken(token);
			if (claims != null) {
				username = claims.getSubject();
			}
		} catch (ExpiredJwtException ex) {
			throw ex;
		} catch (NullPointerException ex) {
			throw ex;
		} catch (Exception e) {
			username = null;
		}
		return username;
	}
	
	private Claims getAllClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser()
					.setSigningKey(SECRET)
					.parseClaimsJws(token)
					.getBody();
		} catch (ExpiredJwtException ex) {
			throw ex;
			
		}  catch (NullPointerException ex) {
			throw ex;
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}
	
	public Boolean validateToken(String token, UserDetails userDetails) {
		var user = (User) userDetails;
		final var username = getUsernameFromToken(token);
		final var created = getIssuedAtDateFromToken(token);
		return (username != null
			&& username.equals(userDetails.getUsername())
			&& !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()));
	}
	
	public Date getIssuedAtDateFromToken(String token) {
		Date issueAt = null;
		try {
			final var claims = this.getAllClaimsFromToken(token);
			if (claims != null) {
				issueAt = claims.getIssuedAt();
			}
		} catch (ExpiredJwtException ex) {
			throw ex;
		} catch (NullPointerException ex) {
			throw ex;
		} catch (Exception e) {
			issueAt = null;
		}
		return issueAt;
	}
	
	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
		return (lastPasswordReset != null && created.before(lastPasswordReset));
	}
	
	public String getAuthHeaderFromHeader(HttpServletRequest request) {
		return request.getHeader(AUTH_HEADER);
	}
	
	public String getAudienceFromToken(String token) {
		String audience = null;
		try {
			final var claims = this.getAllClaimsFromToken(token);
			if (claims != null) {
				audience = claims.getAudience();
			}
		} catch (ExpiredJwtException ex) {
			throw ex;
		} catch (NullPointerException ex) {
			throw ex;
		} catch (Exception e) {
			audience = null;
		}
		return audience;
	}

	public Date getExpirationDateFromToken(String token) {
		Date expiration = null;
		try {
			final var claims = this.getAllClaimsFromToken(token);
			if (claims != null) {
				expiration = claims.getExpiration();
			}
		} catch (ExpiredJwtException ex) {
			throw ex;
		} catch (NullPointerException ex) {
			throw ex;
		} catch (Exception e) {
			expiration = null;
		}
		
		return expiration;
	}
	
}
