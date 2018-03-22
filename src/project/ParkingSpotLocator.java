package project;

import java.util.List;
import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;

public class ParkingSpotLocator {
	
	protected static ArrayList<ParkingSpot> emptyParkingLot = new ArrayList<ParkingSpot>();
	protected ArrayList<ParkingSpot> cars = new ArrayList<ParkingSpot>();
	Mat parkingMask;


	public void emptyParkingLotProcessing() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat source = Imgcodecs.imread("parkinglot.jpg");
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		Mat contourDest = Mat.zeros(destination.size(), CvType.CV_8UC3);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		// Filter the empty Parking Lot //
		// Grayscale
		Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);
		// Binary the image into black and white
		Imgproc.threshold(destination, destination, 0, 255, Imgproc.THRESH_BINARY);
		// Thicken/smooth lines
		Imgproc.dilate(destination, destination, new Mat(), new Point(-1, 1), 2);
		parkingMask = destination;

		// Finds contours //
		Imgproc.findContours(destination.clone(), contours,new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(contourDest, contours, -1, new Scalar(255, 255, 255));
		
		// Populate emptyParkingLot with parking spaces from contours
		for(int i = 0; i < contours.size(); i++){
			Rect r = Imgproc.boundingRect(contours.get(i));
			if(r.area() >= 2000 && ((r.height / (double) r.width) > 1.0)) {
				Imgproc.rectangle(contourDest, r.tl(), r.br(), new Scalar(100, 175, 50), -1);
				emptyParkingLot.add(new ParkingSpot(r));
			}
		}

		// OPENCV CREATES RECTANGLES FROM BOTTOM UP, SO SPOTS ARE NUMBER 6-1 INSTEAD OF 1-6
		// THIS JUST RENAMES SPOTS IN NUMERICAL ORDER
		int count = 0;
		for(int i = emptyParkingLot.size() - 1; i >= 0; i--) {
			count++;
			emptyParkingLot.get(i).setSpotNumber(count);
		}
	}


	public void activeParkingLotProcessing() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat source = Imgcodecs.imread("Test3.png");
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		Mat contourDest = Mat.zeros(destination.size(), CvType.CV_8UC3);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		// Filter the photo //
		// Grayscale
		Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);
		//Remove parking spot lines from car image
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
				Imgproc.rectangle(contourDest.clone(), r.tl(), r.br(), new Scalar(0, 0, 255), -1);
				cars.add(new ParkingSpot(r));
			}

		}
	}
	
	public void compareParkingLots() {
		// if greater than x1 and less than x2, and greater than y1 and less than y2
		// COMPARES THE ARRAYLIST OF emptyParkingLot RECTANGLES WITH THE ARRAYLIST OF THE NEW IMAGE RECTANGLES TO SEE IF THERE IS A SPOT TAKEN
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
					emptyParkingLot.get(i).available = false;
				}
			}
			// This is solely for us for testing purposes...the number of the spot won't be displayed so the fact that it starts at the bottom right shouldn't matter.
			System.out.println("Parking spot " + emptyParkingLot.get(i).getSpotNumber() + " is " + emptyParkingLot.get(i).getAvailable());
		}
		System.out.println(spotsAvailable + " available parking spots.");
	}

	public static void main(String[] args) {
		ParkingSpotLocator h = new ParkingSpotLocator();
		h.emptyParkingLotProcessing();
		h.activeParkingLotProcessing();
		h.compareParkingLots();
	}
}