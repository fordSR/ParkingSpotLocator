package project;

import java.util.List;

import javax.swing.JFileChooser;

import java.io.File;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;


public class ParkingSpotLocator {

	protected static ArrayList<ParkingSpot> original = new ArrayList<ParkingSpot>();
	protected ArrayList<ParkingSpot> spots = new ArrayList<ParkingSpot>();
	ArrayList<ParkingSpot> taken = new ArrayList<ParkingSpot>();

		public void findTakenSpots() {
           System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
           
           Mat source = Imgcodecs.imread(getFile().toString());
           Mat destination = new Mat(source.rows(), source.cols(), source.type());
           Mat contourDest = Mat.zeros(destination.size(), CvType.CV_8UC3);
           
           // Filter the photo //
           // Grayscale
           Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);
           // Blur
          // Imgproc.GaussianBlur(destination, destination, new Size(5, 7), 0, 0, Core.BORDER_DEFAULT);
           // Save blurred image
           //Imgcodecs.imwrite("blur.jpg", destination);
           // CANNY CAUSES OUTER AND INNER CONTOURS.... SO 12 RECTANGLES INSTEAD OF 6 ----COMMENT OUT----
           // Canny the image which kind of detects edges, tbh not really sure what it does
          //Imgproc.Canny(destination, destination, 100, 255);
           // Binary the image into black and white
           Imgproc.threshold(destination, destination, 0, 255, Imgproc.THRESH_BINARY);
           // Thicken/smooth lines
           Imgproc.dilate(destination, destination, new Mat(), new Point(-1, 1), 2);
           // Save "thresholded" image
           Imgcodecs.imwrite("houghtransform.jpg", destination);
           
           // Finds contours //
           // ArrayList of contours stored as points, I think
           List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
           // Makes copy of thresholded image
           Mat contourImage = destination.clone();
           Imgproc.findContours(contourImage, contours,new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
           Imgproc.drawContours(contourDest, contours, -1, new Scalar(255, 255, 255));
           // Saves contoured image to disk
           Imgcodecs.imwrite("contours.jpg", contourDest);
           Mat img_copy = contourDest.clone();
         

           for(int i = 0; i < contours.size(); i++){
               Rect r = Imgproc.boundingRect(contours.get(i));
               if(r.area() >= 2000 && ((r.height / (double) r.width) > 1.0)) {
            	   Imgproc.rectangle(img_copy, r.tl(), r.br(), new Scalar(0, 0, 255), 3); 
            	   spots.add(new ParkingSpot(r.x, r.y, r.x + r.width, r.y + r.height));
               }
           
           }
           // if greater than x1 and less than x2, and greater than y1 and less than y2
           // COMPARES THE ARRAYLIST OF ORIGINAL RECTANGLES WITH THE ARRAYLIST OF THE NEW IMAGE RECTANGLES TO SEE IF THERE IS A SPOT TAKEN
           int spotFound = 0;
           for(int i = 0; i < spots.size(); i++) {
        	   for(int j = 0; j < original.size(); j++) {
        		   if(original.get(j).getX1() < spots.get(i).getX1() && original.get(j).getX2() > spots.get(i).getX2() && original.get(j).getY1() < spots.get(i).getY1() && original.get(j).getY2() > spots.get(i).getY2()) {
        			   spotFound++;
        			   taken.add(original.get(j));
        			   System.out.println("Parking spot " + original.get(j).spotNumber + " is taken");
        		   }
        	   }
           }
           Imgcodecs.imwrite("img_copy.jpg", img_copy);
		}
		public void originalPicture() {
	           System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	           
	           Mat source = Imgcodecs.imread(getFile().toString());
	           Mat destination = new Mat(source.rows(), source.cols(), source.type());
	           Mat contourDest = Mat.zeros(destination.size(), CvType.CV_8UC3);
	           
	           // Filter the photo //
	           // Grayscale
	           Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);
	          
	           // CANNY CAUSES OUTER AND INNER CONTOURS.... SO 12 RECTANGLES INSTEAD OF 6 ----COMMENT OUT----
	           // Canny the image which kind of detects edges, tbh not really sure what it does
	          //Imgproc.Canny(destination, destination, 100, 255);
	           // Binary the image into black and white
	           Imgproc.threshold(destination, destination, 0, 255, Imgproc.THRESH_BINARY);
	           // Thicken/smooth lines
	           Imgproc.dilate(destination, destination, new Mat(), new Point(-1, 1), 2);
	           // Save "thresholded" image
	           Imgcodecs.imwrite("houghtransform.jpg", destination);
	           
	           // Finds contours //
	           // ArrayList of contours stored as points, I think
	           List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	           // Makes copy of thresholded image
	           Mat contourImage = destination.clone();
	           Imgproc.findContours(contourImage, contours,new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
	           Imgproc.drawContours(contourDest, contours, -1, new Scalar(255, 255, 255));
	           // Saves contoured image to disk
	           Imgcodecs.imwrite("contours.jpg", contourDest);
	           Mat img_copy = contourDest.clone();
	          

	           for(int i = 0; i < contours.size(); i++){
	               Rect r = Imgproc.boundingRect(contours.get(i));
	               if(r.area() >= 2000 && ((r.height / (double) r.width) > 1.0)) {
	            	   Imgproc.rectangle(img_copy, r.tl(), r.br(), new Scalar(0, 0, 255), 3); 
	            	   original.add(new ParkingSpot(r.x, r.y, r.x + r.width, r.y + r.height));
	               }
	           
	           }
	           
	           // OPENCV CREATES RECTANGLES FROM BOTTOM UP, SO SPOTS ARE NUMBER 6-1 INSTEAD OF 1-6
	           // THIS JUST RENAMES SPOTS IN NUMERICAL ORDER
	           int count = 0;
	           for(int i = original.size() - 1; i >= 0; i--) {
	        	   count++;
	        	   original.get(i).setSpotNumber(count);
	           }

	           Imgcodecs.imwrite("img_copy.jpg", img_copy);
			}

   
   // RETURNS A FILE
	public static File getFile() {
		File selected = null;
		JFileChooser chooser = new JFileChooser();
		int option = chooser.showOpenDialog(null);
		if(option == JFileChooser.APPROVE_OPTION) {
			selected = chooser.getSelectedFile();
		}
		return selected;
	}
	public static void main(String[] args) {
		ParkingSpotLocator h = new ParkingSpotLocator();
		h.originalPicture();
		h.findTakenSpots();
		
	}
}