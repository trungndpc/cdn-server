package vn.com.udev.cdn.util;

import com.google.common.net.InternetDomainName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpUtil {
    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("referer");
    }

    public static String getPrivateDomain(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String host = uri.getHost();
        return InternetDomainName.from(host).topPrivateDomain().toString();
    }

    public static String getDomain(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return uri.getHost();
    }

}
