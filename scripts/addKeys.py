import pandas as pd
import argparse
"Rna Type"
def main(path_matches, path_ids_xlsx, out): 
    df_matches = pd.read_csv(path_matches)
    df_ids = pd.read_excel(path_ids_xlsx)
    df_out = df_matches.merge(df_ids, left_on="FileName", right_on="Benchmark ID", how="inner")
    colums_to_drop = df_ids.columns.delete(df_ids.columns.get_loc("Accession number"))
    colums_to_drop = colums_to_drop.delete(colums_to_drop.get_loc("Rna Type"))
    df_out = df_out.drop(columns=colums_to_drop)
    df_out.to_csv(out, index=False, sep=",")
    print("Merged file saved in: ", out)
    print("Total merged molecolous: ", len(df_out))

if __name__ == "__main__":
    parse = argparse.ArgumentParser(description="Merge a match csv file with a xlsx file")
    parse.add_argument("path_matches", help="Path to the match csv file")
    parse.add_argument("path_ids_xlsx", help="Path to the xlsx file with the ids")
    parse.add_argument("out", help="Path to save the merged molecolous")
    args = parse.parse_args()
    if args.out is not None and not args.out.endswith(".csv"):
        args.out = args.out + ".csv"
    main(args.path_matches, args.path_ids_xlsx, out=args.out)