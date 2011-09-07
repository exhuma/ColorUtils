package lu.albert.colorutils;

import java.awt.Color;
import java.util.logging.Logger;

/**
 *
 * @author malbert
 */
public class ColorMath {

   private static final Logger logger = Logger.getLogger(ColorMath.class.getCanonicalName());

   /**
    * Return the complementary color
    *
    * @return
    */
   public static Color getComplement(Color input) {
      int[] hsl = new int[3];
      int[] rgb = new int[3];
      rgb2hsl(input.getRed(), input.getGreen(), input.getBlue(), hsl);
      int new_h = hsl[0] + 180;
      if (new_h >= 360) {
         new_h -= 360;
      }
      hsl2rgb(new_h, hsl[1], hsl[2], rgb);
      logger.fine(String.format("Complement r=%d g=%s b=%s",
              rgb[0], rgb[1], rgb[2]));
      try {
         return new Color(rgb[0], rgb[1], rgb[2]);
      } catch (IllegalArgumentException exc){
         System.err.println(exc.getMessage());
         return Color.BLACK;
      }
   }

   public static String toHex(Color color){
      return String.format("#%02x%02x%02x",
              color.getRed(),
              color.getGreen(),
              color.getBlue());
   }

   /**
    * Convert RGB to HSL components H ranges from 0-360, S and L ranges from 0 to 100
    * @param r Red
    * @param g Green
    * @param b Blue
    * @param hsl This array will be assigned the HSL values (3 ints)
    */
   public static void rgb2hsl(int r, int g, int b, int hsl[]) {

      float red = (r / 255f);
      float green = (g / 255f);
      float blue = (b / 255f);
      logger.fine(String.format("Converting r=%1.3f, g=%1.3f, b=%1.3f to HSL",
              red, green, blue));

      float min = Math.min(red, Math.min(green, blue));
      float max = Math.max(red, Math.max(green, blue));
      float delta;

      delta = max - min;

      float h = 0;
      float s;
      float l;

      l = (max + min) / 2f;

      if (delta == 0) {
         h = 0;
         s = 0;
      } else {
         if (l < 0.5) {
            s = delta / (max + min);
         } else {
            s = delta / (2 - max - min);
         }

         float delta_r = (((max - red) / 6f) + (delta / 2f)) / delta;
         float delta_g = (((max - green) / 6f) + (delta / 2f)) / delta;
         float delta_b = (((max - blue) / 6f) + (delta / 2f)) / delta;

         if (red == max) {
            h = delta_b - delta_g;
         } else if (green == max) {
            h = (1 / 3f) + delta_r - delta_b;
         } else if (blue == max) {
            h = (2 / 3f) + delta_g - delta_r;
         }
         if (h < 0) {
            h += 1;
         }
         if (h > 1) {
            h -= 1;
         }
      }
      hsl[0] = Math.round(360 * h);
      hsl[1] = Math.round(s * 100);
      hsl[2] = Math.round(l * 100);
      logger.fine(String.format("Result of rgb2hsl: h=%d, s=%d, l=%d",
              hsl[0], hsl[1], hsl[2]));
   }

   /**
    * Convert HSL values to RGB
    *
    * @param h
    * @param s
    * @param x
    * @param rgb This array will be assigned the RGB values (3 ints)
    */
   public static void hsl2rgb(int h, int s, int l, int rgb[]) {

      float hue = h / 360f;
      float saturation = s / 100f;
      float lightness = l / 100f;
      float r;
      float g;
      float b;
      float tmp1;
      float tmp2;

      logger.fine(String.format("Converting h=%1.3f, s=%1.3f, l=%1.3f to RGB",
              hue, saturation, lightness));

      if (saturation == 0) {
         r = lightness * 255f;
         g = lightness * 255f;
         b = lightness * 255f;
      } else {
         if (lightness < 0.5) {
            tmp2 = lightness * (1 + saturation);
         } else {
            tmp2 = (lightness + saturation) - (saturation * lightness);
         }

         tmp1 = 2 * lightness - tmp2;

         r = 255 * hue2rgb(tmp1, tmp2, hue + (1f / 3f));
         g = 255 * hue2rgb(tmp1, tmp2, hue);
         b = 255 * hue2rgb(tmp1, tmp2, hue - (1f / 3f));
      }

      rgb[0] = Math.round(r);
      rgb[1] = Math.round(g);
      rgb[2] = Math.round(b);
      logger.fine(String.format("Result of hsl2rgb: r=%d, g=%d, b=%d",
              rgb[0], rgb[1], rgb[2]));

   }

