package com.foodiesfave.sd2cwui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.io.IOException;

public class FoodiesFave extends Application {
    static FoodQueue queue1 = new FoodQueue(2, "Queue 1");
    static FoodQueue queue2 = new FoodQueue(3 , "Queue 2");
    static FoodQueue queue3 = new FoodQueue(5 , "Queue 3");
    static FoodQueue waitingQueue = new FoodQueue(20 , "Waiting Queue");

    static int burgers = 50;
    public static final String ANSI_RESET = "\u001B[0m"; //ANSI colors for the program : https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FoodiesFave.class.getResource("foodiesfave-gui.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Foodies Fave - Cashiers");
        stage.setScene(scene);
        stage.getIcons().add(new Image("file:src/main/resources/com/foodiesfave/icons/burger.png"));
        stage.show();
    }

    private static volatile boolean javaFxLaunched = false;
    // Launches the JavaFX application and keeps its thread allowing to re-launch after closing (fix for issue #1) https://stackoverflow.com/questions/24320014/how-to-call-launch-more-than-once-in-java/61771424#61771424

    public static void launchGUI() {
        if (!javaFxLaunched) { // First time
            Platform.setImplicitExit(false);
            new Thread(()->Application.launch(FoodiesFave.class)).start();
            javaFxLaunched = true;
        } else { // Next times
            Platform.runLater(()->{
                try {
                    Application application = FoodiesFave.class.getDeclaredConstructor().newInstance();
                    Stage primaryStage = new Stage();
                    application.start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) {
        boolean continueProgram = true;
        Scanner input = new Scanner(System.in);
        while (continueProgram) {
            System.out.println(
                    """
                            \n--------------------------------------------------------- \s
                            100 or VFQ: View all Queues.
                            101 or VEQ: View all Empty Queues.
                            102 or ACQ: Add customer to a Queue.
                            103 or RCQ: Remove a customer from a Queue.
                            104 or PCQ: Remove a served customer.
                            105 or VCS: View Customers Sorted in alphabetical order.
                            106 or SPD: Store Program Data into file.
                            107 or LPD: Load Program Data from file.
                            108 or STK: View Remaining burgers Stock.
                            109 or AFS: Add burgers to Stock.
                            110 or IFQ: View the income of each queue.
                            112 or GUI: Launch the GUI.
                            999 or EXT: Exit the Program.""");

            if (burgers <= 10) {
                System.out.println("\n" + ANSI_RED_BACKGROUND + "Burger stock is low (" + burgers + "), Consider adding more burgers to the stock." + ANSI_RESET);
            } //Menu for the program
            System.out.print("\nEnter an option: ");

            switch (input.next().toUpperCase()) {
                case "100", "VFQ" -> arrayPrint();
                case "101", "VEQ" -> emptyArrays();
                case "102", "ACQ" -> addCustomerSelector();
                case "103", "RCQ" -> removeCustomer(input);
                case "104", "PCQ" -> burgers -= removeServed(input);
                case "105", "VCS" -> sortAlphabetically();
                case "106", "SPD" -> storeData();
                case "107", "LPD" -> burgers = loadData(input);
                case "108", "STK" ->
                        System.out.println(ANSI_YELLOW + "Burgers in stock: " + burgers + "\nAnd will be roughly enough for " + burgers / 5 + " customers. (5 per)" + ANSI_RESET);
                case "109", "AFS" -> burgers = addStock(input);
                case "110", "IFQ" -> income();
                case "112", "GUI" -> launchGUI();
                case "999", "EXT" -> continueProgram = false; //loop breaker
                default -> System.out.println(ANSI_RED + "Invalid option" + ANSI_RESET);
            }
        }
        System.out.print(ANSI_RED + "Do you want to save current data before exit?\nPress " + ANSI_YELLOW + "Y" + ANSI_RED + " to save or " + ANSI_YELLOW + "anything else" + ANSI_RED + " to exit. :" + ANSI_RESET); //data save prompt
        if (input.next().equalsIgnoreCase("Y")) {
            storeData();
        }
        input.close(); //important : closes the scanner
        System.out.println(ANSI_YELLOW + "Program ended." + ANSI_RESET);
        System.exit(0); //exits the program
    }

    private static void income() {
        //Calculates the income of each queue
        int burgerPrice = 650;
        System.out.println(ANSI_YELLOW + "---INCOME---" + "\nQueue 1: " + (queue1.queueBurgerCount() * burgerPrice) + "\nQueue 2: " + (queue2.queueBurgerCount() * burgerPrice) + "\nQueue 3: " + (queue3.queueBurgerCount() * burgerPrice) + ANSI_RESET);
    }

    public static void arrayPrint() {
        //Prints the arrays in a vertical format
        int i = 0;
        System.out.println("*****************\n*   Cashiers   *\n*****************");
        while (i < 5) {
            System.out.println("    " + queue1.emptyOrNot(i) + "   " + queue2.emptyOrNot(i) + "   " + queue3.emptyOrNot(i) + "    ");
            i++;
        }
        System.out.println(ANSI_YELLOW + "\n0" + ANSI_RESET + "-Occupied " + ANSI_GREEN + "X" + ANSI_RESET + "-Not Occupied");
        if (!waitingQueue.isEmpty())
            System.out.println(ANSI_YELLOW + "\nWaiting Queue: " + ANSI_RESET + waitingQueue.length(true) + " customers");
    }

    public static void emptyArrays() {
        //Checks if any queue is empty and prints it
        if (!queue1.isFull()) System.out.println(ANSI_YELLOW + "Cashier 1 queue is empty.  At least one spot available." + ANSI_RESET);
        if (!queue2.isFull()) System.out.println(ANSI_YELLOW + "Cashier 2 queue is empty.  At least one spot available." + ANSI_RESET);
        if (!queue3.isFull()) System.out.println(ANSI_YELLOW + "Cashier 3 queue is empty.  At least one spot available." + ANSI_RESET);
        if (queue1.isFull() && queue2.isFull() && queue3.isFull())
            System.out.println(ANSI_RED + "All the queues are full. No empty spots." + ANSI_RESET);
    }

    public static void addCustomerSelector() {
        //adds a customer to the queue with the least amount of customers
        int index = 0;
        while (index < 5) {
            //this process will select the shortest queue and the position to add the customer automatically.
            if (queue1.emptyOrNot(index).equals(ANSI_GREEN + "X" + ANSI_RESET)) {
                queue1.addCustomer();
                break;
            } else if (queue2.emptyOrNot(index).equals(ANSI_GREEN + "X" + ANSI_RESET)) {
                queue2.addCustomer();
                break;
            } else if (queue3.emptyOrNot(index).equals(ANSI_GREEN + "X" + ANSI_RESET)) {
                queue3.addCustomer();
                break;
            }
            index++;
        }
        if (index == 5) {
            if (waitingQueue.isFull()) {
                System.out.println(ANSI_RED + "All the queues and the waiting queue are full. No empty spots. Try adding later." + ANSI_RESET);
            } else {
                System.out.print(ANSI_YELLOW + "All the queues are full. Adding to the waiting queue."+ ANSI_RESET);
                waitingQueue.addCustomer();
            }
        }
    }

    public static int removeServed(Scanner input) {
        //removes a customer from the queue from a specific location
        int cashier = selectItem(input, "cashier(1,2,3)");
        switch (cashier) {
            case 1 -> burgers = queue1.removeCustomer(0);
            case 2 -> burgers = queue2.removeCustomer(0);
            case 3 -> burgers = queue3.removeCustomer(0);
            default -> System.out.println(ANSI_RED + "Invalid queue" + ANSI_RESET);
        }
        return burgers;
    }

    public static void removeCustomer(Scanner input) {
        //removes a served customer from the queue
        int cashier = selectItem(input, "cashier(1,2,3)");
        int index = selectItem(input, "location(1,2,3,4,5)");
        switch (cashier) {
            case 1 -> queue1.removeCustomer(index - 1);
            case 2 -> queue2.removeCustomer(index - 1);
            case 3 -> queue3.removeCustomer(index - 1);
            default -> System.out.println(ANSI_RED + "Invalid queue" + ANSI_RESET);
        }
    }

    public static int selectItem(Scanner input, String item) {
        //Asks the user to select a cashier or a position depending on the passed parameter
        System.out.print("Enter which " + item + " : ");
        int received;
        try {
            received = input.nextInt();
        } catch (Exception e) {
            System.out.println(ANSI_RED_BACKGROUND + "Invalid " + item + ANSI_RESET);
            return 0; //returns 0 if the user enters an invalid queue or position
        }
        return received;
    }

    public static int addStock(Scanner input) {
        //adds burgers to the stock
        System.out.print("Enter how many burgers to add: ");
        try {
            burgers += input.nextInt();
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Invalid input" + ANSI_RESET);
            return burgers;
        }
        if (burgers > 50) {
            System.out.println(ANSI_RED + "Cannot add more than 50 burgers, the rest of " + (burgers - 50) + " was ignored." + ANSI_RESET);
            return 50;
        }
        System.out.println(ANSI_GREEN + "Added burgers to the stock, now there are " + burgers + " burgers." + ANSI_YELLOW + "\nWill be enough for " + burgers / 5 + " customers." + ANSI_RESET);
        return burgers;
    }

    public static void sortAlphabetically() {
        //sorts the customers in all 3 arrays in alphabetical order without using the library sort
        String[] allCustomers = mergeQueues();

        for (int i = 0; i < allCustomers.length; i++) {
            for (int j = i + 1; j < allCustomers.length; j++) {
                if (allCustomers[i] != null && allCustomers[j] != null) {
                    //compare two strings with .compareTo
                    if (allCustomers[i].toLowerCase().compareTo(allCustomers[j].toLowerCase()) > 0) {
                        String temp = allCustomers[i];
                        allCustomers[i] = allCustomers[j];
                        allCustomers[j] = temp;
                    }
                }
            }
        }
        System.out.println(ANSI_GREEN + "Customers sorted alphabetically: " + ANSI_RESET);
        for (String customer : allCustomers) {
            if (customer != null) {
                System.out.println(customer); //prints all the customers in the array
            }
        }
    }

    public static void storeData() {
        //stores the data in a file : https://www.w3schools.com/java/java_files_create.asp
        try {
            File dataFile = new File("programData.txt");
            if (dataFile.createNewFile()) {
                System.out.println(ANSI_GREEN + "File created: " + dataFile.getName() + ANSI_RESET);
            } else {
                System.out.println(ANSI_YELLOW + "File already exists." + ANSI_RESET);
            }
            FileWriter dataWriter = new FileWriter("programData.txt");
            dataWriter.write(burgers + "\n"); //first line is the burgers amount
            writeLine(queue1.getQueue(), dataWriter); //writes the first queue
            writeLine(queue2.getQueue(), dataWriter); //writes the second queue
            writeLine(queue3.getQueue(), dataWriter); //writes the third queue
            writeLine(waitingQueue.getQueue(), dataWriter); //writes the waiting queue
            dataWriter.close();
            System.out.println(ANSI_GREEN + "Successfully wrote to the file." + ANSI_RESET);
        } catch (IOException e) {
            System.out.println(ANSI_RED_BACKGROUND + "An error occurred." + ANSI_RESET);
            e.printStackTrace();
        }
    }

    private static void writeLine(String[] cashier, FileWriter dataWriter) throws IOException {
        //writes current data to the file line by line (for cashier arrays)
        for (String s : cashier) {
            if (s == null) {
                dataWriter.write("*\n");
            } else {
                dataWriter.write(s + "\n");
            }
        }
    }

    public static int loadData(Scanner input) {
        //prints the data from the file
        System.out.println(ANSI_YELLOW + "Are you sure want to overwrite the existing data?\nEnter " + ANSI_RED + "Y" + ANSI_YELLOW + " to confirm or anything else to abort: " + ANSI_RESET);
        if (!input.next().equalsIgnoreCase("Y")) {
            System.out.print(ANSI_RED + "Aborted." + ANSI_RESET);
            return burgers;
        }
        try {
            File dataFiles = new File("programData.txt");
            Scanner myReader = new Scanner(dataFiles);
            burgers = Integer.parseInt(myReader.nextLine()); //first line is the burgers amount
            queue1.dataRestore(tryRead(queue1.length(false), myReader)); //restores the first queue
            queue2.dataRestore(tryRead(queue2.length(false), myReader)); //restores the second queue
            queue3.dataRestore(tryRead(queue3.length(false), myReader)); //restores the third queue
            waitingQueue.dataRestore(tryRead(waitingQueue.length(false), myReader)); //restores the waiting queue
        } catch (FileNotFoundException e) {
            System.out.println(ANSI_RED_BACKGROUND + "An error occurred. Backup file not found." + ANSI_RESET);
        }
        System.out.println(ANSI_GREEN + "Successfully loaded the data." + ANSI_RESET);
        arrayPrint();
        System.out.println(ANSI_YELLOW + "There are " + burgers + " burgers in the stock." + ANSI_RESET);
        return burgers;
    }

    private static String[] tryRead(int length, Scanner myReader) {
        //tries to read the next line and if it is * it sets the value to null
        String[] output = new String[length];
        for (int i = 0; i < length; i++) {
            if (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.equals("*")) {
                    output[i] = null;
                } else {
                    output[i] = data;
                }
            }
        }
        return output;
    }

    public static ArrayList<String> searchCustomerSelector(String name) {
        //searches for a customer in the queues
        ArrayList<String> foundCustomers = new ArrayList<>();
        if (queue1.searchCustomer(name) != null) {
            foundCustomers.add("Results from queue 1 :\n" + queue1.searchCustomer(name));
        }
        if (queue2.searchCustomer(name) != null) {
            foundCustomers.add("Results from queue 2 :\n" + queue2.searchCustomer(name));
        }
        if (queue3.searchCustomer(name) != null) {
            foundCustomers.add("Results from queue 3 :\n" + queue3.searchCustomer(name));
        }
        if (waitingQueue.searchCustomer(name) != null) {
            foundCustomers.add("Results from waiting queue :\n" + waitingQueue.searchCustomer(name));
        }
        return foundCustomers;
    }

    private static String[] mergeQueues() {
        String[] allCustomers = new String[queue1.length(false) + queue2.length(false) + queue3.length(false)];
        for (int i = 0; i < queue1.length(false); i++) {
            if (!Objects.equals(queue1.returnIndex(i), "")) {
                allCustomers[i] = queue1.returnIndex(i);
            }
        }
        for (int i = 0; i < queue2.length(false); i++) {
            if (!Objects.equals(queue2.returnIndex(i), "")) {
                allCustomers[i + queue1.length(false)] = queue2.returnIndex(i);
            }
        }
        for (int i = 0; i < queue3.length(false); i++) {
            if (!Objects.equals(queue3.returnIndex(i), "")) {
                allCustomers[i + queue1.length(false) + queue2.length(false)] = queue3.returnIndex(i);
            }
        }
        return allCustomers;
    }
}