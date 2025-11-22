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
        merged_flag = False
        for i, m in enumerate(merged):
            if current & m:
                merged[i] = m | current
                merged_flag = True
                break
        if not merged_flag:
            merged.append(current)
    return merged

# Main logic for computing zones and saving to output
def compute_candidate_zones(candidates_df, out_izones_path, out_bzones_path):
    index_sets_by_file = defaultdict(list)
    bond_sets_by_file = defaultdict(list)

    for _, row in candidates_df.iterrows():
        fname = row['file_name']
        index_set = parse_indices(row['indices_seq'])
        bond_set = parse_bonds(row['indices_bond'])

        if index_set:
            index_sets_by_file[fname].append(index_set)
        if bond_set:
            bond_sets_by_file[fname].append(bond_set)

    # Process and save index zones
    izones_data = []
    for fname, sets in index_sets_by_file.items():
        zones = merge_overlapping_sets(sets[:])
        for zone in zones:
            count = sum(1 for s in sets if s & zone)
            izones_data.append({
                "file_name": fname,
                "index_zone": ";".join(map(str, sorted(zone))),
                "count": count
            })
    pd.DataFrame(izones_data).to_csv(out_izones_path, index=False)

    # Process and save bond zones
    bzones_data = []
    for fname, sets in bond_sets_by_file.items():
        zones = merge_overlapping_sets(sets[:])
        for zone in zones:
            count = sum(1 for s in sets if s & zone)
            bond_str = ";".join(f"({i},{j})" for i, j in sorted(zone))
            bzones_data.append({
                "file_name": fname,
                "bond_zone": bond_str,
                "count": count
            })
    pd.DataFrame(bzones_data).to_csv(out_bzones_path, index=False)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Compute index and bond zones from candidate data.")
    parser.add_argument("--cand", required=True, help="Path to candidate CSV file")
    parser.add_argument("--outizones", required=True, help="Path to output index zones CSV")
    parser.add_argument("--outbzones", required=True, help="Path to output bond zones CSV")
    args = parser.parse_args()

    df = pd.read_csv(args.cand, sep=",")
    compute_candidate_zones(df, args.outizones, args.outbzones)

