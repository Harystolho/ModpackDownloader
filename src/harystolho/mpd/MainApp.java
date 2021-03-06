package harystolho.mpd;

import java.io.IOException;
import java.util.ResourceBundle;

import harystolho.mpd.controller.MainController;
import harystolho.mpd.controller.SelectModsController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage stage;
	private MainController controller;
	private SelectModsController ModsController;

	@Override
	public void start(Stage primaryStage) {
		this.stage = primaryStage;
		primaryStage.setTitle("ModpackDownloader");

		Scene scene = loadLayout();

		primaryStage.setOnCloseRequest((e) -> {
			Main.ex.shutdown();
		});

		stage.setScene(scene);
		stage.show();
	}

	public void loadGUI(String[] args) {
		launch(args);
	}

	public Scene loadLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MpdFXML.fxml"));
			loader.setResources(ResourceBundle.getBundle("harystolho.lang." + Main.configs.getProperty("lang")));

			VBox box = loader.load();
			Scene scene = new Scene(box);

			controller = loader.getController();
			controller.setMainApp(this);
			controller.setFXMLLoader(loader);

			return scene;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public Scene loadSelectModsScene() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/SelectModsView.fxml"));
			loader.setResources(ResourceBundle.getBundle("harystolho.lang." + Main.configs.getProperty("lang")));

			Pane pane = loader.load();
			Scene scene = new Scene(pane);

			ModsController = loader.getController();
			ModsController.setMainController(getMainController());

			return scene;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Stage getStage() {
		return this.stage;
	}

	public MainController getMainController() {
		return controller;
	}

	public SelectModsController getSelectModsController() {
		return ModsController;
	}
}