package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Table {
	private SimpleIntegerProperty rID;
	private SimpleStringProperty rWord;
	private SimpleIntegerProperty rCount;
	
	public Table(int sID, String sWord, Integer sCount) {
		this.rID = new SimpleIntegerProperty(sID);
		this.rWord = new SimpleStringProperty(sWord);
		this.rCount = new SimpleIntegerProperty(sCount);
	}
	
	public Integer rIDProperty() {
		return rID.get();
	}
	
	public SimpleStringProperty rWordProperty() {
		return rWord;
	}

	public SimpleIntegerProperty rCountProperty() {
		return rCount;
	}

}
