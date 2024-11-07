import os
import argparse

def rename_files(path, prefix="", extensions=[".jpg"]):
    """
    Rename all image files in a folder to a sorted and sequential format.
    
    Args:
        path (str): The path to the folder containing image files.
        prefix (str): The prefix for the new filenames. Defaults to 'image_'.
        extensions (str or list): File extension(s) to filter by. Accepts a single extension as a string or a list of extensions.
    """

    # List all files in the directory
    files = [f for f in os.listdir(path) if os.path.isfile(os.path.join(path, f))]

    # Filter for image files with the specified extensions
    image_files = [f for f in files if any(f.lower().endswith(ext) for ext in extensions)]

    # Rename files sequentially
    for index, old_name in enumerate(image_files, start=1):
        # Create new name with prefix and index, preserving the original extension
        extension = os.path.splitext(old_name)[1].lower() # Get the original file's extension and convert it to lowercase
        new_name = f"{prefix}-100{index}{extension}"
        
        # Full path for old and new filenames
        old_path = os.path.join(path, old_name)
        new_path = os.path.join(path, new_name)

        # Rename the file
        os.rename(old_path, new_path)
        print(f"Renamed '{old_name}' to '{new_name}'")

    print("Renaming complete.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Rename files in a specified folder.")
    parser.add_argument("path", type=str, help="Path to the folder containing files")
    parser.add_argument("--prefix", type=str, default="", help="Prefix for the renamed files (default: '')")
    parser.add_argument("--extensions", nargs='+', default=[".jpg"], help="List of file extensions to include (default: ['.jpg'])")

    args = parser.parse_args()

    rename_files(args.path, args.prefix, args.extensions)
    # prefix = 1063

    # python rename_files.py ../data/raw/train --prefix=1063
