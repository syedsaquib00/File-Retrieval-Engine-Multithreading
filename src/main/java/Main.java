import java.util.Scanner;

import engine.AppInterface;
import engine.IndexStore;
import engine.ProcessingEngine;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("File Retrieval Engine!");
        
        System.out.print("Enter no. of Threads: ");
        int totalThreads = sc.nextInt();

        ProcessingEngine processingEngine = new ProcessingEngine(new IndexStore(), totalThreads );
        AppInterface appInterface = new AppInterface(processingEngine);
        appInterface.start();
    }

}
