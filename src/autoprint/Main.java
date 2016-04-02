package autoprint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage PrimaryStage){
		PrimaryStage.setTitle("Auto-Printer");
		Button startB =  new Button();
		TextField w = new TextField(), h = new TextField();
		Text error = new Text();
		w.setPromptText("Width");
		h.setPromptText("Height");
		startB.setText("Start Taking Orders");
		
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
				double x = Double.parseDouble(w.getText());
				double y = Double.parseDouble(h.getText());
				Server s = new Server(x, y);
				Thread serverThread = new Thread(s);
				serverThread.setDaemon(true);
				serverThread.start();	
				startB.setVisible(false);
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
		grid.add(h, 0, 1);
		grid.add(startB, 0, 2);
		grid.add(error, 0, 3);
		root.getChildren().addAll(grid);
		PrimaryStage.setScene(new Scene(root, 300, 200));
		PrimaryStage.show();
	}

}
