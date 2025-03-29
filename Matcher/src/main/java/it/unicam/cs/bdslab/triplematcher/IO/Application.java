package it.unicam.cs.bdslab.triplematcher.IO;

import java.nio.file.Path;

public interface Application {
    void exportFile(Path input, Path output);
    void exportFolder(Path inputFolder, Path output);
}
