package harystolho.mpd.controller;

import java.util.ResourceBundle;

import harystolho.mpd.DownloadUtils;
import harystolho.mpd.Main;
import harystolho.mpd.MainApp;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.shape.Arc;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class MainController {

	@FXML
	private TextField modpackUrl;

	@FXML
	private Label modpacklName;

	@FXML
	private Label modpackMods;

	@FXML
	private Button selectModsButton;

	@FXML
	private Label modpackVersion;

	@FXML
	private Button getInfoButton;

	@FXML
	private Button downloadButton;

	@FXML
	private TextArea MPDConsole;

	@FXML
	private Arc progressArch;

	@FXML
	private Label progressNumber;

	@FXML
	private ChoiceBox<String> languagueBox;

	@FXML
	private Button downoadFolderButton;

	@FXML
	private Button checkUpdatesButton;

	// --- //
	private DownloadUtils utils;
	private FXMLLoader loader;
	private MainApp app;

	public String modpackID;

	private ObservableList<String> languageList = FXCollections.observableArrayList();

	@FXML
	void initialize() {
		utils = new DownloadUtils(this);

		setupElements();
		loadEvents();

	}

	public void addText(String text) {
		Platform.runLater(() -> {
			this.MPDConsole.appendText(text + "\n");
		});
	}

	public void setInfo(String modpackName, int mods, String version) {
		this.modpacklName.setText(modpackName);
		this.modpackMods.setText(mods + "");
		this.modpackVersion.setText(version);

	}

	public void setFXMLLoader(FXMLLoader loader) {
		this.loader = loader;
	}

	public void setMainApp(MainApp app) {
		this.app = app;
	}

	@FXML
	private void setupElements() {
		languageList.add("English");
		languageList.add("Portuguese");

		languagueBox.setItems(languageList);
		selectCorrectLanguage();

	}

	private void selectCorrectLanguage() {
		switch (Main.configs.getProperty("lang")) {
		case "en":
			languagueBox.getSelectionModel().select(0);
			break;
		case "pt":
			languagueBox.getSelectionModel().select(1);
			break;
		default:
			break;
		}
	}

	@FXML
	private void loadEvents() {
		getInfoButton.setOnAction(e -> {
			utils.getInfo(modpackUrl.getText());
		});

		languagueBox.getSelectionModel().selectedIndexProperty().addListener((observer, oldValue, newValue) -> {
			String lang = languageList.get(newValue.intValue());
			switch (lang) {
			case "English":
				changeLangugue("en");
				break;
			case "Portuguese":
				changeLangugue("pt");
				break;
			default:
				break;
			}

		});

		downoadFolderButton.setOnAction(e -> {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle(loader.getResources().getString("mpd.folder"));
			chooser.showDialog(app.getStage());
		});
	}

	private void changeLangugue(String lang) {
		loader.setResources(ResourceBundle.getBundle("harystolho.lang." + lang));
		Main.configs.setProperty("lang", lang);
		Main.saveConfigs();
		app.getStage().setScene(app.loadLayout());
	}
}
