# TripleMatcher Project

## Overview
The **TripleMatcher** project is a Java-based tool designed for finding triple helix structures in RNA sequences.
It is divided into two main modules: **Matcher** and **3DFilter**.
### Modules
1. **Matcher**: A command-line tool for detecting triple helix looking at RNA secondary structures.
2. **3DFilter**: A command-line tool for filtering matches fond by the Matcher module based on 3D structure information.
---

## Getting Started

### Prerequisites
- **Java 8 or higher**
- **Maven 3.6 or higher**

If you need example molecules you can download the metadata in csv and molecule secondary structures in any format (db, ct, bpseq, aas) from the [PhyloRNA database](https://bdslab.unicam.it/phylorna)
### Build Instructions
To build the project, run the following command in the root directory:
```bash
mvn clean install
```
or download the release [here](https://github.com/bdslab/triplematcher/releases/latest).

### Running the Matcher and 3DFilter Modules

To run the Matcher module, use the following command:
```bash
java -jar Matcher/target/matcher-1.0-SNAPSHOT.jar <input_folder> <output_file>
```
the input folder must contain the RNA secondary structures, in various formats (.db, .aas, .bpseq, .ct).
If you have secondary structure in non-accepted format, you can use [TARNAS app](https://github.com/bdslab/TARNAS/releases/latest) or visit [TARNAS web](https://bdslab.unicam.it/tarnas).

With the output of the Matcher module, in order to use the 3DFilter module, you have to add 2 columns:
1. "Accession number": the pdb id of the RNA structure
2. "RNA Type": the chain used in the secondary structure file.

If you have an ".xlsx" or "csv" file which have these two columns, and a `Benchmark ID` column (which is the name of the file without extension), you can use the following command:
```bash
pip install pandas
pip install openpyxl
python3 Scripts/add_keys.py <matcher_file> <file_with_extra_info> <output_file>
```
otherwise, you have to add these columns manually.

To run the 3DFilter module, use the following command:
```bash
java -jar 3DFilter/target/3dfilter-1.0-SNAPSHOT.jar <matcher_output_file> <output_file>
```

If you are familiar with shell scripting, you can use the provided script to run both modules in one go:
```bash
bash Scripts/example.sh <matcher_jar> <python_script> <3dfilter_jar>
```
on Windows, you can use the provided batch script:
```batch
Scripts\example.bat <matcher_jar> <python_script> <3dfilter_jar>
```
make sure to change `config.conf` with your configuration file.

### Matcher Configuration
```aiignore
$> java -jar Matcher/target/matcher-1.0-SNAPSHOT.jar -h 

usage: java -jar Matcher.jar <inputFolder> <output> [Options]
 -b <arg>    canonical base pair (AU, UA, GC, CG), default UA
 -bt <arg>   base pair tolerance for mismatch/insertion/deletion, default
             1
 -ct <arg>   consecutive tolerance, default 1
 -h,--help   print this message
 -ml <arg>   minimum length of the pattern, default 4
 -n <arg>    nucleotide letter (A, C, G, U), default U
 -p <arg>    tolerance for pseudoknot, default: allow pseudoknot free
             matches
 -pt <arg>   paired tolerance, default 1
 -st <arg>   number of allowed mismatch/insertion/deletion on the
             sequence, default 1

```
### 3DFilter Configuration
```aiignore
$> java -jar 3DFilter/target/3dfilter-1.0-SNAPSHOT.jar -h
usage: Usage: java -jar 3DFilter.jar <input_file> <output_file> [Options]
 -h,--help              print this message
 -p,--pdb-files <arg>   path to a folder containing PDB files, if not
                        provided, the program will download the files
 -t,--tolerance <arg>   tolerance in angstroms added to a base distance of
                        11.0 (e.g., distance < 11.0 + tolerance), default
                        is 0
```

## Contributing
Report any issue on github or create a pull request, we will review it as soon as possible.

## License
This project is licensed under the GNU General Public License v3.0 (GPL-3.0).
[LICENSE](LICENSE) file for details.


