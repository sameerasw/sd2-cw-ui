package com.foodiesfave.sd2cwui;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.ArrayList;

public class HelloController extends FoodiesFave implements InitializeUI {

    @FXML
    private Text burgerCount;

    @FXML
    private Text waitingQueueText;

    @FXML
    private Label q1i0;

    @FXML
    private Label q1i1;

    @FXML
    private Label q2i0;

    @FXML
    private Label q2i1;

    @FXML
    private Label q2i2;

    @FXML
    private Label q3i0;

    @FXML
    private Label q3i1;

    @FXML
    private Label q3i2;

    @FXML
    private Label q3i3;

    @FXML
    private Label q3i4;

    @FXML
    private TextField searchField;

    @FXML
    private Text searchResults;

    @FXML
    void searchButtonClick() {
        //searches for a customer by name
        String searchName = searchField.getText().toLowerCase();
        StringBuilder output = new StringBuilder();
        ArrayList<String> results = searchCustomerSelector(searchName);
        if (searchName.isEmpty()) {
            searchField.setPromptText("Enter a name to search");
        } else {
            searchField.clear();
            int index = 0;
            while (index < results.size()) {
                output.append(results.get(index)).append("\n");
                index++;
            }
            searchResults.setText(output.toString());
        }
    }

    @Override
    public void initialize() {
        //displays the initial values at launch
        burgerCount.setText(String.valueOf(burgers));
        if (waitingQueue.isEmpty()) {
            waitingQueueText.setText("No customers");
        } else {
            waitingQueueText.setText(waitingQueue.uiDataRequest());
        }
        //displays the initial queue at launch
        q1i0.setText(queue1.uiDataRequest(0));
        q1i1.setText(queue1.uiDataRequest(1));
        q2i0.setText(queue2.uiDataRequest(0));
        q2i1.setText(queue2.uiDataRequest(1));
        q2i2.setText(queue2.uiDataRequest(2));
        q3i0.setText(queue3.uiDataRequest(0));
        q3i1.setText(queue3.uiDataRequest(1));
        q3i2.setText(queue3.uiDataRequest(2));
        q3i3.setText(queue3.uiDataRequest(3));
        q3i4.setText(queue3.uiDataRequest(4));
    }
}