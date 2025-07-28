import pandas as pd
import argparse
import re
from collections import defaultdict

# Parses a string of indices into a set of integers
def parse_indices(index_str):
    try:
        return set(int(x.strip()) for x in index_str.split(";") if x.strip().isdigit())
    except:
        return set()

# Parses a string of bonds like "(16,43);(17,42)" into a set of (int, int) tuples
def parse_bonds(bond_str):
    try:
        pairs = re.findall(r"\((\d+),(\d+)\)", bond_str)
        return set((int(a), int(b)) for a, b in pairs)
    except:
        return set()

# Computes the list of maximally merged overlapping sets
def merge_overlapping_sets(sets):
    merged = []
    while sets:
        current = sets.pop()
        overlap_found = False
        for i, other in enumerate(merged):
            if current & other:
                merged[i] = other | current
                overlap_found = True
                break
        if not overlap_found:
            merged.append(current)
    return merged

# Main logic for computing zones and saving to output
def compute_zones_and_write(candidates_df, izones_file, bzones_file):
    # Group all index sets and bond sets by file_name
    index_sets_by_file = defaultdict(list)
    bond_sets_by_file = defaultdict(list)

    for _, row in candidates_df.iterrows():
        fname = row['file_name']
        seq_indices = parse_indices(row['indices_seq'])
        bond_pairs = parse_bonds(row['indices_bond'])

        if seq_indices:
            index_sets_by_file[fname].append(seq_indices)
        if bond_pairs:
            bond_sets_by_file[fname].append(bond_pairs)

    # Write index zones
    with open(izones_file, "w") as f:
        for fname, sets in index_sets_by_file.items():
            f.write(f"{fname}\n")
            zones = merge_overlapping_sets(sets)
            for zone in zones:
                f.write("  " + ";".join(map(str, sorted(zone))) + "\n")

    # Write bond zones
    with open(bzones_file, "w") as f:
        for fname, sets in bond_sets_by_file.items():
            f.write(f"{fname}\n")
            zones = merge_overlapping_sets(sets)
            for zone in zones:
                f.write("  " + ";".join(f"({i},{j})" for i, j in sorted(zone)) + "\n")

# Command-line interface
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Compute disjoint zones of indices and bonds from candidate CSV.")
    parser.add_argument("--cand", required=True, help="Candidate CSV file path")
    parser.add_argument("--izones", required=True, help="Output text file path for index zones")
    parser.add_argument("--bzones", required=True, help="Output text file path for bond zones")
    args = parser.parse_args()

    candidates_df = pd.read_csv(args.cand, sep=",")
    compute_zones_and_write(candidates_df, args.izones, args.bzones)
