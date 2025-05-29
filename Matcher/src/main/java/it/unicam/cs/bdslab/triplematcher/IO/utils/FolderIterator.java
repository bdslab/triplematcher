package it.unicam.cs.bdslab.triplematcher.IO.utils;

import it.unicam.cs.bdslab.triplematcher.RNAInputFileParserException;
import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructure;
import it.unicam.cs.bdslab.triplematcher.RNASecondaryStructureFileReader;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FolderIterator implements Iterator<RNASecondaryStructure> {
    private Queue<Path> pathsQueue;
    private RNASecondaryStructure nextStructure;

    public FolderIterator(Path folder, boolean recursive) {
        this.pathsQueue = new LinkedList<>();
        if (recursive) {
            try {
                Files.walk(folder).filter(Files::isRegularFile).forEach(pathsQueue::add);
            } catch (IOException e) {
                throw new RuntimeException("[ERROR] walking through the folder", e);
            }
        } else {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
                for (Path entry : stream) {
                    if (Files.isRegularFile(entry)) {
                        pathsQueue.add(entry);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("[ERROR] reading the folder", e);
            }
        }
        advance();
    }

    @Override
    public boolean hasNext() {
        return nextStructure != null;
    }

    @Override
    public RNASecondaryStructure next() {
        if (nextStructure == null) {
            throw new NoSuchElementException();
        }
        RNASecondaryStructure currentStructure = nextStructure;
        advance();
        return currentStructure;
    }

    public List<RNASecondaryStructure> getAllStructures() {
        List<RNASecondaryStructure> structures = new ArrayList<>();
        while (hasNext()) {
            structures.add(next());
        }
        return structures;
    }

    private void advance() {
        nextStructure = null;
        while (!pathsQueue.isEmpty()) {
            Path path = pathsQueue.poll();
            RNASecondaryStructure structure = getStructure(path);
            if (structure != null) {
                nextStructure = structure;
                break;
            }
        }
    }

    private RNASecondaryStructure getStructure(Path path) {
        RNASecondaryStructure secondaryStructure = null;
        try {
            secondaryStructure = RNASecondaryStructureFileReader.readStructure(path.toString(), false);
            secondaryStructure.description = path.getFileName().toString();
        } catch (IOException | RNAInputFileParserException e) {
            System.err.println("[WARNING] Skipping file " + path + " ... " + e.getMessage());
        }
        return secondaryStructure;
    }
}