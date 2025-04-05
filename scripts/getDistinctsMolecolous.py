import pandas as pd
import argparse 
def main(path, out):
    df = pd.read_csv(path)
    df = df.drop_duplicates("length")
    df.to_csv(out, index=False, sep=",")
    print("Distincts molecolous saved in: ", out)
    print("Total distincts molecolous: ", len(df))

if __name__ == "__main__":
    parse = argparse.ArgumentParser(description="Filter a match csv file and get distincts molecolous")
    parse.add_argument("path", help="Path to the match csv file")
    parse.add_argument("out", help="Path to save the distincts molecolous")
    args = parse.parse_args()
    if args.out is not None and args.out[:3] != "csv":
        args.out = args.out + ".csv"
    main(args.path, args.out)
    