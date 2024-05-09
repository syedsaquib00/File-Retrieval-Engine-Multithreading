package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class IndexingTask implements Runnable {
    private File file;
    private IndexStore indexStore;

    public IndexingTask(File file, IndexStore indexStore) {
        this.file = file;
        this.indexStore = indexStore;
    }

    @Override
    public void run() {
        indexFile(file);
    }

    private void indexFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    String term = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    synchronized (indexStore) {
                        int currentFrequency = indexStore.lookup(term).getOrDefault(file, 0);
                        indexStore.update(term, file, currentFrequency + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

