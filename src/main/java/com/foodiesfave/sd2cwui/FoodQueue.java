package com.foodiesfave.sd2cwui;

import java.util.InputMismatchException;
import java.util.Scanner;

public class FoodQueue {
    private final int size;
    private final Customer[] queue;

    public FoodQueue(int size) {
        this.size = size;
        this.queue = new Customer[size];
    }

    public boolean isEmpty() { return this.queue[0] == null; }

    public boolean isFull() { return this.queue[this.size-1] != null; }

    public String addCustomer() {
        Scanner queueInput = new Scanner(System.in);
        System.out.print("\nEnter customer's first name: ");
        String fName = queueInput.nextLine();
        if (fName.equals("")) {
            System.out.println(FoodiesFave.ANSI_RED + "First name cannot be empty" + FoodiesFave.ANSI_RESET);
            return "No customer";
        }
        System.out.print("Enter customer's second name: ");
        String sName = queueInput.nextLine();
        System.out.print("Enter number of burgers: ");
        try {
        int burgers = queueInput.nextInt();
            if (burgers < 1) {
                System.out.println(FoodiesFave.ANSI_RED + "Number of burgers must be greater than 0" + FoodiesFave.ANSI_RESET);
                return fName;
            }
                if (this.isFull()) {
                    System.out.println(FoodiesFave.ANSI_RED + "Queue is full" + FoodiesFave.ANSI_RESET);
                } else {
                    Customer newCustomer = new Customer(fName, sName, burgers);
                    for (int index = 0; index < this.queue.length; index++) {
                        if (this.queue[index] == null) {
                            queue[index] = newCustomer;
                            break;
                        }
                    }
                }
        }
        catch (NullPointerException | InputMismatchException e) {
            System.out.println(FoodiesFave.ANSI_RED + "burgers count must be a number" + FoodiesFave.ANSI_RESET);
        }
        return fName;
    }

    public int removeCustomer(int index) {
        int burgers = 0;
        try{
        if (this.isEmpty()) {
            System.out.println(FoodiesFave.ANSI_RED + "Queue is empty" + FoodiesFave.ANSI_RESET);
        } else {
            burgers = this.queue[index].getBurgers();
            System.out.println(FoodiesFave.ANSI_GREEN + "Customer " + this.queue[index].getFName() + " " + "removed" + FoodiesFave.ANSI_RESET);
            this.queue[index] = null;
            this.reOrder();
            if (!FoodiesFave.waitingQueue.isEmpty()){
                this.queue[this.length()-1] = FoodiesFave.waitingQueue.queue[0];
                FoodiesFave.waitingQueue.queue[0] = null;
                FoodiesFave.waitingQueue.reOrder();
                System.out.println(FoodiesFave.ANSI_GREEN + "Customer " + this.queue[this.length()-1].getFName() + " " + "added to queue from waiting list" + FoodiesFave.ANSI_RESET);
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
        String[] queue = new String[this.length()];
        for (int i = 0; i < this.length(); i++) {
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
        //returns the index of the customer at the given index
        try{ return this.queue[index].getFName() + " " + this.queue[index].getSName(); }
        catch (NullPointerException e) { return ""; }
    }

    public int length() { return this.queue.length; }

    public void dataRestore(String[] data) {
        //restores the data from the file
        for (int i = 0; i < data.length; i++) {
            try {
                String[] customerData = data[i].split("_");
                Customer newCustomer = new Customer(customerData[0], customerData[1], Integer.parseInt(customerData[2]));
                this.queue[i] = newCustomer;
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
                this.queue[i] = null;
            }
        }
    }

    public int queueLength() {
        //prints the length of the queue
        int length = 0;
        for (Customer customer : this.queue) {
            if (customer != null) {
                length++;
            }
        }
        return length;
    }
}