   /**
    * Convert a hue value into the appropriate RGB value
    * @see <a href="http://www.easyrgb.com/index.php?X=MATH&H=19#text19">EasyRGB</a>
    *
    * @param v1 ?
    * @param v2 ?
    * @param vH ?
    * @return
    */
   public static float hue2rgb(float v1, float v2, float vH) {
      if (vH < 0) {
         vH += 1;

      }
      if (vH > 1) {
         vH -= 1;

      }
      if ((6f * vH) < 1) {
         return (v1 + (v2 - v1) * 6f * vH);

      }
      if ((2f * vH) < 1) {
         return (v2);

      }
      if ((3f * vH) < 2) {
         return (v1 + (v2 - v1) * ((2f / 3f) - vH) * 6);

      }
      return v1;
   }

   /**
    * Convenience method to return a color given a HSL array;
    * @param hsl
    * @return
    */
   public static Color fromHSL(int[] hsl){
      int[] rgb = new int[3];
      hsl2rgb(hsl[0], hsl[1], hsl[2], rgb);
      return new Color(rgb[0], rgb[1], rgb[2]);
   }

   /**
    * Creates an HSL array from a given color
    * @param color the input color
    * @param hsl the HSL triple
    */
   public static void getHSL(Color color, int[] hsl) {
      rgb2hsl(color.getRed(), color.getGreen(), color.getBlue(), hsl);
   }

   /**
    * Returns the relative luminosity (luminance) of the given color. The value ranges from 0-255.
    * The weights were taken from http://en.wikipedia.org/wiki/Luminance_(relative)
    *
    * @param color
    * @return
    */
   public static float getLuminosity(Color color) {
      return 0.2126f * color.getRed() + 0.7152f * color.getGreen() + 0.0722f * color.getBlue();
   }

   /**
    * Disambiguation method for getLuminosity.
    *
    * @param color
    * @return
    * @see #getLuminosity(java.awt.Color) 
    */
   public static float getLuminance(Color color) {
      return getLuminosity(color);
   }

   // ######################################################################
   // The following code has been nicked from
   // http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
   // They will be adapted to be more usable
   void rgb2ycbcr(int r, int g, int b, int[] ycbcr) {
      int y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
      int cb = (int) (-0.16874 * r - 0.33126 * g + 0.50000 * b);
      int cr = (int) (0.50000 * r - 0.41869 * g - 0.08131 * b);

      ycbcr[0] = y;
      ycbcr[1] = cb;
      ycbcr[2] = cr;
   }

   void rgb2yuv(int r, int g, int b, int[] yuv) {
      int y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
      int u = (int) ((b - y) * 0.492f);
      int v = (int) ((r - y) * 0.877f);

      yuv[0] = y;
      yuv[1] = u;
      yuv[2] = v;
   }

   void rgb2hsb(int r, int g, int b, int[] hsb) {
      float[] hsbvals = new float[3];
      Color.RGBtoHSB(r, g, b, hsbvals);
   }

