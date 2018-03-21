package project;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

class Pixel {
  BufferedImage image;
  int width;
  int height;
 
  public Pixel() {
  }
 
  public void getPixels() {
       try {
             File input = getFile();
             image = ImageIO.read(input);
             width = image.getWidth();
             height = image.getHeight();
            
             int count = 0;
            
             for(int i=0; i<height; i++){
            
                for(int j=0; j<width; j++){
                
                   count++;
                   Color c = new Color(image.getRGB(j, i));
                   System.out.println("S.No: " + count + " Red: " + c.getRed() +"  Green: " + c.getGreen() + " Blue: " + c.getBlue());
                }
             }
            
          } catch (Exception e) {
              
          }
          }
 


  public void grayscale() {
      BufferedImage  image;
      int width;
      int height;
      
     try {
        File input = getFile();
        image = ImageIO.read(input);
        width = image.getWidth();
        height = image.getHeight();
       
        for(int i=0; i<height; i++){
       
           for(int j=0; j<width; j++){
           
              Color c = new Color(image.getRGB(j, i));
              int red = (int)(c.getRed() * 0.299);
              int green = (int)(c.getGreen() * 0.587);
              int blue = (int)(c.getBlue() *0.114);
              Color newColor = new Color(red+green+blue,
             
              red+green+blue,red+green+blue);
             
              image.setRGB(j,i,newColor.getRGB());
           }
        }
       
        File ouptut = new File("grayscale.jpg");
        ImageIO.write(image, "jpg", ouptut);
       
     } catch (Exception e) {
        
     }
}


 
  // RETURNS A FILE
    public File getFile() {
        File selected = null;
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(null);
        if(option == JFileChooser.APPROVE_OPTION) {
            selected = chooser.getSelectedFile();
        }
        return selected;
    }
 
  public static void main(String args[]) throws Exception
  {
     Pixel obj = new Pixel();
     obj.grayscale();
     
  }
}