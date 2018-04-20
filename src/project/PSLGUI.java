package project;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PSLGUI extends Application {
	BorderPane bp = new BorderPane();
	Pane center;
	Pane root;
	Pane root2;
	HBox top;
	HBox bottom;
	int counter = 0;
	ParkingSpotLocator psl = new ParkingSpotLocator();
	ComboBox<String> box;
	Button search;
	VBox v;
	Pane pane;
	JFrame structure = new JFrame();
	ImageView currentLot = new ImageView();
	Button button;
	GridPane grid;
	boolean clicked = false;
	Button result = new Button("Result");
	double startDragX = 0;
	double startDragY = 0; 
	ImageView resultPic;
	Scene scene = new Scene(bp, 1116, 800);
	boolean lot = false;
	ArrayList<ImageView> cars = new ArrayList<ImageView>();
	@Override
	public void start(Stage primaryStage) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		center = new Pane();
		grid = new GridPane();
		v = new VBox();
		bottom = new HBox();
		box = new ComboBox<String>();

		pane = new Pane();

		button = new Button("Locate Spots");
		center.getChildren().addAll(pane);
		bottom.getChildren().addAll(button, result);
		
		bp.setTop(top);
		bp.setBottom(bottom);
		
		leftMenu();
		rightMenu();
		bp.setTop(top);
		bp.setCenter(center);
		button.setOnAction(new EventHandler<ActionEvent>() { 
            @Override
            public void handle(ActionEvent event) {
                snapShot("after");
                searchFunctions();        
            }
        });
		// GONNA WORK ON THIS LATER
//		result.setOnAction(new EventHandler<ActionEvent>() {	 
//            @Override
//            public void handle(ActionEvent event) {
//            	clicked = true;
//               if(clicked) {
//            	   center.getChildren().remove(currentLot);
//            	   for(ImageView v : cars) {
//            		   center.getChildren().remove(v);
//            	   }
//           		   try {
//					Image result = new Image(new FileInputStream("result.png"));
//					resultPic = new ImageView(result);
//					resultPic.setX(220);
//					resultPic.setY(100);
//					resultPic.setFitWidth(700);
//					resultPic.setFitHeight(500);
//					center.getChildren().add(resultPic);
//					clicked = false;  
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//               }
//               else {
//            	   
//               }
//            }
//        });
		primaryStage.setScene(scene);
		primaryStage.show();
	} 
	
	public void searchFunctions() {
		psl.locateSpots("before.png", "after.png");
	}
	
	public void snapShot(String fileName) {
		  WritableImage snapImage = scene.snapshot(null);
          //display in new window
          
          ImageView snapView = new ImageView();
          snapView.setImage(snapImage);
           
          StackPane snapLayout = new StackPane();
          snapLayout.getChildren().add(snapView);
           
          Scene snapScene = new Scene(snapLayout, 800, 700);

          Stage snapStage = new Stage();
          File file = new File(fileName + ".png");
          counter++;
          RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapScene.snapshot(null), null);
          try {
          	ImageIO.write(renderedImage, "png", file);
          } catch (IOException e) {
          	// TODO Auto-generated catch block
          	e.printStackTrace();
          }
          snapStage.setTitle("Snapshot");
          snapStage.setScene(snapScene);

          snapStage.show();
	}

	
	
	public void dragAndDrop(ImageView v) {
		 v.setOnMousePressed(e -> {
	            startDragX = e.getX();
	            startDragY = e.getY();
	        });

	     v.setOnMouseDragged(e -> {
	            v.setTranslateX(e.getSceneX() - startDragX - 1000);
	            v.setTranslateY(e.getSceneY() - startDragY - 100);
	        });
	     v.setOnMouseReleased(e -> {
	    	 	try {
					rightMenu();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	 
	     });
	}
	public void lotChoice(ImageView v, Image i,  Pane root) throws IOException {
			ImageView v1 = new ImageView(i);
			currentLot = v1;
		    clear();
        	center.getChildren().add(v1);
        	reAdd();
        	v1.setX(220);
        	v1.setY(100);
        	v1.setFitWidth(700);
        	v1.setFitHeight(500);
        	snapShot("before");
    }
	public void reAdd() {
		for(ImageView v : cars){
		center.getChildren().remove(v);
		center.getChildren().add(v);
		}
	}
	public void clear() throws IOException {
		center.getChildren().clear();
		cars.clear();
		leftMenu();
		rightMenu();
	}
		public void leftMenu() throws IOException {
			Image lot1 = new Image(new FileInputStream("lot1.png"));
			Image lot2 = new Image(new FileInputStream("lot2.png"));
			Image lot3 = new Image(new FileInputStream("lot3.png"));
			ImageView v1 = new ImageView(lot1);
			ImageView v2 = new ImageView(lot2);
			ImageView v3 = new ImageView(lot3);
			v1.setFitWidth(275);
			v1.setFitHeight(120);
			v2.setFitWidth(275);
			v2.setFitHeight(110);
			v3.setFitWidth(275);
			v3.setFitHeight(120);
			root = new Pane();
		    root.setPrefSize(400, 300);
		    Text text = new Text("Choose a parking lot");
		    text.setWrappingWidth(385);
		    text.setLayoutX(15);
		    text.setLayoutY(20);
		    VBox menu = new VBox();
		    menu.setId("menu");
		    menu.prefHeightProperty().bind(root.heightProperty());
		    menu.setPrefWidth(200);

		    menu.getChildren().addAll(v1, v2, v3);
		    menu.setPadding(new Insets(20, 0, 20, 0));
		    menu.getStylesheets().add(getClass().getResource("menustyle.css").toExternalForm());
		    menu.setTranslateX(-190);
		    TranslateTransition menuTranslation = new TranslateTransition(Duration.millis(300), menu);

		    menuTranslation.setFromX(-190);
		    menuTranslation.setToX(0);
		    menu.setOnMouseEntered(evt -> {
		        menuTranslation.setRate(1);
		        menuTranslation.play();
		        v1.setOnMouseClicked(e -> {    	
		        	try {
						lotChoice(v1, lot1, root);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        });
		        v2.setOnMouseClicked(e -> {
		        	try {
						lotChoice(v2, lot2, root);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        });
		        v3.setOnMouseClicked(e -> {
		        	try {
						lotChoice(v3, lot3, root);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        });
		    });
		    menu.setOnMouseExited(evt -> {
		        menuTranslation.setRate(-1);
		        menuTranslation.play();
		    });
		    root.setLayoutY(50);
		    root.getChildren().addAll(text, menu);
		    center.getChildren().add(root);
		
	}
	public void rightMenu() throws FileNotFoundException {
		Image car1 = new Image(new FileInputStream("car1.png"));
		ImageView v1 = new ImageView(car1);
		ImageView v2 = new ImageView(car1);
		ImageView v3 = new ImageView(car1);
//		VBox cars = new VBox();
		v1.setFitHeight(120);
		v1.setFitWidth(50);
		//cars.getChildren().addAll(v1);
		center.getChildren().add(v1);
		cars.add(v1);
		v1.setLayoutX(1000);
		v1.setLayoutY(100);
		dragAndDrop(v1);

		//BufferedImage image = ImageIO.read(new File("car1.png"));
//		Image car1 = new Image(new FileInputStream("car1.png"));
////		Image car2 = new Image(new FileInputStream("car1.png"));
////		Image car3 = new Image(new FileInputStream("car1.png"));
//		ImageView v1 = new ImageView(car1);
////		ImageView v2 = new ImageView(car2);
////		ImageView v3 = new ImageView(car3);
//
//		v1.setFitWidth(80);
//		v1.setFitHeight(140);
////		v2.setFitWidth(80);
////		v2.setFitHeight(140);
////		v3.setFitWidth(80);
////		v3.setFitHeight(140);
//		
//		
//		// Use a label to display the image
////		JFrame frame = new JFrame();
////		
////		JLabel car1 = new JLabel(new ImageIcon(image));
////		frame.getContentPane().add(car1, BorderLayout.CENTER);
////		frame.setSize(300, 400);
//		//frame.setVisible(true);
//		root2 = new Pane();
//	    root2.setPrefSize(400, 300);
//
//	    Text text = new Text("Choose cars");
//
//	    text.setWrappingWidth(385);
//	    text.setLayoutX(15);
//	    text.setLayoutY(20);
//
//	    VBox menu = new VBox();
//	    menu.setId("menu");
//	    menu.prefHeightProperty().bind(root2.heightProperty());
//	    menu.setPrefWidth(200);
//
//	    menu.getChildren().addAll(v1);//, v2, v3);
//
//	    
//	    
//	    menu.getStylesheets().add(getClass().getResource("menustyle.css").toExternalForm());
//	    menu.setTranslateX(228);
//	    TranslateTransition menuTranslation = new TranslateTransition(Duration.millis(300), menu);
//
//	    menuTranslation.setFromX(228);
//	    menuTranslation.setToX(37);
//
//	    menu.setOnMouseEntered(evt -> {
//	        menuTranslation.setRate(1);
//	        menuTranslation.play();
//	        if(lot) {
//	        v1.setOnMousePressed(e -> {
//	        	try {
//	        		Image car2 = new Image(new FileInputStream("car1.png"));
//	        		ImageView v2 = new ImageView(car2);
//	        		v2.setFitWidth(80);
//	        		v2.setFitHeight(140);
//					double x = e.getSceneX();
//					double y = e.getSceneY();
//					center.getChildren().add(v2);
//					v2.setLayoutX(x);
//					v2.setLayoutY(y);
//					dragAndDrop(v2, v2.getX(), v2.getY(), root2);
//				} catch (FileNotFoundException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//	        });
//	        }
////	        v2.setOnMousePressed(e -> {
////	        	dragAndDrop(v2, v1.getX(), v2.getY(), root2);
////	        	root.getChildren().remove(v2);
////				center.getChildren().add(v2);
////
////	        });
////	        v3.setOnMousePressed(e -> {
////	        	dragAndDrop(v3, v3.getX(), v3.getY(), root2);
////	        	root.getChildren().remove(v3);
////				center.getChildren().add(v3);
////
////	        });
//	    });
//	    menu.setOnMouseExited(evt -> {
//	        menuTranslation.setRate(-1);
//	        menuTranslation.play();
//	    });
//	    root2.setLayoutX(879);
//	    root2.setLayoutY(50);
//	    root2.getChildren().addAll(text, menu);
//		center.getChildren().add(root2);

}
// MIGHT BE USED FOR DISPLAYING RESULTS
//	public void setUpSpots() {
//		grid.getChildren().clear();
//		ParkingSpot[][] spots = new ParkingSpot[3][2];
//		for(ParkingSpot p : psl.emptyParkingLot) {
//			for(int row = 0; row < 3; row++) {
//				for(int col = 0; col < 2; col++) {
//					spots[row][col] = p;
//					Label l = new Label(p.getSpotNumber() + "");
//					l.getStyleClass().add("diff");
//					grid.add(l, row, col);
//				}
//			}	
//		}
//		center.getChildren().add(grid);
//		grid.alignmentProperty().set(Pos.CENTER);
//	}
		
	
	public static void main(String[] args) {
		launch(args);
	}
}
