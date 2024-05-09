package engine;

import java.util.List;
import java.util.Scanner;

import model.SearchResult;

public class AppInterface {
    private ProcessingEngine processingEngine;

    public AppInterface(ProcessingEngine processingEngine) {
        this.processingEngine = processingEngine;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;


        while (running) {
            System.out.print("Enter command (index <dataset path>, search <AND query>, or quit): ");
            String input = scanner.nextLine().trim();

            String[] commandArgs = input.split(" ", 2);
            String command = commandArgs[0];

            switch (command) {
                case "index":
                    if (commandArgs.length == 2) {
                        String datasetPath = commandArgs[1];
                        indexing(datasetPath);
                    } else {
                        System.out.println("Invalid command. Please provide valid dataset path.");
                    }
                    break;
                case "search":
                    if (commandArgs.length == 2) {
                        String query = commandArgs[1];
                        search(query);
                    } else {
                        System.out.println("Invalid command. Please provide valid search query.");
                    }
                    break;
                case "quit":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }

        System.out.println("Thank you for using the File Retrieval Engine.");
    }

    private void indexing(String datasetPath) {
        System.out.println("Indexing files from dataset: " + datasetPath);
        processingEngine.index(datasetPath);
        
        System.out.println("Indexing complete.");
    }

    private void search(String query) {
        System.out.println("Searching for: " + query);
        
        List<SearchResult> results = processingEngine.search(query);

        if (results.isEmpty()) {
            System.out.println("No results found.");
        } else {
            System.out.println("Top 10 results:");
            for (int i = 0; i < results.size(); i++) {
                SearchResult result = results.get(i);
                System.out.println((i + 1) + ". " + result.getFileName() + " ( " + result.getOccurrences() + " occurrences)");
            }
        }
    }
    
}
