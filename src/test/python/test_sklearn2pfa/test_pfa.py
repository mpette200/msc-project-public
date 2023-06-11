import unittest
from sklearn2pfa.pfa import PfaDoc
from sklearn2pfa.utils import remove_whitespace

class TestPfaDoc(unittest.TestCase):

    test_schema_1 = '''
    {"type": "record",
     "name": "TreeNode",
     "fields": 
      [
        {"type": 
          {"type": "enum",
           "name": "TreeFields",
           "symbols": ["one", "two", "three"]},
         "name": "field"},
       {"type": "string", "name": "operator"},
        {"type": ["double", "string"],
         "name": "value"},
        {"type": ["string", "TreeNode"],
         "name": "pass"},
        {"type": ["string", "TreeNode"],
         "name": "fail"}]}
    '''

    # incorrect schema because enum missing name
    test_schema_2_error = '''
    {"type": "record",
     "name": "TreeNode",
     "fields": 
      [
        {"type": 
          {"type": "enum",
           "symbols": ["one", "two", "three"]},
         "name": "field"}]}
    '''

    def test_doc_w_name_and_type(self) -> None:
        doc = PfaDoc()
        doc.set_name('UnitTest Name')
        doc.set_input_from_json(self.test_schema_1)
        
        json_doc = doc.to_json_str()
        self.assertIn('UnitTest Name', json_doc)
        self.assertIn('TreeNode', json_doc)

        expected = remove_whitespace('''
            {"name": "UnitTest Name",
             "input": {{schema}} }
        '''.replace('{{schema}}', self.test_schema_1))
        self.assertEqual(remove_whitespace(json_doc), expected)

    def test_doc_w_bad_input_schema(self) -> None:
      doc = PfaDoc()
      with self.assertRaises(Exception):
        doc.set_input_from_json(self.test_schema_2_error)
