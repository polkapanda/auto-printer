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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{
	
	Server s;
	static ObservableList<String> fontList = FXCollections.observableArrayList();
	static ComboBox<String> fontBox = new ComboBox<String>(fontList);
	static TextField fontSize = new TextField();
	static Button startB =  new Button();
	static TextField w = new TextField(), h = new TextField(),portNum = new TextField();
	static Text error = new Text();
	public static void main(String[] args) {
		launch(args);
	}
	
	public static void printerNotConnect(){
		fontBox.setDisable(false);
		fontSize.setDisable(false);
		w.setDisable(false);
		h.setDisable(false);
		portNum.setDisable(false);
		startB.setVisible(true);
		error.setText("Please Choose a Printer");
	}
	
	public static void goodPrint(){
		error.setText("Running Please Do Not Close!");
	}
	
	public void startServer(){
		try{
			double x = Double.parseDouble(w.getText());
			double y = Double.parseDouble(h.getText());
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
					s = new Server(x, y , port, sFont, 
							Integer.parseInt(fontSize.getText()));
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
					s = new Server(x, y, sFont, 
							Integer.parseInt(fontSize.getText()));
				}
				error.setText("");
			}
			w.setDisable(true);
			h.setDisable(true);
			portNum.setDisable(true);
			Thread serverThread = new Thread(s);
			serverThread.setDaemon(true);
			serverThread.start();	
			startB.setVisible(false);
			}catch(Exception e){
				error.setText("Enter Paper Height and Width as a decimal number please");
			}
	}
	
	@Override
	public void start(Stage PrimaryStage){
		PrimaryStage.setTitle("Auto-Printer");
		w.setPromptText("Width");
		h.setPromptText("Height");
		portNum.setPromptText("Port Number (optional)");
		w.setStyle("-fx-prompt-text-fill: grey;");
		h.setStyle("-fx-prompt-text-fill: grey;");
		portNum.setStyle("-fx-prompt-text-fill: grey;");
		fontSize.setPromptText("Font Size (optional)");
		fontSize.setDisable(true);
		fontBox.setPromptText("Font (optional)");
		fontBox.setEditable(false);
		
		
		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String f : fonts){
			fontBox.getItems().add(f);
		}
		
		startB.setText("Start Taking Orders");
		
		
		fontBox.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				fontSize.setDisable(false);
			}
		});
		
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
		error.setTranslateY(20);
		root.getChildren().add(error);
		
		PrimaryStage.setResizable(false);
		PrimaryStage.setScene(new Scene(root, 375, 250));
		PrimaryStage.show();
	}

}