   void rgb2hmmd(int r, int g, int b, int[] hmmd) {

      float max = (int) Math.max(Math.max(r, g), Math.max(g, b));
      float min = (int) Math.min(Math.min(r, g), Math.min(g, b));
      float diff = (max - min);
      float sum = (float) ((max + min) / 2.);

      float hue = 0;
      if (diff == 0) {
         hue = 0;
      } else if (r == max && (g - b) > 0) {
         hue = 60 * (g - b) / (max - min);
      } else if (r == max && (g - b) <= 0) {
         hue = 60 * (g - b) / (max - min) + 360;
      } else if (g == max) {
         hue = (float) (60 * (2. + (b - r) / (max - min)));
      } else if (b == max) {
         hue = (float) (60 * (4. + (r - g) / (max - min)));
      }

      hmmd[0] = (int) (hue);
      hmmd[1] = (int) (max);
      hmmd[2] = (int) (min);
      hmmd[3] = (int) (diff);
   }

   private void rgb2hsv(int r, int g, int b, int hsv[]) {

      int min;    //Min. value of RGB
      int max;    //Max. value of RGB
      int delMax; //Delta RGB value

      if (r > g) {
         min = g;
         max = r;
      } else {
         min = r;
         max = g;
      }
      if (b > max) {
         max = b;
      }
      if (b < min) {
         min = b;
      }

      delMax = max - min;

      float H = 0, S;
      float V = max;

      if (delMax == 0) {
         H = 0;
         S = 0;
      } else {
         S = delMax / 255f;
         if (r == max) {
            H = ((g - b) / (float) delMax) * 60;
         } else if (g == max) {
            H = (2 + (b - r) / (float) delMax) * 60;
         } else if (b == max) {
            H = (4 + (r - g) / (float) delMax) * 60;
         }
      }

      hsv[0] = (int) (H);
      hsv[1] = (int) (S * 100);
      hsv[2] = (int) (V * 100);
   }

   public void rgb2xyY(int R, int G, int B, int[] xyy) {
      //http://www.brucelindbloom.com

      float rf, gf, bf;
      float r, g, b, X, Y, Z;

      // RGB to XYZ
      r = R / 255.f; //R 0..1
      g = G / 255.f; //G 0..1
      b = B / 255.f; //B 0..1

      if (r <= 0.04045) {
         r = r / 12;
      } else {
         r = (float) Math.pow((r + 0.055) / 1.055, 2.4);
      }

      if (g <= 0.04045) {
         g = g / 12;
      } else {
         g = (float) Math.pow((g + 0.055) / 1.055, 2.4);
      }

      if (b <= 0.04045) {
         b = b / 12;
      } else {
         b = (float) Math.pow((b + 0.055) / 1.055, 2.4);
      }

      X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
      Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
      Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

      float x;
      float y;

      float sum = X + Y + Z;
      if (sum != 0) {
         x = X / sum;
         y = Y / sum;
      } else {
         float Xr = 0.964221f;  // reference white
         float Yr = 1.0f;
         float Zr = 0.825211f;

         x = Xr / (Xr + Yr + Zr);
         y = Yr / (Xr + Yr + Zr);
      }

      xyy[0] = (int) (255 * x + .5);
      xyy[1] = (int) (255 * y + .5);
      xyy[2] = (int) (255 * Y + .5);

   }

   public void rgb2xyz(int R, int G, int B, int[] xyz) {
      float rf, gf, bf;
      float r, g, b, X, Y, Z;

      r = R / 255.f; //R 0..1
      g = G / 255.f; //G 0..1
      b = B / 255.f; //B 0..1

      if (r <= 0.04045) {
         r = r / 12;
      } else {
         r = (float) Math.pow((r + 0.055) / 1.055, 2.4);
      }

      if (g <= 0.04045) {
         g = g / 12;
      } else {
         g = (float) Math.pow((g + 0.055) / 1.055, 2.4);
      }

      if (b <= 0.04045) {
         b = b / 12;
      } else {
         b = (float) Math.pow((b + 0.055) / 1.055, 2.4);
      }

      X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
      Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
      Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

      xyz[1] = (int) (255 * Y + .5);
      xyz[0] = (int) (255 * X + .5);
      xyz[2] = (int) (255 * Z + .5);
   }

