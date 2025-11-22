# TripleMatcher

## Overview
The **TripleMatcher** project is a Java-based tool designed for finding triple helix structures in RNA sequences.
It is divided into two main modules: **Matcher** and **3DFilter**.
### Modules
1. **Matcher**: A command-line tool for detecting triple helix looking at RNA secondary structures.
2. **3DFilter**: A command-line tool for filtering matches found by the Matcher module based on 3D structure information.
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

## Running the Matcher and 3DFilter Modules

To run the Matcher module, use the following command:
```bash
java -jar Matcher-v1.0-all-dependencies.jar <input_folder> <output_file>
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
java -jar 3DFilter-v1.0-all-dependencies.jar <matcher_output_file> <output_file>
```

If you are familiar with shell scripting, you can use the provided script to run both modules in one go:
```bash
bash example.sh <matcher_jar> <python_script> <3dfilter_jar>
```
on Windows, you can use the provided batch script:
```batch
example.bat <matcher_jar> <python_script> <3dfilter_jar>
```
make sure to change `config.conf` with your configuration file.

### Matcher Options
```aiignore
$> java -jar Matcher-v1.0-all-dependencies.jar -h 

usage: java -jar Matcher.jar <inputFolder> <output> [Options]
 -a          find all sub-matches of exact bond matches
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
A sequence of bonds must be consecutive, meaning that each bond must occur between 
adjacent nucleotide positions. In some cases, a sequence of bonds may consist of 
correct base pairs, but the positions are not adjacent.
For example, the sequence:
(U-13, A-18); (U-24, A-51); (U-25, A-50); (U-26, A-49); (U-27, A-48)
includes five consecutive UA base pairs. However, the first pair starts at 
position 13, while the second starts at position 24. In this case, the match is 
discarded due to non-adjacency.

By default, the Matcher does not include sub-matches when reporting exact matches, 
in order to avoid an excessive number of results.
In this example, the sub-match
(U-24, A-51); (U-25, A-50); (U-26, A-49); (U-27, A-48)
has adjacent positions and length 4, but would not be found under the default 
behavior.

The use of the `-a` option solves this issue by always enabling the detection of 
sub-matches in bonds, although this may lead to a large number of additional results. 
To manage this, the use of zones (see the `Combining Matcher and 3DFilter Output 
into Zones` section below) aggregates 
overlapping or adjacent sub-matches to facilitate the identification of regions 
that may contain triple helices.

### 3DFilter Options
```aiignore
$> java -jar 3DFilter-v1.0-all-dependencies.jar -h
usage: Usage: java -jar 3DFilter.jar <input_file> <output_file> [Options]
 -h,--help              print this message
 -p,--pdb-files <arg>   path to a folder containing PDB files, if not
                        provided, the program will download the files
 -t,--tolerance <arg>   tolerance in angstroms added to a base distance of
                        11.0 (e.g., distance < 11.0 + tolerance), default
                        is 0
