package fxResurssit;
	
import javafx.application.Application;
import javafx.stage.Stage;
import ryhma.Ryhma;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


/**
 * P��ohjelma
 * @author miemkoiv
 * @version 11.2.2017
 *
 */
public class ResurssitMain extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			final FXMLLoader ldr = new FXMLLoader(getClass().getResource("ResurssitGUIView.fxml"));
			final BorderPane root = (BorderPane)ldr.load();
			final ResurssitGUIController ryhmaCtrl = (ResurssitGUIController)ldr.getController();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("resurssit.css").toExternalForm());
			primaryStage.setScene(scene); //koko ohjelman p��ikkuna
			primaryStage.setTitle("Ty�kalurekisteri");
			
			Ryhma ryhma = new Ryhma();
			ryhmaCtrl.setRyhma(ryhma);
			
			primaryStage.show();
			ryhmaCtrl.avaa();
		} catch(Exception e) {
			e.printStackTrace();     //"hy�dyllinen kehitysvaiheessa, mutta asiakkaalle luovutettavasta versiosta voisi poistaa"
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * K�ynnistet��n ohjelma
	 * @param args ei k�yt�ss�
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
