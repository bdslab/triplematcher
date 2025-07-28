
import pandas as pd
import argparse

def generate_zone_combinations(index_csv, bond_csv, output_csv):
    index_df = pd.read_csv(index_csv)
    bond_df = pd.read_csv(bond_csv)
    index_df = index_df[['file_name', 'index_zone']]
    bond_df = bond_df[['file_name', 'bond_zone']]
    output_rows = []
    for file_name in set(index_df['file_name']).intersection(bond_df['file_name']):
        index_zones = index_df[index_df['file_name'] == file_name]['index_zone'].unique()
        bond_zones = bond_df[bond_df['file_name'] == file_name]['bond_zone'].unique()
        for iz in index_zones:
            for bz in bond_zones:
                output_rows.append({'file_name': file_name, 'index_zone': iz, 'bond_zone': bz})
    output_df = pd.DataFrame(output_rows)
    output_df.to_csv(output_csv, index=False)
    print(f"Combinations saved in: {output_csv}")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Combine index and bond zones by file_name.")
    parser.add_argument("index_csv", help="Path to the index zone CSV file")
    parser.add_argument("bond_csv", help="Path to the bond zone CSV file")
    parser.add_argument("output_csv", help="Output CSV path")
    args = parser.parse_args()
    generate_zone_combinations(args.index_csv, args.bond_csv, args.output_csv)
