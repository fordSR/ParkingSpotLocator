package project;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageTool {
      public static void toBlackAndWhite(BufferedImage img) throws IOException {
          toBlackAndWhite(img, 50);
      }
      public static void toBlackAndWhite(BufferedImage img, int precision) throws IOException {
          int w = img.getWidth();
          int h = img.getHeight();

          precision = (0 <= precision && precision <= 100) ? precision : 50;

          int limit = 255 * precision / 100;

          for(int i = 0, j; i < w; ++i) {
              for(j = 0; j < h; ++j) {
                  Color color = new Color(img.getRGB(i, j));
                  if(limit <= color.getRed() || limit <= color.getGreen() || limit <= color.getBlue()) {
                      img.setRGB(i, j, Color.WHITE.getRGB());
                  } else {
                      img.setRGB(i, j, Color.BLACK.getRGB());
                  }
              }
          }
//           for(Integer i : new Integer[] {0, 30, 70, 100}) {
//                BufferedImage image = ImageIO.read(new File(getFile()));
//                ImageTool.toBlackAndWhite(img, i);
//                ImageIO.write(img, "png", new File("out_" + i + ".png"));
//            }
    
  }
}