#!/usr/bin/env python3
"""
Prune unused textures from the assets folder.

This script will recursively traverse the assets folder and remove any PNG files that are not referenced in any of the JSON files.

Run with --dry-run to see what would be removed without actually removing anything.
"""

import os
import sys
import json
from itertools import chain

def main(dry_run=False):
    found_textures = set()

    for root, dirs, files in chain(os.walk("src/generated/resources/assets/create/models"),os.walk("src/main/resources/assets/create/models")):
        for file in files:
            if not file.endswith(".json"):
                continue

            with open(os.path.join(root, file), "r") as f:
                data = json.load(f)
                for key, texture in data.get("textures", {}).items():
                    found_textures.add(texture)

    deleted_textures = 0

    for root, dirs, files in os.walk("src/main/resources/assets/create/textures"):
        for file in files:
            path = os.path.join(root, file)
            filename, extension = os.path.splitext(file)
            if not (extension == ".png" or filename.endswith(".png")) or "/gui/" in path:
                continue

            id = "create:" + path[len("src/main/resources/assets/create/textures/"):].split(".")[0]
            if id.endswith("_connected"):
                id = id[:-len("_connected")]

            if id not in found_textures:
                if dry_run:
                    print(f"Would delete {path} ({id})")
                else:
                    os.remove(path)
                deleted_textures += 1

    action = "would delete" if dry_run else "deleted"
    print(f"Found {len(found_textures)} textures, {action} {deleted_textures} textures.")

if __name__ == "__main__":
    main(len(sys.argv) > 1 and sys.argv[1] == "--dry-run")