```

In the case of the 3DFilter, the tolerance refers to the average distance between the 
unpaired nucleotide and the closest nucleotide in the base pair of the considered 
triple.
Typically, in a real triple, this distance does not exceed 11 angstroms. However, 
allowing a tolerance of one or two angstroms may be useful to capture edge cases.

## Output of TripleMatcher

The CSV output files contain the detected 2D and 3D matches, as detailed below.

### Output of the Matcher

The `Matcher` outputs 2D matches, i.e., combinations of unpaired nucleotide sequences and corresponding base-pair sequences (bonds) that plausibly form a triple helix according to a defined pattern. The main columns in the CSV output are `indices_seq` and `indices_bond`, each associated with a given `file_name`. Additional columns report alignment details and match lengths, depending on the tolerance options used.

Below is an example of two 2D matches identified in the PDB structure `2M8K`:

| `indices_seq`    | `indices_bond`                                 |
|------------------|------------------------------------------------|
| 31;32;33;34;35   | (17,42);(18,41);(19,40);(20,39);(21,38);(23,36) |
| 6;7;8;9;10;11    | (17,42);(18,41);(19,40);(20,39);(21,38);(23,36) |

The left column lists unpaired nucleotide positions (third strand), while the right column lists canonical WCF base pairs. In the first row, the unpaired region lies outside the annotated triple helix in `2M8K`; in the second, it matches the known Hoogsteen strand. In both cases, the paired region corresponds to a known triple helix.

### Output of the 3DFilter

The `3DFilter` retains the same columns as the `Matcher`, but adds spatial distance information and an augmented dot-bracket notation for matches that are geometrically feasible. This is called a 3D match.

Below is an example of spatial data from RNA structure `2M8K`, corresponding to the second 2D match above. Values are taken from the `distance_info` column and reorganized for readability:

| **Interaction A**   | **Interaction B**   | **Interaction C**   |
|---------------------|---------------------|---------------------|
| (11; 17; 14.96)     | (11; 42; 7.78)      | (17; 42; 11.06)     |
| (10; 18; 14.60)     | (10; 41; 7.77)      | (18; 41; 10.96)     |
| (9; 19; 14.82)      | (9; 40; 7.95)       | (19; 40; 10.40)     |
| (8; 20; 14.89)      | (8; 39; 8.50)       | (20; 39; 9.70)      |
| (7; 21; 14.57)      | (7; 38; 9.01)       | (21; 38; 9.76)      |
| (6; 23; 13.02)      | (6; 36; 8.49)       | (23; 36; 7.41)      |

Each entry is a triple of the form (index₁; index₂; distance), where the distance (in Ångströms) is the Euclidean distance between the C1′ atoms of the two nucleotides involved.
- **Interaction A**: distance between the unpaired third-strand nucleotide and the WCF base *not* involved in Hoogsteen pairing
- **Interaction B**: distance between the third strand and the Hoogsteen partner (typically <11 Å, average ≈ 8.25 Å)
- **Interaction C**: distance between the two bases forming each WCF pair

The `3DFilter` also adds a `triple_notation` column representing the **augmented dot-bracket notation** derived from the spatially validated triple helix:

`-----zyxwvu------------------------Z-YXWVU------`

This notation must be aligned with the RNA sequence and secondary structure in standard dot-bracket format. For example, for `2M8K`:

`GGUUUCUUUUUAGUGAUUUUUCCAAACCCCUUUGUGCAAAAAUCAUUA`<br>
`(((((......[[[[[[[[[[.[))))).......].]]]]]]]]]].`<br>
`-----zyxwvu------------------------Z-YXWVU------`

Here, each Hoogsteen base pair is represented by a pair of matching lowercase-uppercase letters (e.g., `zZ`), where the lowercase letter indicates the unpaired third-strand nucleotide, and the uppercase letter its Hoogsteen interaction partner in the WCF region.

The filtered 3D match corresponds exactly to the experimentally validated triple helix in `2M8K`.



## Combining Matcher and 3DFilter Output into Zones

The `Scripts/` folder contains a collection of Python scripts for grouping the
output of the `Matcher` and `3DFilter` components of the `TripleMatcher` tool
into *zones*.

Zones are defined as unions of sequences of indices (for unpaired nucleotides)
or base pairs (bonds) where every element in the union shares at least one
index with another, forming connected regions.

Each script is intended to be executed from the command line using appropriate
input and output CSV ore TXT files.

---

### Script: `compute_zones.py`

**Description:** Computes zones from sequences of matched unpaired nucleotides
and base pairs, and outputs them to text files.

**Usage:** ```bash python compute_zones.py --cand input.csv --izones
index_output.txt --bzones bond_output.txt ```

- `input.csv`: CSV file containing match data, including sequence or bond
  zones. - `index_output.txt`: Path where the computed index zones will be
  saved. - `bond_output.txt`: Path where the computed bond zones will be saved.

---

### Script: `candidate_zones_count.py`

**Description:** Aggregates candidate zones from CSV files listing index and
bond zone data. Produces output CSVs listing, for each RNA structure
(`file_name`), the zones and the number of matching elements in each.

**Usage:** ```bash python candidate_zones_count.py --cand index_zones.csv
--outizones index_zones_count.csv --outbzones bond_zones_count.csv ```

- `index_zones.csv`: Input file containing at least the columns `file_name`
  and `index_zone`. - `index_zones_count.csv`: Output path for aggregated index
  zone data. - `bond_zones_count.csv`: Output path for aggregated bond zone
  data.

---

### Script: `combine_zones.py`

**Description:** Combines index zones and bond zones for each RNA structure
into all possible zone combinations, producing a single CSV file.

**Usage:** ```bash python combine_zones.py index.csv bond.csv output.csv ```

- `index.csv`: Input file containing index zone definitions. - `bond.csv`:
  Input file containing bond zone definitions. - `output.csv`: Path to save the
  output file containing combined zone definitions.


## Contributing
Report any issue on github or create a pull request, we will review it as soon as possible.

## License
This project is licensed under the GNU General Public License v3.0 (GPL-3.0).
[LICENSE](LICENSE) file for details.


