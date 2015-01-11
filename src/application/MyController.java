package application;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;



import java.util.TreeMap;

import model.Table;
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

public class MyController implements Initializable {

	@FXML
	private Button calcBtn;
	@FXML
	private ComboBox<Integer> wordsComboBox;
	ObservableList<Integer> wordChoices = FXCollections.observableArrayList(
			5, 10, 15, 20, 50, 100);
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
		wordsComboBox.getSelectionModel().selectFirst();
		// set error message to be invisible by default
		errorMessage.setVisible(false);
		
		tWord.setCellValueFactory(new PropertyValueFactory<Table, String>("rWord"));
		tCount.setCellValueFactory(new PropertyValueFactory<Table, Integer>("rCount"));

		tableID.setItems(data);
	}
	
	public void comboBoxAction(ActionEvent event) {
		
	}
	
	
	public void generateResults(ActionEvent event) {
		// valid input
		if (urlTextField.getText().trim() != null
				&& !urlTextField.getText().trim().isEmpty()) {
			errorMessage.setVisible(false);
			
			LinkedHashMap<String, Integer> results = HtmlParser.callThis(urlTextField.getText().trim());
			
			int counter = 0;
			for (Map.Entry<String, Integer> entry : results.entrySet()) {
				Table tmp = new Table(iNumber++, entry.getKey(), entry.getValue());
				data.add(tmp);
				if (++counter >= wordsComboBox.getValue())
					break;
			}
			
			
			
//			Table entry = new Table(iNumber++, urlTextField.getText().trim(), wordsComboBox.getValue());
//			data.add(entry); // add data to table
		}
		// invalid input, display error message
		else {
			errorMessage.setVisible(true);
		}
		
	}

}