   public void rgb2lab(int R, int G, int B, int[] lab) {
      //http://www.brucelindbloom.com

      float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
      float Ls, as, bs;
      float eps = 216.f / 24389.f;
      float k = 24389.f / 27.f;

      float Xr = 0.964221f;  // reference white D50
      float Yr = 1.0f;
      float Zr = 0.825211f;

      // RGB to XYZ
      r = R / 255.f; //R 0..1
      g = G / 255.f; //G 0..1
      b = B / 255.f; //B 0..1

      // assuming sRGB (D65)
      if (r <= 0.04045) {
         r = r / 12;
      } else {
         r = (float) Math.pow((r + 0.055) / 1.055, 2.4);
      }

      if (g <= 0.04045) {
         g = g / 12;
      } else {
         g = (float) Math.pow((g + 0.055) / 1.055, 2.4);
      }

      if (b <= 0.04045) {
         b = b / 12;
      } else {
         b = (float) Math.pow((b + 0.055) / 1.055, 2.4);
      }


      X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
      Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
      Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

      // XYZ to Lab
      xr = X / Xr;
      yr = Y / Yr;
      zr = Z / Zr;

      if (xr > eps) {
         fx = (float) Math.pow(xr, 1 / 3.);
      } else {
         fx = (float) ((k * xr + 16.) / 116.);
      }

      if (yr > eps) {
         fy = (float) Math.pow(yr, 1 / 3.);
      } else {
         fy = (float) ((k * yr + 16.) / 116.);
      }

      if (zr > eps) {
         fz = (float) Math.pow(zr, 1 / 3.);
      } else {
         fz = (float) ((k * zr + 16.) / 116);
      }

      Ls = (116 * fy) - 16;
      as = 500 * (fx - fy);
      bs = 200 * (fy - fz);

      lab[0] = (int) (2.55 * Ls + .5);
      lab[1] = (int) (as + .5);
      lab[2] = (int) (bs + .5);
   }

   public void rgb2luv(int R, int G, int B, int[] luv) {
      //http://www.brucelindbloom.com

      float rf, gf, bf;
      float r, g, b, X_, Y_, Z_, X, Y, Z, fx, fy, fz, xr, yr, zr;
      float L;
      float eps = 216.f / 24389.f;
      float k = 24389.f / 27.f;

      float Xr = 0.964221f;  // reference white D50
      float Yr = 1.0f;
      float Zr = 0.825211f;

      // RGB to XYZ

      r = R / 255.f; //R 0..1
      g = G / 255.f; //G 0..1
      b = B / 255.f; //B 0..1

      // assuming sRGB (D65)
      if (r <= 0.04045) {
         r = r / 12;
      } else {
         r = (float) Math.pow((r + 0.055) / 1.055, 2.4);
      }

      if (g <= 0.04045) {
         g = g / 12;
      } else {
         g = (float) Math.pow((g + 0.055) / 1.055, 2.4);
      }

      if (b <= 0.04045) {
         b = b / 12;
      } else {
         b = (float) Math.pow((b + 0.055) / 1.055, 2.4);
      }


      X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
      Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
      Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

      // XYZ to Luv

      float u, v, u_, v_, ur_, vr_;

      u_ = 4 * X / (X + 15 * Y + 3 * Z);
      v_ = 9 * Y / (X + 15 * Y + 3 * Z);

      ur_ = 4 * Xr / (Xr + 15 * Yr + 3 * Zr);
      vr_ = 9 * Yr / (Xr + 15 * Yr + 3 * Zr);

      yr = Y / Yr;

      if (yr > eps) {
         L = (float) (116 * Math.pow(yr, 1 / 3.) - 16);
      } else {
         L = k * yr;
      }

      u = 13 * L * (u_ - ur_);
      v = 13 * L * (v_ - vr_);

      luv[0] = (int) (2.55 * L + .5);
      luv[1] = (int) (u + .5);
      luv[2] = (int) (v + .5);
   }
}
