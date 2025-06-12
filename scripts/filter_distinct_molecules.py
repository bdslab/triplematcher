import pandas as pd
import argparse

def main(path, out):
    df = pd.read_csv(path)
    # Drop duplicates based on both id_from_file and length
    df = df.drop_duplicates(subset=["id_from_file", "length"])
    df.to_csv(out, index=False, sep=",")
    print("Distinct molecules saved in:", out)
    print("Total distinct molecules:", len(df))

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Filter match CSV file and keep distinct molecules based on id_from_file and length.")
    parser.add_argument("path", help="Path to the input match CSV file")
    parser.add_argument("out", help="Path to save the filtered distinct molecules")
    args = parser.parse_args()

    # Ensure the output file ends with .csv
    if args.out is not None and not args.out.endswith(".csv"):
        args.out += ".csv"

    main(args.path, args.out)
    