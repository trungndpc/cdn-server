package vn.com.udev.cdn.common;

import java.util.Arrays;
import java.util.List;

public class WhiteListDomain {
    public static final WhiteListDomain INSTANCE = new WhiteListDomain();
    public static final List<String> DOMAINS = Arrays.asList("insee.com.vn", "cuahang.insee.udev.com.vn", "nhathau.insee.com.vn","admin-nhathau.insee.udev.com.vn", "admin.insee.udev.com.vn",
            "dev-admin-nhathau.insee.udev.com.vn", "conwood.insee.udev.com.vn", "admin-conwood.insee.udev.com.vn", "localhost", "knauf.udev.com.vn", "udev.com.vn");

    public static final List<String> ORIGIN_ZALO_MINI_APP = Arrays.asList("https://h5.zdn.vn/", "zbrowser://h5.zdn.vn/", "zdn.vn", "h5.zdn.vn");


    public boolean hasWhitelist(String domain) {
        return DOMAINS.contains(domain) || ORIGIN_ZALO_MINI_APP.contains(domain);
    }

}
