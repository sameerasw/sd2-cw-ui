package com.foodiesfave.sd2cwui;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FoodQueue {
    private final int size;
    private final String queueName;
    private final Customer[] queue;

    public FoodQueue(int size, String queueName) {
        //constructor
        this.size = size;
        this.queueName = queueName;
        this.queue = new Customer[size];
    }

    public boolean isEmpty() { return this.queue[0] == null; } //if the first element is null, the queue is empty

    public boolean isFull() { return this.queue[this.size-1] != null; } //if the last element is not null, the queue is full

    public void addCustomer() {
        //adds a customer to the queue with required details
        Scanner queueInput = new Scanner(System.in);
        System.out.print("\nEnter customer's first name: ");
        String fName = queueInput.nextLine();
        if (fName.equals("")) {
            System.out.println(FoodiesFave.ANSI_RED + "First name cannot be empty" + FoodiesFave.ANSI_RESET + "\nNo customer was added. Please try again.");
            return;
        }
        System.out.print("Enter customer's second name: ");
        String sName = queueInput.nextLine(); //get the customer's name (optional)
        System.out.print("Enter number of burgers: ");
        try {
            int burgers = queueInput.nextInt();
                if (burgers < 1) {
                    System.out.println(FoodiesFave.ANSI_RED + "Number of burgers must be greater than 0. " + FoodiesFave.ANSI_RESET + "\nNo customer was added. Please try again.");
                    return;
                }
                Customer newCustomer = new Customer(fName, sName, burgers); //create a new customer object
                for (int index = 0; index < this.queue.length; index++) {
                    if (this.queue[index] == null) {
                        queue[index] = newCustomer;
                        break;
                    }
                }
        }
        catch (NullPointerException | InputMismatchException e) {
            System.out.println(FoodiesFave.ANSI_RED + "burgers count must be a number" + FoodiesFave.ANSI_RESET);
            return;
        }
        System.out.println(FoodiesFave.ANSI_GREEN + "Customer " + fName + " " + sName + " added to " + this.queueName + FoodiesFave.ANSI_RESET);
    }

    public int removeCustomer(int index) {
        //removes a customer from the queue at the specified index and returns the number of burgers they ordered
        int burgers = 0;
        try{
        if (this.isEmpty()) {
            System.out.println(FoodiesFave.ANSI_RED + "Queue is empty" + FoodiesFave.ANSI_RESET);
        } else {
            burgers = this.queue[index].getBurgers();
            System.out.println(FoodiesFave.ANSI_GREEN + "Customer " + this.queue[index].getFName() + " removed from " + this.queueName + FoodiesFave.ANSI_RESET);
            this.queue[index] = null; //remove the customer from the queue
            this.reOrder(); //reorder the queue
            if (!FoodiesFave.waitingQueue.isEmpty() && !this.queueName.equals("Waiting Queue")){
                this.queue[this.length(false)-1] = FoodiesFave.waitingQueue.queue[0];
                FoodiesFave.waitingQueue.queue[0] = null; //remove the customer from the waiting queue
                FoodiesFave.waitingQueue.reOrder(); //reorder the waiting queue
                System.out.println(FoodiesFave.ANSI_GREEN + "Customer " + this.queue[this.length(false)-1].getFName() + " " + "added to " + this.queueName + " from waiting list" + FoodiesFave.ANSI_RESET);
            }
        }
        }
        catch (IndexOutOfBoundsException e){
            System.out.println(FoodiesFave.ANSI_RED + "position out of bounds" + FoodiesFave.ANSI_RESET);
        }
        return burgers;
    }

    public void reOrder(){
        //reorders the queue so that the first customer is at the front
        for (int first = 0; first < this.queue.length; first++) {
            if (this.queue[first] == null) {
                for (int second = first; second < this.queue.length; second++) {
                    if (this.queue[second] != null) {
                        this.queue[first] = this.queue[second];
                        this.queue[second] = null;
                        break;
                    }
                }
            }
        }
    }

    public String[] getQueue() {
        //returns a string array of the queue with each element in the format "firstName_secondName_burgers"
        String[] queue = new String[this.length(false)];
        for (int i = 0; i < this.length(false); i++) {
            try {
                queue[i] = this.queue[i].getFName() + "_" + this.queue[i].getSName() + "_" + this.queue[i].getBurgers();
            }
            catch (NullPointerException e) {
                queue[i] = "*";
            }
        }
        return queue;
    }

    public String emptyOrNot(int index) {
        //returns X if the index is occupied and O if it is empty
            try {
                if (this.queue[index] == null) {
                    return FoodiesFave.ANSI_GREEN +"X"+ FoodiesFave.ANSI_RESET;
                } else {
                    return FoodiesFave.ANSI_YELLOW + "0" + FoodiesFave.ANSI_RESET;
                }
            } catch (IndexOutOfBoundsException e) {
                    return " ";
            }
    }

    public String returnIndex(int index) {
        //returns the first and second name of the customer at the specified index
        try{ return this.queue[index].getFName() + " " + this.queue[index].getSName(); }
        catch (NullPointerException e) { return ""; }
    }

    public int length(boolean isWaiting) {
        //returns the length of the queue (if isWaiting is true, it returns the length of the occupied queue, otherwise it returns the length of the whole queue
        if(isWaiting){
            int length = 0;
            for (Customer customer : this.queue) {
                if (customer != null) {
                    length++;
                }
            }
            return length;
        }
        else{
            return this.queue.length;
        }
    }

    public void dataRestore(String[] data) {
        //restores the data from the array of strings to the queue
        for (int i = 0; i < data.length; i++) {
            try {
                String[] customerData = data[i].split("_"); //split the data into an array of strings
                Customer newCustomer = new Customer(customerData[0], customerData[1], Integer.parseInt(customerData[2]));
                this.queue[i] = newCustomer;
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
                this.queue[i] = null;
            }
        }
    }

    public int queueBurgerCount() {
        //returns how much burgers are requested from the whole queue
        int burgers = 0;
        for (Customer customer : this.queue) {
            try {
                burgers += customer.getBurgers();
            } catch (NullPointerException e) {
                break;
            }
        }
        return burgers;
    }

    public String uiDataRequest(int index) {
        //returns the data of the customer at the given index as a String
        try {
            return "👤\n" + this.queue[index].getFName() + " " + this.queue[index].getSName() + " -" + this.queue[index].getBurgers();
        }
        catch (NullPointerException e) {
            return "❌";
        }
    }

    public String uiDataRequest() {
        //returns the data of the queue as a String
        StringBuilder queue = new StringBuilder();
        for (int i = 0; i < this.queue.length; i++) {
            try {
                queue.append((i + 1)).append(". ").append(this.queue[i].getFName()).append(" ").append(this.queue[i].getSName()).append(" - ").append(this.queue[i].getBurgers()).append("\n");
            }
            catch (NullPointerException e) {
                queue.append("\n");
            }
        }
        return queue.toString();
    }

    public String searchCustomer(String name){
        //searches for a customer in the queue by their name and returns their data as a String
        ArrayList<String> results = new ArrayList<>();
        for (Customer customer : this.queue) {
            try {
                if (customer.getFName().toLowerCase().contains(name) || customer.getSName().toLowerCase().contains(name)) {
                    results.add(customer.getFName() + " " + customer.getSName() + " - " + customer.getBurgers());
                }
            } catch (NullPointerException e) {
                break;
            }
        }
        if (results.size() == 0) {
            return null;
        } else {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < results.size(); i++) {
                result.append((i + 1)).append(". ").append(results.get(i)).append("\n"); //add the results to a string
            }
            return result.toString();
        }
    }
}