package Mosaic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Greg on 11/11/16.
 *
 * This class handles the dirty work of reading and saving image files.
 */
public class FileManager {

    // Read and write directories
    public static final String HOME_DIRECTORY = System.getProperties().getProperty("user.home"); // Gets "Users/YourName/"
    public static final String SAVE_DIRECTORY = HOME_DIRECTORY + "/Desktop/Mosaics";
    public static final String READ_DIRECTORY = HOME_DIRECTORY + "/Desktop/Source Images";


    /**
     * Retrieves a list of all eligible images from a directory.
     *
     * @param directory - the directory's path, for example "/Users/Greg/Desktop/Source Images"
     * @return an ArrayList of all the images in the directory, converted to BufferedImages
     */
    public ArrayList<BufferedImage> getImagesFromDirectory(String directory){

        // Get a list of all the files in the directory, if it exists
        File sourceDirectory = new File(directory);
        File[] fileList = sourceDirectory.listFiles();
        ArrayList<BufferedImage> images = new ArrayList<>();

        // Handle invalid directories
        if (fileList == null){
            System.out.println("Failed to read from directory - No valid directory at that address");
            return new ArrayList<>();
        }

        // Loop through the files and add the image files to the image list
        for (File file : fileList) {
            String extension = getFileExtension(file);
            if (file.isFile() && (extension.equals("png") || extension.equals("jpg"))) {
                try {
                    images.add(ImageIO.read(file));
                } catch (IOException io){
                    System.out.println("Unable to read file due to IOException.");
                    System.out.println(io.getMessage());
                }
            }
        }

        return images;
    }


    /**
     * Auxiliary method to save an image to the project directory
     * @param image - BufferedImage to print
     * @param filename - Desired name of file
     */
    public void saveImageToFile(BufferedImage image, String filename){

        boolean folderIsCreated = new File(SAVE_DIRECTORY).mkdirs();
        File saveFile = new File(SAVE_DIRECTORY + filename + ".png");

        try {
            ImageIO.write(image, "png", saveFile);
            System.out.println("Saved image to file " + saveFile.getName());
        } catch (IOException io){
            System.out.println("Unable to write to file - IOException");
            System.out.println(io.getMessage());
        }
    }


    /**
     * Gets the file extension of a file, for example "png" or "mp3"
     */
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex == -1 || dotIndex == 0){
            return "";
        } else {
            return fileName.substring(dotIndex + 1);
        }
    }
}
