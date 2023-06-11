import unittest
from typing import Any, cast
from sklearn2pfa.convert import Supported, to_pfa
from sklearn2pfa.pfa import PfaDoc


class Mock1Fitted:
    '''scikit-learn models follow the convention that if a model is fitted
    it will have at least one attribute that ends with an underscore and 
    does not start with double underscore. Example 'coeff_'
    '''
    def __init__(self) -> None:
        self.coeff_ = 1.0

    def fit(self, _: Any) -> None:
        return None


class Mock2Unregistered:
    pass


class Mock3Unfitted:
    def fit(self, _: Any) -> None:
        return None


def mock_converter(*_: Any) -> PfaDoc:
    # cast needed to avoid mypy errors
    mock = cast(PfaDoc, 'called mock')
    return mock


class TestSupported(unittest.TestCase):
    mock1: Mock1Fitted
    mock_unregistered: Mock2Unregistered

    @classmethod
    def setUpClass(cls) -> None:
        cls.mock1 = Mock1Fitted()
        cls.mock_unregistered = Mock2Unregistered()
        Supported.register_converter(cls.mock1, mock_converter)
    
    @classmethod
    def tearDownClass(cls) -> None:
        Supported.unregister_converter(cls.mock1)

    def test_register_and_get(self) -> None:
        converter = Supported.get_converter(self.mock1)
        self.assertEqual('called mock', converter())

    def test_get_non_existing_fails(self) -> None:
        with self.assertRaisesRegex(LookupError, 'supported'):
            Supported.get_converter(self.mock_unregistered)


class TestToPfa(unittest.TestCase):
    mock1: Mock1Fitted
    mock_unfitted: Mock3Unfitted
    
    @classmethod
    def setUpClass(cls) -> None:
        cls.mock1 = Mock1Fitted()
        cls.mock_unfitted = Mock3Unfitted()
        Supported.register_converter(cls.mock1, mock_converter)
        Supported.register_converter(cls.mock_unfitted, mock_converter)

    @classmethod
    def tearDownClass(cls) -> None:
        Supported.unregister_converter(cls.mock1)
        Supported.unregister_converter(cls.mock_unfitted)
    
    def test_to_pfa_calls_converter(self) -> None:
        self.assertEqual('called mock', to_pfa(self.mock1))
    
    def test_to_pfa_fails_if_not_fitted(self) -> None:
        # scikit-learn NotFittedError subclasses ValueError and AttributeError
        with self.assertRaises((ValueError, AttributeError)):
            to_pfa(self.mock_unfitted)
