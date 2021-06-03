package com.Udee.filters;

import com.Udee.models.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.Udee.utils.Constants.*;


public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public JWTAuthorizationFilter() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (containsJWT(request, response)) {
                Claims claims = validateToken(request);
                if (claims.get("user") != null) {
                    setUpSpringAuthentication(claims);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private Claims validateToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(JWT_HEADER).replace(JWT_PREFIX, "");
        return Jwts.parser().setSigningKey(JWT_SECRET.getBytes()).parseClaimsJws(jwtToken).getBody();
    }

    private void setUpSpringAuthentication(Claims claims) {
        try {
            List<String> authorities = (List) claims.get("authorities");
            String userClaim = (String) claims.get("user");
            UserDTO user = objectMapper.readValue(userClaim, UserDTO.class);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null,
                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JsonProcessingException e) {
            SecurityContextHolder.clearContext();
        }
    }

    private boolean containsJWT(HttpServletRequest request, HttpServletResponse res) {
        String authenticationHeader = request.getHeader(JWT_HEADER);
        return authenticationHeader != null && authenticationHeader.startsWith(JWT_PREFIX);
    }
}
