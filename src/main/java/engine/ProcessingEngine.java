package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.SearchResult;

public class ProcessingEngine {
    private IndexStore indexStore;
    private int totalThreads;

    public ProcessingEngine(IndexStore indexStore, int totalThreads) {
        this.indexStore = indexStore;
        this.totalThreads = totalThreads;
    }
    
    public void index(String datasetPath) {
    	long startTime = System.currentTimeMillis();
    	
    	// Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);

        // Traverse dataset and submit indexing tasks to executor
        indexDataset(new File(datasetPath), executor);

        //wall time
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        
        //Dataset size
        double datasetSizeMB = getTotalDatasetSizeMB(datasetPath);
    	double result1 = Double.parseDouble(String.format("%.4f", datasetSizeMB));
        
    	//Throughput
        double throughputMBs = datasetSizeMB / (elapsedTime / 1000.0); // Converted to seconds
        double result = Double.parseDouble(String.format("%.4f", throughputMBs));
        
        //Print the values
        System.out.println("Dataset size: " + result1 + " MB");
        System.out.println("Indexing Wall Time: " + elapsedTime + " milliseconds");
        System.out.println("Indexing Throughput: "+result+" MB/s");
        
        // Shutdown executor
        executor.shutdown();

    }
    
    private void indexDataset(File datasetDir, ExecutorService executor) {
        // Check if datasetDir is a directory
        if (!datasetDir.isDirectory()) {
            System.err.println("Invalid dataset path: " + datasetDir);
            return;
        }

        // Traverse dataset and submit indexing tasks to executor
        File[] files = datasetDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively index files in subfolders
                    indexDataset(file, executor);
                } else {
                    // Submit indexing task to executor
                    executor.submit(new IndexingTask(file));
                }
            }
        }
    }
    

    public List<SearchResult> search(String query) {
        long startTime = System.currentTimeMillis();
        List<String> terms = parseQuery(query);
        Map<File, Integer> fileOccurrences = new HashMap<>();

        // Initialize a set to store files containing all query terms
        Set<File> filesContainingAllTerms = new HashSet<>();

        for (String term : terms) {
            // Lookup occurrences of the term in the index
            Map<File, Integer> termOccurrences = indexStore.lookup(term);
            for (Map.Entry<File, Integer> entry : termOccurrences.entrySet()) {
                File file = entry.getKey();
                // Increment the frequency of occurrences for the file
                int frequency = entry.getValue();
                int currentFrequency = fileOccurrences.getOrDefault(file, 0);
                fileOccurrences.put(file, currentFrequency + frequency);
                // Check if the file contains all query terms
                if (!filesContainingAllTerms.contains(file)) {
                    boolean allTermsPresent = true;
                    for (String queryTerm : terms) {
                        if (!indexStore.lookup(queryTerm).containsKey(file)) {
                            allTermsPresent = false;
                            break;
                        }
                    }
                    // If all terms are present, add the file to the set
                    if (allTermsPresent) {
                        filesContainingAllTerms.add(file);
                    }
                }
            }
        }

        // Sort files by the total occurrences of query terms
        List<File> sortedFiles = new ArrayList<>(filesContainingAllTerms);
        sortedFiles.sort((file1, file2) -> fileOccurrences.get(file2).compareTo(fileOccurrences.get(file1)));

        // Create SearchResult objects for the top files
        List<SearchResult> searchResults = new ArrayList<>();
        for (File file : sortedFiles.subList(0, Math.min(sortedFiles.size(), 10))) {
            int occurrences = fileOccurrences.get(file);
            searchResults.add(new SearchResult(file.getName(), occurrences));
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Wall time for search: " + elapsedTime + " milliseconds");
        return searchResults;
    }



    private List<String> parseQuery(String query) {
        String[] terms = query.split("\\s+AND\\s+");
        return Arrays.asList(terms);
    }

    private class IndexingTask implements Runnable {
        private File file;

        public IndexingTask(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            indexFile(file);
        }
    }

    private void indexFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    String term = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    int currentFrequency = indexStore.lookup(term).getOrDefault(file, 0);
                    indexStore.update(term, file, currentFrequency + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static double getTotalDatasetSizeMB(String datasetPath) {
        File datasetDir = new File(datasetPath);
        return getTotalSizeInMB(datasetDir);
    }

    private static double getTotalSizeInMB(File file) {
        if (file.isFile()) {
            return (double) file.length() / (1024 * 1024);
        }

        double totalSize = 0.0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File subFile : files) {
                totalSize += getTotalSizeInMB(subFile);
            }
        }
        return totalSize;
    }

}

