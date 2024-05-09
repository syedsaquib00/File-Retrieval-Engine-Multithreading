# File-Retrieval-Engine-Multithreading

**Description:**
The File Retrieval Engine, a Java-based application, serves as a demonstration platform for distributed systems architectures and multithreading principles. It excels in efficiently indexing and retrieving files from datasets, leveraging parallel processing techniques for enhanced performance.

**Directory Structure:**
```
File-Retrieving-Engine/
│
├── src/main/java/engine                   # Source code files
│   ├── AppInterface.java  # Command-line interface implementation
│   ├── ProcessingEngine.java  # File indexing and search functionalities
│   ├── IndexStore.java    # Global index management
│   ├── IndexingTask.java   # Indexing functionality
│   └── DataPartitioning.java  # Data partitioning logic
│
├── model/              # Additional model files
│   ├── searchResult.java
│   
│   
│
├── README.md              # Manual providing build and execution instructions
│
└── Datasets              # Sample datasets for testing
    ├── Dataset1
    ├── Dataset2
    ├── Dataset3
    ├── Dataset4
    └── Dataset5
```

**How to Build:**
1. Clone the repository to your local machine: `git clone https://github.com/your-username/File-Retrieving-Engine.git`
2. Navigate to the project directory: `cd File-Retrieving-Engine`
3. Compile the Java source files:
   ```
   javac src/main/java/engine/*.java
   ```

**How to Run:**
1. Ensure that the datasets to be indexed are located in the project root directory.
2. Create a .jar file of the project.
3. Execute the program:
   ```
   java -jar file-retrieval-engine.jar
   ```
4. Follow the command-line prompts to index files or search for specific terms within the datasets.

This structure emphasizes the functionality and organization of the File Retrieval Engine while providing clear instructions for building and executing the program.
