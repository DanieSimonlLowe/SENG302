package nz.ac.canterbury.seng302.tab.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Custom FetchHeaderFilter class, to allow for fetching the location API key.
 * 
 * @author Nathan Harper
 * @version 1.0.0, April 23
 */
@WebFilter(urlPatterns = "**/api/location-request")
@Component
public class FetchHeaderFilter extends OncePerRequestFilter {

    /** {@inheritDoc} */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Pattern expectedUri = Pattern.compile("^.*\\/api\\/location-request$");
        Matcher uriTest = expectedUri.matcher(request.getRequestURI());
        if ("GET".equals(request.getMethod()) && uriTest.matches()) {
            String requestedWithHeader = request.getHeader("X-Requested-With");
            if (requestedWithHeader != null && requestedWithHeader.equals("XMLHttpRequest")) {
                filterChain.doFilter(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        filterChain.doFilter(request, response);
    }

}
