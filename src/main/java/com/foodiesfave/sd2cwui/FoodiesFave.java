package com.foodiesfave.sd2cwui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;

import java.io.IOException;

public class FoodiesFave extends Application {
    static FoodQueue queue1 = new FoodQueue(2);
    static FoodQueue queue2 = new FoodQueue(3);
    static FoodQueue queue3 = new FoodQueue(5);
    static FoodQueue waitingQueue = new FoodQueue(10);

    static int burgers = 50;
    public static final String ANSI_RESET = "\u001B[0m"; //ANSI colors for the program : https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FoodiesFave.class.getResource("foodiesfave-gui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        boolean continueProgram = true;
        Scanner input = new Scanner(System.in);
        while (continueProgram) {
            System.out.println(
                    """
                            --------------------------------------------------------- \s
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
                case "102", "ACQ" -> addCustomer();
                case "103", "RCQ" -> removeCustomer();
                case "104", "PCQ" -> burgers -= removeServed();
                case "105", "VCS" -> sortAlphabetically(queue1);
                case "106", "SPD" -> storeData();
                case "107", "LPD" -> burgers = loadData();
                case "108", "STK" ->
                        System.out.println(ANSI_YELLOW + "Burgers in stock: " + burgers + "\nAnd will be enough for " + burgers / 5 + " customers" + ANSI_RESET);
                case "109", "AFS" -> burgers = addStock();
                case "110", "IFQ" -> income();
                case "112", "GUI" -> gui();
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

    private static void gui() {
        //Launches the GUI
        launch();
    }

    private static void income() {
        //Calculates the income of each queue
        int burgerPrice = 650;
        System.out.println(ANSI_YELLOW + "---INCOME---" + "\nQueue 1: " + (queue1.queueLength() * burgerPrice) + "\nQueue 2: " + (queue2.queueLength() * burgerPrice) + "\nQueue 3: " + (queue3.queueLength() * burgerPrice) + ANSI_RESET);
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
            System.out.println(ANSI_YELLOW + "\nWaiting Queue: " + ANSI_RESET + waitingQueue.queueLength() + " customers");
    }

    public static void emptyArrays() {
        //Checks if any queue is empty and prints it
        if (queue1.isEmpty()) System.out.println(ANSI_YELLOW + "Cashier 1 is empty" + ANSI_RESET);
        if (queue2.isEmpty()) System.out.println(ANSI_YELLOW + "Cashier 2 is empty" + ANSI_RESET);
        if (queue3.isEmpty()) System.out.println(ANSI_YELLOW + "Cashier 3 is empty" + ANSI_RESET);
        if (!queue1.isEmpty() && !queue2.isEmpty() && !queue3.isEmpty())
            System.out.println(ANSI_RED + "All the queues are full. No empty spots." + ANSI_RESET);
    }

    public static void addCustomer() {
        //adds a customer to the queue with the least amount of customers
        int index = 0;
        while (index < 5) {
            if (queue1.emptyOrNot(index).equals(ANSI_GREEN + "X" + ANSI_RESET)) {
                System.out.println(queue1.addCustomer() + " added to queue 1");
                break;
            } else if (queue2.emptyOrNot(index).equals(ANSI_GREEN + "X" + ANSI_RESET)) {
                System.out.println(queue2.addCustomer() + " added to queue 2");
                break;
            } else if (queue3.emptyOrNot(index).equals(ANSI_GREEN + "X" + ANSI_RESET)) {
                System.out.println(queue3.addCustomer() + " added to queue 3");
                break;
            }
            index++;
        }
        if (index == 5) {
            if (waitingQueue.isFull()) {
                System.out.println(ANSI_RED + "All the queues and the waiting queue are full. No empty spots." + ANSI_RESET);
            } else {
                System.out.println(ANSI_YELLOW + "All the queues are full. " + waitingQueue.addCustomer() + " added to the waiting queue." + ANSI_RESET);
            }
        }
    }

    public static int removeServed() {
        //removes a customer from the queue from a specific location
        Scanner input = new Scanner(System.in);
        int cashier = selectItem(input, "cashier(1,2,3)");
        switch (cashier) {
            case 1 -> burgers = queue1.removeCustomer(0);
            case 2 -> burgers = queue2.removeCustomer(0);
            case 3 -> burgers = queue3.removeCustomer(0);
            default -> System.out.println(ANSI_RED + "Invalid queue" + ANSI_RESET);
        }
        return burgers;
    }

    public static void removeCustomer() {
        //removes a served customer from the queue
        Scanner input = new Scanner(System.in);
        int cashier = selectItem(input, "cashier(1,2,3): ");
        int index = selectItem(input, "location(1,2,3,4,5): ");
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

    public static int addStock() {
        //adds burgers to the stock
        System.out.print("Enter how many burgers to add: ");
        Scanner input = new Scanner(System.in);
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

    public static void sortAlphabetically(FoodQueue queue1) {
        //sorts the customers in all 3 arrays in alphabetical order without using the library sort
        String[] allCustomers = new String[queue1.length() + queue2.length() + queue3.length()];
        for (int i = 0; i < queue1.length(); i++) {
            if (!Objects.equals(queue1.returnIndex(i), "")) {
                allCustomers[i] = queue1.returnIndex(i);
            }
        }
        for (int i = 0; i < queue2.length(); i++) {
            if (!Objects.equals(queue2.returnIndex(i), "")) {
                allCustomers[i + queue1.length()] = queue2.returnIndex(i);
            }
        }
        for (int i = 0; i < queue3.length(); i++) {
            if (!Objects.equals(queue3.returnIndex(i), "")) {
                allCustomers[i + queue1.length() + queue2.length()] = queue3.returnIndex(i);
            }
        } //adds all the customers to one array

        for (int i = 0; i < allCustomers.length; i++) {
            for (int j = i + 1; j < allCustomers.length; j++) {
                if (allCustomers[i] != null && allCustomers[j] != null) {
                    //compare two strings with .compareTo
                    if (allCustomers[i].compareTo(allCustomers[j]) > 0) {
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
                System.out.println(customer);
            } //prints all the customers in the array
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

    public static int loadData() {
        //prints the data from the file
        try {
            File dataFiles = new File("programData.txt");
            Scanner myReader = new Scanner(dataFiles);
            burgers = Integer.parseInt(myReader.nextLine());
            queue1.dataRestore(tryRead(queue1.getQueue(), myReader));
            queue2.dataRestore(tryRead(queue2.getQueue(), myReader));
            queue3.dataRestore(tryRead(queue3.getQueue(), myReader));
            waitingQueue.dataRestore(tryRead(waitingQueue.getQueue(), myReader));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        arrayPrint();
        System.out.println(ANSI_YELLOW + "There are " + burgers + " burgers in the stock." + ANSI_RESET);
        return burgers;
    }

    private static String[] tryRead(String[] cashier, Scanner myReader) {
        //tries to read the next line and if it is * it sets the value to null
        String[] output = new String[cashier.length];
        for (int i = 0; i < cashier.length; i++) {
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

}
