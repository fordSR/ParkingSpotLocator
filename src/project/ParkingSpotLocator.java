package project;

import java.util.List;
import java.awt.Color;
import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;

public class ParkingSpotLocator {
	
	protected static ArrayList<ParkingSpot> emptyParkingLot = new ArrayList<ParkingSpot>();
	protected ArrayList<ParkingSpot> cars = new ArrayList<ParkingSpot>();
	Mat parkingMask;
	int counter = 0;
	int num = 1;
	String im;


	public void emptyParkingLotProcessing(Mat source) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// uncomment if you want to hardcode filepath of image
		//Mat source = Imgcodecs.imread("parkinglot.jpg");
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		Mat contourDest = Mat.zeros(destination.size(), CvType.CV_8UC3);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		parkingMask = destination;
		Mat s2 = Imgcodecs.imread(im);

		// Filter the empty Parking Lot //
		// Grayscale
		Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);
		// Binary the image into black and white
		Imgproc.threshold(destination, destination, 0, 255, Imgproc.THRESH_BINARY);
		// Thicken/smooth lines
		Imgproc.dilate(destination, destination, new Mat(), new Point(-1, 1), 2);
		// Finds contours //
		Imgproc.findContours(destination.clone(), contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(contourDest, contours, -1, new Scalar(255, 255, 255));
		
		// Populate emptyParkingLot with parking spaces from contours
		for(int i = 0; i < contours.size(); i++){
			Rect r = Imgproc.boundingRect(contours.get(i));
			if(r.area() >= 2000 && ((r.height / (double) r.width) > 1.0)) {
				Imgproc.rectangle(s2, r.tl(), r.br(), new Scalar(0, 255, 0), -1);
				
				ParkingSpot p = new ParkingSpot(r);
				p.setSpotNumber(num);
				num++;
				emptyParkingLot.add(p);
			}
		}

	    Imgcodecs.imwrite("firstResult.png", s2);


		// OPENCV CREATES RECTANGLES FROM BOTTOM UP, SO SPOTS ARE NUMBER 6-1 INSTEAD OF 1-6
		// THIS JUST RENAMES SPOTS IN NUMERICAL ORDER
		int count = 0;
		for(int i = emptyParkingLot.size() - 1; i >= 0; i--) {
			count++;
			emptyParkingLot.get(i).setSpotNumber(count);
		}
	}
	
	// returns a mat which is the image but with lines connected
	public Mat connectPoints(String image) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat source = Imgcodecs.imread(image);
		Mat sourceClone = source.clone();
		im = image;
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		Mat contourDest = Mat.zeros(destination.size(), CvType.CV_8UC3);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		// Filter the empty Parking Lot
		// Grayscale
		Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);
		// Binary the image into black and white
		Imgproc.threshold(destination, destination, 0, 255, Imgproc.THRESH_BINARY);
		// Thicken/smooth lines
		Imgproc.dilate(destination, destination, new Mat(), new Point(-1, 1), 2);

