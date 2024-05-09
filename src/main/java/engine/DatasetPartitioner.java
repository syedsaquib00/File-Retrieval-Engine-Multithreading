package engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatasetPartitioner {
    private int numPartitions;

    public DatasetPartitioner(int numPartitions) {
        this.numPartitions = numPartitions;
    }

    public List<List<File>> partitionDataset(String datasetPath) {
        List<List<File>> partitions = new ArrayList<>();

        File[] files = new File(datasetPath).listFiles();
        if (files != null) {
            for (int i = 0; i < numPartitions; i++) {
                partitions.add(new ArrayList<>());
            }

            int partitionIndex = 0;
            for (File file : files) {
                partitions.get(partitionIndex).add(file);
                partitionIndex = (partitionIndex + 1) % numPartitions;
            }
        }

        return partitions;
    }
}
