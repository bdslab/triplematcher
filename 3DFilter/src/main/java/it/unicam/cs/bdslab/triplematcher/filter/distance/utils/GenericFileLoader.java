package it.unicam.cs.bdslab.triplematcher.filter.distance.utils;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;
import org.biojava.nbio.structure.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GenericFileLoader {
    private static final Logger logger = LoggerFactory.getLogger("filtered");

    private final HashMap<String, FileContainer> files = new HashMap<>();
    private final List<String> supportedExtensions = Arrays.stream(StructureFiletype.values())
            .flatMap(t -> t.getExtensions().stream())
            .collect(Collectors.toList());

    public GenericFileLoader(Path folder) {
        if (folder != null) {
            File dir = folder.toFile();
            if (!dir.isDirectory()) {
                logger.error("The path {} is not a directory", folder);
                throw new IllegalArgumentException("The path " + folder + " is not a directory");
            }
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                String extension = getFileExtension(file);
                StructureFiletype filetype = getFileType(extension);
                if (file.isFile() && filetype != StructureFiletype.UNKNOWN) {
                    String nameNoExtension = file.getName().substring(0, file.getName().length() - 4);
                    files.put(nameNoExtension, new FileContainer(filetype, file));
                }
            }
        }
    }


    public Structure getStructure(String accessionNumber) throws StructureException, IOException {
        return !files.containsKey(accessionNumber)
                ? StructureIO.getStructure(accessionNumber)
                : loadGenericType(files.get(accessionNumber));
    }

    private Structure loadGenericType(FileContainer file) throws StructureException, IOException {
        LocalPDBDirectory reader = null;
        switch (file.fileType) {
            case PDB:
                reader = new PDBFileReader();
                break;
            case CIF:
                reader = new CifFileReader();
                break;
            case BCIF:
                reader = new BcifFileReader();
                break;
            case MMTF:
                reader = new MMTFFileReader();
                break;
            case UNKNOWN:
                logger.error("The file {} is not a valid PDB or CIF file", file);
                throw new IllegalArgumentException("The file " + file + " is not a valid PDB or CIF file");
        }
        return reader.getStructure(file.file);
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        } else {
            return "";
        }
    }

    private StructureFiletype getFileType(File file) {
        return getFileType(getFileExtension(file));
    }

    private StructureFiletype getFileType(String extension) {
        if (!extension.startsWith("."))
            extension = "." + extension;
        if (StructureFiletype.PDB.getExtensions().contains(extension)) {
            return StructureFiletype.PDB;
        } else if (StructureFiletype.CIF.getExtensions().contains(extension)) {
            return StructureFiletype.CIF;
        } else if (StructureFiletype.MMTF.getExtensions().contains(extension)) {
            return StructureFiletype.MMTF;
        } else if (StructureFiletype.BCIF.getExtensions().contains(extension)) {
            return StructureFiletype.BCIF;
        } else {
            return StructureFiletype.UNKNOWN;
        }
    }

    private static class FileContainer {
        private final StructureFiletype fileType;
        private final File file;
        public FileContainer(StructureFiletype fileType, File file) {
            this.fileType = fileType;
            this.file = file;
        }
    }

}
