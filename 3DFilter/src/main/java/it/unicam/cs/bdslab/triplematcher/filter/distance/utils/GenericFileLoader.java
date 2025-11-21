package it.unicam.cs.bdslab.triplematcher.filter.distance.utils;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;
import org.biojava.nbio.structure.chem.ChemComp;
import org.biojava.nbio.structure.chem.ChemCompGroupFactory;
import org.biojava.nbio.structure.chem.ChemCompProvider;
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
    private static ChemCompProvider oldProvider;
    public GenericFileLoader(Path folder) {
        if (oldProvider == null) {
            oldProvider = ChemCompGroupFactory.getChemCompProvider();
        }
        ChemCompGroupFactory.setChemCompProvider(new FixChemCompProvider(oldProvider));

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
                    files.put(nameNoExtension.toLowerCase(), new FileContainer(filetype, file));
                }
            }
        }
    }


    public Structure getStructure(String accessionNumber) throws StructureException, IOException {
        return !files.containsKey(accessionNumber.toLowerCase())
                ? StructureIO.getStructure(accessionNumber)
                : loadGenericType(files.get(accessionNumber.toLowerCase()));
    }

    private Structure loadGenericType(FileContainer file) throws StructureException, IOException {
        logger.info("Using {}", ChemCompGroupFactory.getChemCompProvider());
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

    /**
     * This provider fixes the issue where first nucleotide may have an extra character.
     * For example, A5' should be treated as A, C3' as C, G2' as G, U1' as U, T7' as T.
     * This is done by checking if the name starts or ends with A, C, G, U, T and removing the extra character.
     */
    private static class FixChemCompProvider implements ChemCompProvider {

        private final ChemCompProvider oldProvider;

        public FixChemCompProvider(ChemCompProvider provider) {
            this.oldProvider = Objects.requireNonNull(provider);
        }

        private final String[] BASES = {"A", "C", "G", "U"};

        @Override
        public ChemComp getChemComp(String name) {

            if (name == null) return this.oldProvider.getChemComp(null);

            if (name.length() != 2) {
                return this.oldProvider.getChemComp(name);
            }

            // upper has length 2
            String upper = name.toUpperCase();
            for (String base : BASES) {
                if (upper.startsWith(base) || upper.endsWith(base)) {
                    upper = upper.substring(0, upper.length() - 1);
                    break;
                }
            }

            return this.oldProvider.getChemComp(upper);
        }
    }


}
