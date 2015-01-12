package application;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Table;

/**
 * "Controls" the UI and dictates how each module acts with the assistance of
 * the FXML file.
 * 
 * @author James Reinlein
 */
public class MyController implements Initializable {

	@FXML
	private Button calcBtn;
	@FXML
	private ComboBox<Integer> wordsComboBox;
	ObservableList<Integer> wordChoices = FXCollections.observableArrayList(5,
			10, 20, 50, 100, 200, 500, 1000);
	@FXML
	private TextField urlTextField;
	@FXML
	private Label errorMessage;

	// defining table

	@FXML
	TableView<Table> tableID;
	@FXML
	TableColumn<Table, String> tWord;
	@FXML
	TableColumn<Table, Integer> tCount;

	private int iNumber = 1;

	final ObservableList<Table> data = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// load # of word choices into combo box
		wordsComboBox.setItems(wordChoices);
		wordsComboBox.getSelectionModel().select(4);
		// set error message to be invisible by default
		errorMessage.setVisible(false);

		tWord.setCellValueFactory(new PropertyValueFactory<Table, String>(
				"rWord"));
		tCount.setCellValueFactory(new PropertyValueFactory<Table, Integer>(
				"rCount"));

		tableID.setItems(data);
	}

	/**
	 * The principle method of the applet. Connects the UI with the majority of
	 * the Java code which does the calculations and provides the output.
	 */
	public void generateResults(ActionEvent event) {
		// valid input
		if (urlTextField.getText().trim() != null
				&& !urlTextField.getText().trim().isEmpty()) {
			errorMessage.setVisible(false);
			data.clear(); // remove old results already in table

			LinkedHashMap<String, Integer> results = HtmlParser
					.countedWordResults(urlTextField.getText().trim());

			int counter = 0;
			for (Map.Entry<String, Integer> entry : results.entrySet()) {
				Table tmp = new Table(iNumber++, entry.getKey(),
						entry.getValue());
				data.add(tmp);
				if (++counter >= wordsComboBox.getValue())
					break;
			}
			tableID.scrollTo(0);
		}

		// invalid input, display error message
		else {
			errorMessage.setVisible(true);
		}

	}

}
