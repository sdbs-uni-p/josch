"""
Acts as a interface between the Josch Java application and the JsonSubSchema (JSS) tool.

The general idea is to generate two schema.json files in the Josch user directory from the Java
application. This module then loads these two schema files, performs the schema containment check of
the JSS by interacting with its application programming interface (API) and stores the result as
another json file as specified.

It requires that 'jsonsubschema' module is installed within the Python environment you are running
this script with.

 Args:
    leftSchema (str): The path to the left schema of the view.

    rightSchema (str): The path to the right schema of the view.

    result (str): The path to the result file.

Returns:
     A message indicating success or failure.

Author:
    Kai Dauberschmidt
"""
import json
import sys

from jsonsubschema import api as jss


def main():
    """
    Performs a schema containment on two input schema args and stores the result into a result
    JSON document.
    """
    assert len(sys.argv) == 4, "The script requires 2 json schemas and a result as input."
    print(compare(sys.argv[1], sys.argv[2], sys.argv[3]))


def _load_json_schema(path):
    """
    Loads a JSON Schema from a file found at the path and returns it.

    Args:
        path (str): The path to the file.

    Returns:
        The corresponding JSON Schema fetched.
    """
    with open(path, 'r') as f:
        schema = json.load(f)
        return schema


def _write_result(result: str, path: str):
    """
    Stores a given result string in a result key within the given .json file found at the path.

    Args:
        result: The value to store within the result key.
        path: The path to the result file.
    """
    result = {"result": result}
    with open(path, 'w') as f:
        json.dump(result, f)


def compare(path1: str, path2: str, result: str):
    """
    Compares two JSON schemas and stores the result at the specified path.

    Args:
        path1: The path to the first JSON schema presented as a .json file.
        path2: The path to the second JSON schema presented as a .json file.
        result: The path to the result .json file.

    Returns:
        A message indicating success or failure of the process.

    Raises:
        IOError:
            
    """

    try:
        json_schema1 = _load_json_schema(path1)
        json_schema2 = _load_json_schema(path2)
    except IOError as error:
        return "Failure while loading the schemas.", error

    # evaluate the schemas.
    s1_sub_s2 = jss.isSubschema(json_schema1, json_schema2)
    s1_sup_s2 = jss.isSubschema(json_schema2, json_schema1)

    # The most strict scalar result of the containment check is chosen.
    if s1_sub_s2 and s1_sup_s2:
        msg = 'equivalent'
    elif s1_sub_s2:
        msg = 'subset'
    elif s1_sup_s2:
        msg = 'superset'
    else:
        msg = 'incomparable'

    try:
        _write_result(msg, result)
        return "Success."
    except IOError as error:
        return "Failure while saving the result " + msg + ".", error


if __name__ == "__main__":
    main()