//		parkingMask = destination;
	    Imgcodecs.imwrite("check_" + counter + ".jpg", destination);

		// Finds contours //
		Imgproc.findContours(destination.clone(), contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(contourDest, contours, -1, new Scalar(255, 255, 255));
		for(int i = 0; i < contours.size(); i++){
			// skips first contour because it is not a valid point on rectangle
			//if(i != 0) {
				
			Rect r = Imgproc.boundingRect(contours.get(i));
			// lines that connect rectangle points
			// adds the lines to the original image
			Imgproc.circle(sourceClone, r.br(), 10, new Scalar(0, 0, 255));
			Imgproc.circle(sourceClone, r.tl(), 10, new Scalar(0, 255, 0));
			Imgproc.line(sourceClone, new Point(r.br().x - 5, r.tl().y), new Point(r.tl().x + 5, r.tl().y), new Scalar(255, 255, 255), 6);
			Imgproc.line(sourceClone, new Point(r.tl().x + 5, r.br().y), new Point(r.br().x - 5, r.br().y), new Scalar(255, 255, 255), 6);
//			Imgproc.line(sourceClone, new Point(r.br().x - 18, r.tl().y), new Point(r.tl().x + 18, r.tl().y), new Scalar(255, 255, 255), 20);
//			Imgproc.line(sourceClone, new Point(r.tl().x + 18, r.br().y), new Point(r.br().x - 18, r.br().y), new Scalar(255, 255, 255), 20);
			//}
		}
		// uncomment to test
		// writes to image
	    Imgcodecs.imwrite("connectedLines_" + counter + ".jpg", sourceClone);
	    counter++;
	    return sourceClone;
	}
	

	public void activeParkingLotProcessing(String source2) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat source = Imgcodecs.imread(source2);
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		Mat contourDest = Mat.zeros(destination.size(), CvType.CV_8UC3);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		// Filter the photo //
		// Grayscale
		Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);
		//Remove parking spot lines from car image
		//parkingMask
		Core.subtract(destination, parkingMask, destination);
		// Binary the image into black and white
		Imgproc.threshold(destination, destination, 0, 255, Imgproc.THRESH_BINARY);
		// Thicken/smooth lines
		Imgproc.dilate(destination, destination, new Mat(), new Point(-1, 1), 2);

		// Finds contours //
		Imgproc.findContours(destination, contours,new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(contourDest, contours, -1, new Scalar(255, 255, 255));

		// Populate cars 
		for(int i = 0; i < contours.size(); i++){
			Rect r = Imgproc.boundingRect(contours.get(i));
			if(r.area() >= 2000 && ((r.height / (double) r.width) > 1.0)) {
//				Imgproc.rectangle(source, r.tl(), r.br(), new Scalar(0, 0, 255), -1);
				cars.add(new ParkingSpot(r));
			}
		}
	  //  Imgcodecs.imwrite("result.png", source);

	}
	
	public void compareParkingLots(String source2) {
		// COMPARES THE ARRAYLIST OF emptyParkingLot RECTANGLES WITH THE ARRAYLIST OF THE NEW IMAGE RECTANGLES TO SEE IF THERE IS A SPOT TAKEN
		Mat result = Imgcodecs.imread("firstResult.png");
		Mat source = Imgcodecs.imread(source2);
		int spotsAvailable = emptyParkingLot.size();
		int left, right, bottom, top;

		for(int i = 0; i < emptyParkingLot.size(); i++) {
			for(int j = 0; j < cars.size(); j++) {
				left = Math.max(emptyParkingLot.get(i).getX1(), cars.get(j).getX1());
				right = Math.min(emptyParkingLot.get(i).getX2(), cars.get(j).getX2());
				bottom = Math.min(emptyParkingLot.get(i).getY2(), cars.get(j).getY2());
				top = Math.max(emptyParkingLot.get(i).getY1(), cars.get(j).getY1());

				if (left < right && bottom > top) {
					spotsAvailable--;
					Imgproc.rectangle(result, emptyParkingLot.get(i).r.tl(), emptyParkingLot.get(i).r.br(), new Scalar(0, 0, 255), -1);
					emptyParkingLot.get(i).available = false;
				}
			}
			// This is solely for us for testing purposes...the number of the spot won't be displayed so the fact that it starts at the bottom right shouldn't matter.
			System.out.println("Parking spot " + emptyParkingLot.get(i).getSpotNumber() + " is " + emptyParkingLot.get(i).getAvailable());

		}
	    //Imgcodecs.imwrite("result.png", source);

	    Imgcodecs.imwrite("result.png", result);
		System.out.println(spotsAvailable + " available parking spots.");
	}
	
	public void locateSpots(String before, String after) {
		cars.clear();
		emptyParkingLot.clear();
		num = 1;
		emptyParkingLotProcessing(connectPoints(before));
		activeParkingLotProcessing(after);
		compareParkingLots(before);
	}
	public void display(){
		for(ParkingSpot p : emptyParkingLot) {
			System.out.println(p.spotNumber);
		}
	}

//	public static void main(String[] args) {
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
////
////		ParkingSpotLocator h = new ParkingSpotLocator();
////	//	h.connectPoints();
//////		Mat source = Imgcodecs.imread("parking 3.png");
//////		Mat cars = Imgcodecs.imread("parking 3 one car.png"); 
//////		Mat dst = new Mat();
//////		Core.subtract(cars, source, dst);
//////	    Imgcodecs.imwrite("output.png", dst);
////
////		h.emptyParkingLotProcessing(h.connectPoints("test 1.png"));
////		h.activeParkingLotProcessing();
////		h.compareParkingLots();
////		System.out.println("done?");
//
//	}
}