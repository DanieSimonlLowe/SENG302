package nz.ac.canterbury.seng302.tab.service;


import nz.ac.canterbury.seng302.tab.exceptions.ImageSizeException;
import nz.ac.canterbury.seng302.tab.exceptions.ImageTypeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;


public class ImageHandlerTest {

    static final String fileDir = "dir";
    static final String fileNameStart = "start";

    @Test
    void uploadFileToLarge() {
        MultipartFile file = Mockito.spy(MultipartFile.class);
        Mockito.doReturn("name.jpg").when(file).getOriginalFilename();
        Mockito.doReturn(10500000L).when(file).getSize();
        Mockito.doReturn("image/jpg").when(file).getContentType();
        Assertions.assertThrows(ImageSizeException.class, () -> ImageHandler.uploadImage(file,fileNameStart,fileDir));

    }

    @Test
    void uploadFileNoExt() {
        MultipartFile file = Mockito.spy(MultipartFile.class);
        Mockito.doReturn("name").when(file).getOriginalFilename();
        Mockito.doReturn(10000000L).when(file).getSize();
        Mockito.doReturn(null).when(file).getContentType();
        Assertions.assertThrows(ImageTypeException.class, () -> ImageHandler.uploadImage(file,fileNameStart,fileDir));

    }

    @Test
    void uploadFileNotAllowedExt() {
        MultipartFile file = Mockito.spy(MultipartFile.class);
        Mockito.doReturn("name.blob").when(file).getOriginalFilename();
        Mockito.doReturn(10000000L).when(file).getSize();
        Mockito.doReturn("image/blob").when(file).getContentType();
        Assertions.assertThrows(ImageTypeException.class, () -> ImageHandler.uploadImage(file,fileNameStart,fileDir));
    }

}
