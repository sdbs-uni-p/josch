import unittest
import os
import sys

root = os.path.abspath(os.path.dirname(os.path.dirname(os.path.abspath(
    __file__))))
sys.path.append(root)

from i_jss import _load_json_schema
from i_jss import compare


class IJssTest(unittest.TestCase):
    """
    Tests the compare() function from i_jss with fixed schemas found at '/schemas/'.
    """

    def test_equiv(self):
        """ Tests equivalence by reflexivity. """
        result = compare('schemas/s1.json', 'schemas/s1.json', 'schemas/result.json')
        self.assertEqual(result, "Success.")
        result = _load_json_schema('schemas/result.json')
        self.assertEqual(result["result"], "equivalent")

    def test_superset(self):
        """ Tests superset with s1 being a superset of s2 """
        result = compare('schemas/s1.json', 'schemas/s2.json', 'schemas/result.json')
        self.assertEqual(result, "Success.")
        result = _load_json_schema('schemas/result.json')
        self.assertEqual(result["result"], "superset")

    def test_subset(self):
        """ Tests super set with s2 being a subset of s1 """
        result = compare('schemas/s2.json', 'schemas/s1.json', 'schemas/result.json')
        self.assertEqual(result, "Success.")
        result = _load_json_schema('schemas/result.json')
        self.assertEqual(result["result"], "subset")

    def test_difference(self):
        """ Tests super set with s1 being a superset of s2 """
        result = compare('schemas/s1.json', 'schemas/s3.json', 'schemas/result.json')
        self.assertEqual(result, "Success.")
        result = _load_json_schema('schemas/result.json')
        self.assertEqual(result["result"], "incomparable")
        result = compare('schemas/s2.json', 'schemas/s3.json', 'schemas/result.json')
        self.assertEqual(result, "Success.")
        result = _load_json_schema('schemas/result.json')
        self.assertEqual(result["result"], "incomparable")


if __name__ == '__main__':
    unittest.main()
