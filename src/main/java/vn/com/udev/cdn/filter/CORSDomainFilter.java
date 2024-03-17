package vn.com.udev.cdn.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSDomainFilter extends CorsFilter {
    private static final String REFERER_HEADER = "Referer";
    private static final String DOMAIN_FORMAT = "%s://%s:%d";
    private static final String DOMAIN_FORMAT_NO_PORT = "%s://%s";


    public CORSDomainFilter(CorsConfigurationSource configSource) {
        super(configSource);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String referer = request.getHeader(REFERER_HEADER);
        String clientDomain = "*";
        try {
            URL url = new URL(referer);
            if (url.getPort() == -1) {
                clientDomain = String.format(DOMAIN_FORMAT_NO_PORT, url.getProtocol(), url.getHost());
            }else{
                clientDomain = String.format(DOMAIN_FORMAT, url.getProtocol(), url.getHost(), url.getPort());
            }
        } catch (MalformedURLException ignored) {
        }
        response.setHeader("Access-Control-Allow-Origin", clientDomain);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
