package autoprint;

import java.awt.GraphicsEnvironment;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{
	
	/**
	 * @param fontList - a list of fonts that are grabbed from the computer
	 * @param fontBox - the drop down that the user can select a font from
	 * @param fontSize - the text field for the user entered font size
	 * @param startB - button that starts the networking
	 * @param w - text field for the width of the paper
	 * @param h - text field for the height of the paper being printed
	 * @param portNum - text field for the optional port number enter
	 * @param error - text view for error messages
	 * @param runningMessage - text view for text that shows when the server is running
	 */
	Server s;
	static ObservableList<String> fontList = FXCollections.observableArrayList();
	static ComboBox<String> fontBox = new ComboBox<String>(fontList);
	static TextField fontSize = new TextField();
	static Button startB =  new Button();
	static TextField w = new TextField(), h = new TextField(),portNum = new TextField();
	static Text error = new Text();
	static Text runningMessage = new Text();
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Enables all the input boxes if the printer is not selected
	 */
	public static void printerNotConnect(){
		fontBox.setDisable(false);
		fontSize.setDisable(false);
		w.setDisable(false);
		h.setDisable(false);
		portNum.setDisable(false);
		startB.setVisible(true);
		error.setText("Please Choose a Printer");
	}
	
	/**
	 * Sets the text of the message that shows while server is running
	 */
	public static void goodPrint(){
		runningMessage.setText("Running Please Do Not Close!");
	}
	
	/**
	 * Checks validity of the user entered fields
	 * Gives sets error message if fields are not valid
	 */
	public void startServer(){
		try{
			double x = Double.parseDouble(w.getText());
			double y = Double.parseDouble(h.getText());
			if (x <= 0 || y <= 0){
				Exception e = new Exception();
				throw e;
			}
			String sFont = fontBox.getValue();
			int port;
			if (!portNum.getText().isEmpty()){
				port = Integer.parseInt(portNum.getText());
				if (fontSize.getText().isEmpty()){
					if (sFont == null){
						s = new Server(x, y, port);
					}else{
						s = new Server(x, y, port, sFont);
					}
				}else{
					int fs = Integer.parseInt(fontSize.getText());
					if (fs > 0)
						s = new Server(x, y , port, sFont, fs);
					else{
						Exception e = new Exception();
						throw e;
					}
				}
				error.setText("");
			}else{
				if (fontSize.getText().isEmpty()){
					if (sFont == null){
						s = new Server(x, y);
					}else{
						s = new Server(x, y, sFont);
					}
				}else{
					int fs = Integer.parseInt(fontSize.getText());
					if (fs > 0)
						s = new Server(x, y, sFont, fs);
					else{
						Exception e = new Exception();
						throw e;
					}
				}
				error.setText("");
			}
			w.setDisable(true);
			h.setDisable(true);
			portNum.setDisable(true);
			fontBox.setDisable(true);
			fontSize.setDisable(true);
			Thread serverThread = new Thread(s);
			serverThread.setDaemon(true);
			serverThread.start();	
			startB.setVisible(false);
			}catch(Exception e){
				error.setText("Enter Paper Height and Width as a positive decimal number please."
							+ "\nIf you entered font size please make sure it is a positive whole number.");
			}
	}
	
	@Override
	public void start(Stage PrimaryStage){
		//initialize hints and stuff
		PrimaryStage.setTitle("Auto-Printer");
		w.setPromptText("Width");
		h.setPromptText("Height");
		portNum.setPromptText("Port Number (optional)");
		w.setStyle("-fx-prompt-text-fill: grey;");
		h.setStyle("-fx-prompt-text-fill: grey;");
		portNum.setStyle("-fx-prompt-text-fill: grey;");
		error.setFill(Color.RED);
		fontSize.setPromptText("Font Size (optional)");
		fontSize.setDisable(true);
		fontBox.setPromptText("Font (optional)");
		fontBox.setEditable(false);
		
		//gets fonts on the computer
		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String f : fonts){
			fontBox.getItems().add(f);
		}
		
		startB.setText("Start Taking Orders");
		
		//enables the font size when a font is chosen
		fontBox.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				fontSize.setDisable(false);
			}
		});
		
		//Locks the button until the required fields are entered
		BooleanBinding bb = new BooleanBinding(){
			{
				super.bind(w.textProperty(), h.textProperty());
			}
			@Override
			protected boolean computeValue(){
				return (w.getText().isEmpty() || h.getText().isEmpty());
			}
		};
		startB.disableProperty().bind(bb);
		
		startB.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				startServer();
			}
		});
		
		PrimaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent event){
				
				Platform.exit();
				System.exit(0);
			}
		});
		
		StackPane root = new StackPane();
		GridPane grid = new GridPane();
		grid.add(w, 0, 0);
		grid.setVgap(10.0d);
		grid.add(h, 0, 1);
		grid.add(portNum, 0, 2);
		grid.add(startB, 0, 3);
		grid.add(fontBox, 0, 4);
		grid.add(fontSize, 0, 5);
		grid.setAlignment(Pos.CENTER);
		root.getChildren().addAll(grid);
		runningMessage.setTranslateY(20);
		error.setTranslateY(120);
		root.getChildren().add(runningMessage);
		root.getChildren().add(error);
		PrimaryStage.getIcons().add(new Image("/printerIcon.png"));
		PrimaryStage.setResizable(false);
		PrimaryStage.setScene(new Scene(root, 395, 270));
		PrimaryStage.show();
	}

}
