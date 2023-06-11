import unittest
import numpy as np
from sklearn.tree import DecisionTreeRegressor
from sklearn2pfa.converters import sklearn_decision_tree_regressor_2pfa
from sklearn2pfa.convert import to_pfa
from titus.genpy import PFAEngine
from typing import Any

class TestConverters(unittest.TestCase):
    data_unlabelled: Any
    y_values: Any

    @classmethod
    def setUpClass(cls) -> None:
        cls.data_unlabelled = np.array([
            [1, 2, 3, 4, 5],
            [6, 7, 8, 9, 10],
            [11, 12, 13, 14, 15]
        ])
        cls.y_values = np.array([1, 2, 3])

    def test_sklearn_decision_tree_regressor_2pfa(self) -> None:
        dt = DecisionTreeRegressor()
        dt.fit(self.data_unlabelled, self.y_values)
        pfa_doc = sklearn_decision_tree_regressor_2pfa(
            dt,
            row_of_data=None,
            type_info=None,
            pfa_version='',
            options=(None,)
        )
        json_doc = pfa_doc.to_json_str()
        self.assertIn(
            '"type": "double"',
            json_doc
        )
        self.assertIn(
            '"name": "x4"',
            json_doc
        )
        self.assertIn(
            '"pass": {"double":',
            json_doc
        )
        self.assertIn(
            '"fail": {"double":',
            json_doc
        )

    def test_to_pfa_with_decision_tree_regressor(self) -> None:
        dt = DecisionTreeRegressor()
        dt.fit(self.data_unlabelled, self.y_values)
        pfa_doc = to_pfa(dt)
        json_doc = pfa_doc.to_json_str()
        self.assertIn(
            '"type": "double"',
            json_doc
        )
        self.assertIn(
            '"name": "x4"',
            json_doc
        )
        self.assertIn(
            '"pass": {"double":',
            json_doc
        )
        self.assertIn(
            '"fail": {"double":',
            json_doc
        )

    def test_using_titus_engine_matches_sklearn(self) -> None:
        dt = DecisionTreeRegressor()
        data = self.data_unlabelled
        dt.fit(data, self.y_values)

        pfa_doc = to_pfa(dt)
        json_doc = pfa_doc.to_json_str()
        # comma is necessary to unpack list item
        engine, = PFAEngine.fromJson(json_doc)
        
        names = ['x0', 'x1', 'x2', 'x3', 'x4']
        result0 = engine.action(dict(zip(names, data[0])))
        result1 = engine.action(dict(zip(names, data[1])))
        result2 = engine.action(dict(zip(names, data[2])))

        sklearn_val = dt.predict(data)

        self.assertAlmostEqual(result0, sklearn_val[0])
        self.assertAlmostEqual(result1, sklearn_val[1])
        self.assertAlmostEqual(result2, sklearn_val[2])
