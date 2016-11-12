package Mosaic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;

/**
 * Created by Greg on 11/11/16.
 *
 * This class handles the dirty work of the image analysis. It can:
 *
 *  - Draw a tiled mosaic and save it to a file            ->    drawMosaic()
 *  - Draw a mosaic with the average color of each image   ->    drawAverageMosaic()
 *  - Extract an array of the color at every pixel         ->    getColorArray()
 *  - Get the average color of an image                    ->    getAverageColor()
 */

public class ImageAnalyzer {

    // The number of pixels to skip each incrementation when looking for the average color in an image
    private static final int PIXEL_SKIP = 10;


    /**
     * Creates a tiled mosaic of images. Images are scaled to fit the grid.
     *
     * @param images - An ArrayList of the images to draw, in order from left to right, top to bottom.
     * @param gridX - How many images horizontally
     * @param gridY - How many images vertically
     * @param dx - The width of each grid space
     * @param dy - The height of each grid space
     */
    public BufferedImage drawMosaic(ArrayList<BufferedImage> images, int gridX, int gridY, int dx, int dy){

        // Create empty image to draw on
        BufferedImage mosaic = new BufferedImage(gridX * dx, gridY * dy, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = mosaic.createGraphics();

        // Draw stuff
        for (int yIndex = 0; yIndex < gridX; yIndex++){
            for (int xIndex = 0; xIndex < gridX; xIndex++){
                // Get the current image
                int imgIndex = (yIndex * gridX + xIndex) % images.size();  // If no more images, restart from beginning of list
                BufferedImage img = images.get(imgIndex);

                // Scale the image to fit the grid
                AffineTransform transform = new AffineTransform();
                float xscale = (float) dx / (float) img.getWidth();
                float yscale = (float) dy / (float) img.getHeight();
                transform.scale(xscale, yscale);
                BufferedImageOp imgScale = new AffineTransformOp(transform, null);

                // Draw the image to the canvas
                graphics.drawImage(img, imgScale, dx * xIndex, dy * yIndex);
            }
        }

        return mosaic;
    }


    /**
     * Draw a mosaic of the average color of every image.
     * Won't be used in the final product, but can be good for testing.
     */
    public BufferedImage drawAverageMosaic(ArrayList<BufferedImage> images, int gridX, int gridY, int dx, int dy){

        // Create empty image to draw on
        BufferedImage mosaic = new BufferedImage(gridX * dx, gridY * dy, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = mosaic.createGraphics();

        // Draw stuff
        for (int yIndex = 0; yIndex < gridX; yIndex++){
            for (int xIndex = 0; xIndex < gridX; xIndex++){
                // Get the current image
                int imgIndex = (yIndex * gridX + xIndex) % images.size();  // If no more images, restart from start of list
                BufferedImage img = images.get(imgIndex);

                // Get the average color
                Color avg = getAverageColor(img, PIXEL_SKIP);

                // Draw the image to the canvas
                graphics.setColor(avg);
                graphics.fillRect(xIndex * dx, yIndex * dy, dx, dy);
            }
        }

        return mosaic;
    }


    /**
     *
     * !! NOT TESTED !!
     *
     * Test run of a mapping method? Uses trivial selection to pick the best image
     *
     * @param images
     * @param model
     * @param gridX
     * @param gridY
     * @param dx
     * @param dy
     * @return
     */
    /*
    public ArrayList<BufferedImage> generateMappedImageList (ArrayList<BufferedImage> images, BufferedImage model,
                                                             int gridX, int gridY, int dx, int dy){
        // Create empty image to draw on
        BufferedImage mosaic = new BufferedImage(gridX * dx, gridY * dy, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = mosaic.createGraphics();

        // Get the average color of every image
        Color[] avgColors = new Color[images.size()];
        for (int i = 0; i < images.size(); i++){
            avgColors[i] = getAverageColor(images.get(i));
        }

        ArrayList<BufferedImage> mappedList = new ArrayList<>();

        // Loop through the subimages and find the closest image in the source list
        for (int yIndex = 0; yIndex < gridX; yIndex++) {
            for (int xIndex = 0; xIndex < gridX; xIndex++) {

                // Get the current subimage of the model
                BufferedImage subImage = model.getSubimage(xIndex * dx, yIndex * dy, dx, dy);
                Color goalColor = getAverageColor(subImage, PIXEL_SKIP);

                // Find the best source image
                int bestIndex = -1;
                double bestDistance = -1;

                for (int i = 0; i < images.size(); i++){
                    if (colorDistance(goalColor, avgColors[i]) < bestDistance || bestDistance == -1){
                        bestDistance = colorDistance(goalColor, avgColors[i]);
                        bestIndex = i;
                    }
                }

                // Add the best image to the list
                mappedList.add(images.get(bestIndex));
            }
        }

        return mappedList;
    }

    */

    /**
     * Extracts the color of every pixel in an image as an array of Color objects.
     */
    public Color[][] getColorArray(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        // Array to fill with all the pixel colors
        Color[][] pixelColors = new Color[w][h];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                // Get the color of the pixel as an int
                int pixelColorInt = image.getRGB(j, i);

                // Convert it to a color and add it to the array
                pixelColors[i][j] = intToColor(pixelColorInt);
            }
        }

        return pixelColors;
    }


    /**
     * Gets the average color of an image as a Color object, without skipping any pixels
     */
    public Color getAverageColor(BufferedImage image){
        return getAverageColor(image, 1);
    }


    /**
     * Gets the average color of an image, skipping a specified number of pixels to save processing
     */
    public Color getAverageColor(BufferedImage image, int pixelSkip) {
        int w = image.getWidth();
        int h = image.getHeight();
        int sumRed = 0, sumGreen = 0, sumBlue = 0, pixelCount = 0;

        // Get the total of each color component across all the pixels in the image
        for (int i = 0; i < h; i+= pixelSkip) {
            for (int j = 0; j < w; j+= pixelSkip) {

                // Get the red, green, and blue components and add them to the running totals
                int pixel = image.getRGB(j, i);
                sumRed += getRed(pixel);
                sumGreen += getGreen(pixel);
                sumBlue += getBlue(pixel);

                // Increment the pixel count
                pixelCount++;
            }
        }

        // Calculate the average of each color
        int avgRed = sumRed / pixelCount;
        int avgGreen = sumGreen / pixelCount;
        int avgBlue = sumBlue / pixelCount;

        return new Color(avgRed, avgGreen, avgBlue);
    }


    /**
     * Distance formula function for two Colors
     */
    public double colorDistance(Color a, Color b){
        return Math.sqrt(Math.pow(a.getRed() - b.getRed(), 2) + Math.pow(a.getGreen() - b.getGreen(), 2)
                + Math.pow(a.getBlue() - b.getBlue(), 2));
    }




    /*
     * The following auxiliary methods are used to convert the single integer color given by image.getRGB() into
     * usable components.
     */

    private Color intToColor(int color){
        return new Color(getRed(color), getGreen(color), getBlue(color));
    }

    private int getAlpha(int colorInt){
        return (colorInt >> 24) & 0xff;
    }

    private int getRed(int colorInt){
        return (colorInt >> 16) & 0xff;
    }

    private int getGreen(int colorInt){
        return (colorInt >> 8) & 0xff;
    }

    private int getBlue(int colorInt){
        return (colorInt) & 0xff;
    }
}