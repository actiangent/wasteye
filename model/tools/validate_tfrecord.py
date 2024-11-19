# From: https://gist.github.com/ed-alertedh/9f49bfc6216585f520c7c7723d20d951

import tensorflow as tf
import argparse

def validate_dataset(filenames, reader_opts=None):
    """
    Attempt to iterate over every record in the supplied iterable of TFRecord filenames
    :param filenames: iterable of filenames to read
    :param reader_opts: (optional) tf.python_io.TFRecordOptions to use when constructing the record iterator
    """
    i = 0
    for fname in filenames:
        print('validating ', fname)

        record_iterator = tf.python_io.tf_record_iterator(path=fname, options=reader_opts)
        try:
            for _ in record_iterator:
                i += 1
        except Exception as e:
            print('Error in {} at record {}'.format(fname, i))
            print(e)

if __name__ == "__main__":

    parser = argparse.ArgumentParser(description="Validate .tfrecord files.")
    parser.add_argument("file", type=str, help="Path to the tfrecord file")

    args = parser.parse_args()

    validate_dataset(args.folder_path)

    