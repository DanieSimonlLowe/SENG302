package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.exceptions.ImageSizeException;
import nz.ac.canterbury.seng302.tab.exceptions.ImageTypeException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Custom ImageHandler class, to allow for handling images.
 */
public class ImageHandler {

    /** The allowed types of images to upload */
    private static final List<String> allowedExtension = Arrays.asList("jpg", "jpeg", "png", "svg");

    /** The allowed types of images to upload */
    private static final List<String> allowedAttachmentExtension = Arrays.asList("jpg", "jpeg", "png", "svg", "mp4", "webm", "ogg");

    /** Stores the current working directory in a constant */
    private static final String USER_DIR = System.getProperty("user.dir");

    /** String constant for a dot */
    private static final String DOT = ".";


    /**
     * Saves an uploaded image
     * @param file the file which is being uploaded
     * @param subDirectory the which the file will go in to
     * @return the file name of the newly created image
     * */
    public static String uploadImage(MultipartFile file, String fileName, String subDirectory) throws ImageSizeException, ImageTypeException, IOException {
        Path root = Path.of(USER_DIR + File.separator + "public" + File.separator + subDirectory);
        Files.createDirectories(root);

        String fileType = file.getContentType();

        if (fileType == null) { throw new ImageTypeException(); }
        if (file.getSize() >= 10500000) { throw new ImageSizeException(); }
        String extension = fileType.substring(fileType.lastIndexOf("/") + 1);
        if (extension.equals("svg+xml")) { extension = "svg"; }
        if (!allowedExtension.contains(extension.toLowerCase())) { throw new ImageTypeException(); }

        Files.write(Path.of(root + File.separator + fileName + DOT + extension), file.getBytes());
        return subDirectory + File.separator + fileName + DOT + extension;
    }

    public static String uploadAttachment(MultipartFile file, String fileName, String subDirectory) throws ImageSizeException, ImageTypeException, IOException {
        Path root = Path.of(USER_DIR + File.separator + "public" + File.separator + subDirectory);
        Files.createDirectories(root);

        String fileType = file.getContentType();

        if (fileType == null) { throw new ImageTypeException(); }
        if (file.getSize() >= 10500000) { throw new ImageSizeException(); }
        String extension = fileType.substring(fileType.lastIndexOf("/") + 1);
        if (extension.equals("svg+xml")) { extension = "svg"; }
        if (!allowedAttachmentExtension.contains(extension.toLowerCase())) { throw new ImageTypeException(); }

        Files.write(Path.of(root + File.separator + fileName + DOT + extension), file.getBytes());
        return subDirectory + File.separator + fileName + DOT + extension;
    }
}
