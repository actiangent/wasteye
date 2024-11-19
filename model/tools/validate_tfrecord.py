# From: https://stackoverflow.com/a/77783422

import tensorflow as tf
import argparse

def validate_tfrecord(file):
    try:
        for record in tf.data.TFRecordDataset(file):
            # Attempt to parse the record
            _ = tf.train.Example.FromString(record.numpy())
    except tf.errors.DataLossError as e:
        print(f"DataLossError encountered: {e}")
        return True
    except Exception as e:
        print(f"An error occurred: {e}")
        return True
    return False

if __name__ == "__main__":

    parser = argparse.ArgumentParser(description="Validate .tfrecord files.")
    parser.add_argument("file", type=str, help="Path to the tfrecord file")

    args = parser.parse_args()

    if validate_tfrecord(args.file):
      print(f"The TFRecord file {args.file} is corrupted.")
    else:
      print(f"The TFRecord file {args.file} is fine.")

    