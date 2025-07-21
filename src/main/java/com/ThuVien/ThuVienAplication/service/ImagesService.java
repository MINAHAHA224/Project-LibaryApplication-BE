package com.ThuVien.ThuVienAplication.service;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImagesService {
    private final ServletContext servletContext;

    public String handleUploadFile(MultipartFile file, String targetFolder) {
        if (file.isEmpty()) {
            return "";
        }
        String rootPath ="D:\\HeQuanTri_Lop";
        String finalName = "";
        try {
            byte[] bytes;
            bytes = file.getBytes();

            File dir = new File(rootPath + File.separator + targetFolder);
            if (!dir.exists())
                dir.mkdirs();

            finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            File serverFile = new File(dir.getAbsolutePath() + File.separator + finalName);

            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return finalName;
    }

}
