package Mosaic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Greg on 11/11/16.
 *
 * This class acts as a simple test environment for the two other classes in the project.
 * In the final version, there will ideally be a GUI instead of this class.
 */
public class ImageMain {

    /*
     * Note - the weird structure of main() and the class constructor is because of calls to "this.getClass().getResources",
     * which requires a non-static context.
     */

    public static void main(String[] args) {
        new ImageMain();
    }

    public ImageMain() {
        try {
            System.out.println("Welcome to Mosaic!");
            main();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void main() throws IOException {

        ImageAnalyzer analyzer = new ImageAnalyzer();
        FileManager fileManager = new FileManager();

        // Read images from a directory
        String directory = FileManager.READ_DIRECTORY;
        ArrayList<BufferedImage> images = fileManager.getImagesFromDirectory(directory);

        // Test reading a single file from an external location
        File testFile = new File("/Users/Greg/Desktop/Source Images/TestImage2.jpg");
        BufferedImage outsideImage = ImageIO.read(testFile);



        // Do a sample mosaic!
        ArrayList<BufferedImage> mappedList = analyzer.generateMappedImageList(images, outsideImage, 10, 10, 80, 100);
        BufferedImage testFinalMosaic = analyzer.drawMosaic(mappedList, 10, 10, 80, 100);

        // Create some Mosaics!
        BufferedImage firstMosaic = analyzer.drawMosaic(images, 4, 4, 500, 750);
        fileManager.saveImageToFile(firstMosaic, "FirstMosaic");

        BufferedImage averageMosaic = analyzer.drawAverageMosaic(images, 4, 4, 500, 750);
        fileManager.saveImageToFile(averageMosaic, "AverageMosaic");

        BufferedImage secondMosaic = analyzer.drawMosaic(images, 10, 10, 500, 750);
        fileManager.saveImageToFile(secondMosaic, "SecondMosaic");

        BufferedImage secondAverageMosaic = analyzer.drawAverageMosaic(images, 10, 10, 500, 750);
        fileManager.saveImageToFile(secondAverageMosaic, "SecondAverageMosaic");
    }



    /**
     * Stuff that used to be in main, can be used to test that all the other methods work
     */
    private void oldTestCode() throws IOException{

        ImageAnalyzer analyzer = new ImageAnalyzer();
        FileManager fileManager = new FileManager();

        // Read images from a directory
        String directory = FileManager.READ_DIRECTORY;
        ArrayList<BufferedImage> images = fileManager.getImagesFromDirectory(directory);

        // Test reading a single file from an external location
        File testFile = new File("/Users/Greg/Desktop/Source Images/TestImage2.jpg");
        BufferedImage outsideImage = ImageIO.read(testFile);

        // Test getting images from within the package
        java.net.URL url = this.getClass().getResource("TestImage1.jpg");
        BufferedImage image = ImageIO.read(url);

        // Use Image Analyzer class to extract pixel colors and average color
        Color[][] imageColors = analyzer.getColorArray(image);
        Color avgColor = analyzer.getAverageColor(image);
    }

}
