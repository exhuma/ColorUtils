package lu.albert.colorutils;

import java.awt.Color;
import java.util.logging.Logger;

/**
 * This class provides a few static methods useful for color accessibility.
 *
 * @author malbert
 */
public class ColorA11Y {

   // the class logger
   private final static Logger logger = Logger.getLogger(ColorA11Y.class.getCanonicalName());

   /**
    * Supported calculation algorithms
    */
   public static enum Method {
      W3C,
      Luminosity,
      LuminosityContrast
   }


   /**
    * Determines whether two colors are considered readable if one of them is
    * the background, the other the foreground.
    *
    * @param color_a The first color
    * @param color_b The second color
    * @param method The method used to calculate the difference score
    * @return true if the colors are a good mix, false otherwise
    */
   public static boolean isGoodColorMix(Color color_a, Color color_b, Method method){
      float lum_a;
      float lum_b;
      float lum_n;
      switch(method){
         default:
         case W3C:
            return (W3CColorDifference(color_a, color_b) >= 500);
         case Luminosity:
            lum_a = ColorMath.getLuminosity(color_a);
            lum_b = ColorMath.getLuminosity(color_b);
            double ratio = Math.min(lum_a, lum_b) / Math.max(lum_a, lum_b);
            return ratio <= (1.0/10.0);
         case LuminosityContrast:
            lum_a = ColorMath.getLuminosity(color_a);
            lum_b = ColorMath.getLuminosity(color_b);
            lum_n = 255.0f;
            double delta = 116.0 * Math.pow(
                    (Math.abs(lum_a - lum_b)/lum_n), 1.0/3.0);
            return delta > 100.0;

      }
   }

   /**
    * Find the best complementary color for text readability.
    *
    * @param color A color
    * @return the "best" complementary color
    */
   public static Color getReadableComplement(Color color) {
      Color complement = ColorMath.getComplement(color);
      int[] textHSL = new int[3];
      ColorMath.getHSL(complement, textHSL);
      float lumin = ColorMath.getLuminosity(color);
      logger.fine(String.format("Base color luminance: %3.2f", lumin));

      while (!isGoodColorMix(color, complement, Method.LuminosityContrast)
              && textHSL[2] < 100
              && textHSL[2] > 0) {

         if (lumin < 128f) {
            textHSL[2] += 1;
         } else {
            textHSL[2] -= 1;
         }
         logger.finest(String.format("Adjusting HSL: h=%3d s=%3d l=%3d",
                 textHSL[0], textHSL[1], textHSL[2]));
         complement = ColorMath.fromHSL(textHSL);
      }
      return complement;
   }

   /**
    * Color difference as defined <a href="http://www.w3.org/TR/AERT#color-contrast>by W3c</a>
    *
    * @param a First color
    * @param b Second color
    * @return
    */
   private static int W3CColorDifference(Color a, Color b){
      int r1 = a.getRed();
      int g1 = a.getGreen();
      int b1 = a.getBlue();
      int r2 = b.getRed();
      int g2 = b.getGreen();
      int b2 = b.getBlue();
      return Math.abs(r1-r2) + Math.abs(g1-g2) + Math.abs(b1-b2);
   }

}
