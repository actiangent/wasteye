# From: https://github.com/datitran/raccoon_dataset/blob/master/xml_to_csv.py

import os
import glob
import pandas as pd
import xml.etree.ElementTree as ET
import argparse


def xml_to_csv(path):
    xml_list = []
    for xml_file in glob.glob(path + "/*.xml"):
        print(xml_file)
        tree = ET.parse(xml_file)
        root = tree.getroot()
        for member in root.findall("object"):
            value = (
                root.find("filename").text,
                int(root.find("size")[0].text),
                int(root.find("size")[1].text),
                member[0].text,
                int(member[4][0].text),
                int(member[4][1].text),
                int(member[4][2].text),
                int(member[4][3].text),
            )
            xml_list.append(value)

    column_name = [
        "filename",
        "width",
        "height",
        "class",
        "xmin",
        "ymin",
        "xmax",
        "ymax",
    ]
    xml_df = pd.DataFrame(xml_list, columns=column_name)
    return xml_df


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Create CSV data file from Pascal VOC annotation files."
    )
    parser.add_argument("path", type=str, help="Path to the folder containing files")
    parser.add_argument(
        "--directories",
        nargs="+",
        default=["train", "val"],
        help="List of directories to include (default: ['train', 'val'])",
    )

    args = parser.parse_args()

    dirs = args.directories
    if isinstance(dirs, str):
        extensions = [dirs]

    path = args.path
    for dir in dirs:
        image_path = os.path.join(os.getcwd(), (path + "/" + dir))
        print(image_path)
        xml_df = xml_to_csv(image_path)
        xml_df.to_csv((path + "/" + dir + "_labels.csv"), index=None)
        print("Successfully converted xml to csv.")

    # python tools/voc_to_csv.py data/annotated --directories val
