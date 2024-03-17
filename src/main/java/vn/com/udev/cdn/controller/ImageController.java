package vn.com.udev.cdn.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.com.udev.cdn.common.BaseResponse;
import vn.com.udev.cdn.common.ErrorCode;
import vn.com.udev.cdn.common.WhiteListDomain;
import vn.com.udev.cdn.service.StorageService;
import vn.com.udev.cdn.util.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    private static final Logger LOGGER = LogManager.getLogger();
    @Autowired
    private StorageService service;


    @PostMapping("/upload-zalo")
    public ResponseEntity<BaseResponse> uploadV2(@RequestParam("file") MultipartFile[] images,
                                                 HttpServletRequest request) throws IOException {
        return upload(images, request);
    }

//    @PostMapping("/upload-zalo-v2")
//    public ResponseEntity<BaseResponse> uploadV3(@RequestParam("file") MultipartFile[] images,
//                                               HttpServletRequest request) throws IOException {
//        BaseResponse response = new BaseResponse();
//        try {
//            List<String> urls = new ArrayList<>();
//            for (MultipartFile image : images) {
//                String finalFileName = service.save(image, "udev.com.vn");
//                String url = String.format("https://udev.com.vn/static/upload/%s", finalFileName);
//                urls.add(url);
//            }
//            LOGGER.info(urls);
//            response.setData(new UploadZaloResponse(urls));
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//        } catch (Exception e) {
//            response.setError(ErrorCode.FAILED);
//            response.setMessage(e.getMessage());
//            return ResponseEntity.ok(response);
//        }
//    }

    @PostMapping("/upload")
    public ResponseEntity<BaseResponse> upload(@RequestParam("images") MultipartFile[] images,
                                               HttpServletRequest request) throws IOException {
        BaseResponse response = new BaseResponse();
        try {
            String referer = HttpUtil.getReferer(request);
            if (StringUtils.isEmpty(referer)) {
                referer = request.getHeader("Origin");
            }
            String domain = "udev.com.vn";
            //hard code to test
            if (referer != null && referer.contains("localhost")) {
                referer = "";
            }
            if (!StringUtils.isEmpty(referer)) {
                domain = HttpUtil.getPrivateDomain(referer);
            }
            if (!WhiteListDomain.INSTANCE.hasWhitelist(domain)) {
                throw new Exception("not support for this domain");
            }
            List<UploadImageResponse> rs = new ArrayList<>();
            Map<String, String> files = new HashMap<>();
            for (MultipartFile image : images) {
                String fileName = image.getOriginalFilename();
                try {
                    String realDomain = "udev.com.vn";
                    if (!StringUtils.isEmpty(referer)) {
                        realDomain = HttpUtil.getDomain(referer);
                        if (realDomain == null) {
                            realDomain = domain;
                        }
                        if (WhiteListDomain.ORIGIN_ZALO_MINI_APP.contains(domain)) {
                            realDomain = "knauf.udev.com.vn";
                        }
                    }
                    String finalFileName = service.save(image, realDomain);
                    UploadImageResponse uploadImageResponse = new UploadImageResponse();
                    uploadImageResponse.setName(fileName);
                    uploadImageResponse.setUrl(String.format("https://%s/static/upload/%s", realDomain, finalFileName));

                    if ("knauf.udev.com.vn".equals(realDomain)
                            || "udev.com.vn".equals(realDomain)) {
                        InputStream inputStream = image.getInputStream();
                        uploadImageResponse.setChecksum(service.getChecksum(inputStream));
                    }
                    rs.add(uploadImageResponse);
                } catch (Exception e) {
                    files.put(fileName, "failed");
                }
            }
            response.setData(rs);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.setError(ErrorCode.FAILED);
            response.setMessage(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    public static class UploadImageResponse {
        private String name;
        private String url;
        private String checksum;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getChecksum() {
            return checksum;
        }

        public void setChecksum(String checksum) {
            this.checksum = checksum;
        }
    }

    public static class UploadZaloResponse {
        private List<String> urls;


        public UploadZaloResponse(List<String> urls) {
            this.urls = urls;
        }

        public List<String> getUrls() {
            return urls;
        }

        public void setUrls(List<String> urls) {
            this.urls = urls;
        }
    }
}
