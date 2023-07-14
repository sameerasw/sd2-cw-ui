module com.foodiesfave.sd2cwui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.foodiesfave.sd2cwui to javafx.fxml;
    exports com.foodiesfave.sd2cwui;
}