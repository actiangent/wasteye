import argparse
from tflite_support.metadata_writers import object_detector
from tflite_support.metadata_writers import writer_utils

def write_metadata_to_model(model_path, label_file, save_to_path):
    # Normalization parameters
    input_normalization_mean = 127.5
    input_normalization_std = 127.5

    # Create the metadata writer
    ObjectDetectorWriter = object_detector.MetadataWriter
    writer = ObjectDetectorWriter.create_for_inference(
        writer_utils.load_file(model_path), [input_normalization_mean], [input_normalization_std], [label_file])

    print(writer.get_metadata_json())

    # Populate the metadata into the model and save
    writer_utils.save_file(writer.populate(), save_to_path)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Add metadata to TFLite object detection model.")
    parser.add_argument("model_path", type=str, help="Path to the TFLite model file")
    parser.add_argument("labelmap_path", type=str, help="Path to the labelmap file")
    parser.add_argument("save_to_path", type=str, help="Path to save the TFLite model with metadata")
    
    args = parser.parse_args()

    write_metadata_to_model(args.model_path, args.labelmap_path, args.save_to_path)

    # python tflite_metadata_writer.py wasteye_quantized.tflite wasteye_labelmap.txt wasteye_quantized_metadata.tflite
