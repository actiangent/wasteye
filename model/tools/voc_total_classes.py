import os
import xml.etree.ElementTree as ET
from collections import Counter

def count_classes_in_voc(folder_path):
    """
    Count the total number of classes in Pascal VOC XML annotation files.

    Args:
        folder_path (str): The path to the folder containing .xml files.

    Returns:
        dict: A dictionary with class names as keys and their counts as values.
    """
    class_counter = Counter()

    # List all .xml files in the directory
    files = [f for f in os.listdir(folder_path) if f.endswith('.xml')]

    for file in files:
        file_path = os.path.join(folder_path, file)
        try:
            # Parse the XML file
            tree = ET.parse(file_path)
            root = tree.getroot()

            # Extract all object classes
            for obj in root.findall('object'):
                class_name = obj.find('name').text
                class_counter[class_name] += 1

        except ET.ParseError as e:
            print(f"Error parsing {file}: {e}")
        except Exception as e:
            print(f"Error processing {file}: {e}")

    return dict(class_counter)

if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="Count total classes in Pascal VOC XML files.")
    parser.add_argument("folder_path", type=str, help="Path to the folder containing Pascal VOC .xml annotation files")

    args = parser.parse_args()

    # Count classes
    class_counts = count_classes_in_voc(args.folder_path)

    # Print the results
    print("Class Counts:")
    for class_name, count in class_counts.items():
        print(f"{class_name}: {count}")

    # python tools/voc_total_classes.py data/annotated/train