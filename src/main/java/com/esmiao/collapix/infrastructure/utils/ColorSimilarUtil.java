package com.esmiao.collapix.infrastructure.utils;

import java.awt.*;

/**
 * Picture color similar calculate tool
 * @author Steven Chen
 */
public class ColorSimilarUtil {

    private ColorSimilarUtil() {
    }

    /**
     * Calculate the similarity between two colors
     *
     * @param color1 The first color
     * @param color2 The second color
     * @return Similarity (between 0 and 1, 1 means completely identical)
     */
    public static double calculateSimilarity(Color color1, Color color2) {
        int r1 = color1.getRed();
        int g1 = color1.getGreen();
        int b1 = color1.getBlue();

        int r2 = color2.getRed();
        int g2 = color2.getGreen();
        int b2 = color2.getBlue();

        // Calculate Euclidean distance
        double distance = Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));

        // Calculate similarity
        return 1 - distance / Math.sqrt(3 * Math.pow(255, 2));
    }

    /**
     * Calculate similarity based on hexadecimal color codes
     *
     * @param hexColor1 Hexadecimal code of the first color (e.g., 0xFF0000)
     * @param hexColor2 Hexadecimal code of the second color (e.g., 0xFE0101)
     * @return Similarity (between 0 and 1, 1 means completely identical)
     */
    public static double calculateSimilarity(String hexColor1, String hexColor2) {
        Color color1 = Color.decode(hexColor1);
        Color color2 = Color.decode(hexColor2);
        return calculateSimilarity(color1, color2);
    }

    // Example code
    public static void main(String[] args) {
        // Test colors
        Color color1 = Color.decode("0xFF0000");
        Color color2 = Color.decode("0xFE0101");
        double similarity = calculateSimilarity(color1, color2);

        System.out.println("Color similarity is: " + similarity);

        // Test hexadecimal method
        double hexSimilarity = calculateSimilarity("0xFF0000", "0xFE0101");
        System.out.println("Hexadecimal color similarity is: " + hexSimilarity);
    }
}