package vn.com.udev.cdn.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class StorageService {
    private static final String ROOT_FOLDER = "/var/www";
    private static final Tika TIKA = new Tika();
    private Map<String, Path> MAP_ROOT_PATH = new HashMap<>();

    public String save(MultipartFile file, String domain) throws Exception {
        String fileName = retrieveName(file);
        Path rootPath = getRootPath(domain);
        Files.copy(file.getInputStream(), rootPath.resolve(fileName));
        return fileName;
    }

    public Path getRootPath(String domain) throws IOException {
        domain = domain.replaceAll("www.", "");
        String rootPath = ROOT_FOLDER + "/" + domain + "/html/static/upload/";
        Path root = MAP_ROOT_PATH.getOrDefault(domain, Paths.get(rootPath));
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
        return root;
    }

    private String retrieveName(MultipartFile file) throws Exception {
        long timeMillis = System.currentTimeMillis();
        String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
        fileNameWithOutExt = fileNameWithOutExt.replaceAll( " ", "-");
        String mimeType = TIKA.detect(file.getInputStream());
        String extension;
        switch (mimeType) {
            case "image/png":
                extension = "png";
                break;
            case "image/jpeg":
                extension = "jpeg";
                break;
            case "image/heic":
                extension = "heic";
                break;
            default:
                throw new Exception("not support mimeType: " + mimeType);
        }
        return String.format("%s-%d.%s", fileNameWithOutExt, timeMillis, extension);
    }

    public String getChecksum(InputStream stream) throws IOException {
        return DigestUtils.md5Hex(stream);
    }

}
