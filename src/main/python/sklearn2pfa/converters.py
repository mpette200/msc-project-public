from typing import Any, Sequence, List, Optional, Type, Dict
from collections import OrderedDict
from .pfa import PfaDoc, PfaCells, PfaAction
from .register_converters import Supported
from sklearn.tree import DecisionTreeRegressor # type: ignore
from sklearn.tree._tree import TREE_LEAF # type: ignore
from sklearn.base import BaseEstimator # type: ignore
import avro.schema # type: ignore
import dataclasses

def sklearn_decision_tree_regressor_2pfa(dt: DecisionTreeRegressor,
                                         row_of_data: Any,
                                         type_info: Optional[Sequence[Type[type]]],
                                         pfa_version: str,
                                         options: Sequence[Any]) -> PfaDoc:
    '''
    Convert a scikit-learn decision tree regressor model to pfa format.
    '''
    doc = PfaDoc()
    doc.set_name('sklearn_DecisionTreeRegressor')

    # implementation notes
    # scikit-learn decision tree regressors only work with numerical values
    # and those values are converted to floating point if integers.
    column_names = _get_sklearn_column_names(dt)
    column_types = ["double" for _ in column_names]
    column_schemas = _make_columns_schema(column_names, column_types)

    # define input schema
    input_type = '''
    {"type": "record",
     "name": "SingleRow",
     "fields": {{column_types}} }
    '''.replace('{{column_types}}', column_schemas)
    doc.set_input_from_json(input_type)

    # define output schema
    if dt.n_outputs_ > 1:
        raise NotImplementedError('multi-output regression not supported')
    else:
        output_type = '"double"'
        doc.set_output_from_json(output_type)

    # define tree nodes schema
    enum_schema = _make_enums_schema(column_names)
    treenode_type = '''
    {"type": "record",
     "name": "TreeNode",
     "fields":
        [{"type": {{enum_schema}}, "name": "field"},
         {"type": "string", "name": "operator"},
         {"type": "double", "name": "value"},
         {"type": ["TreeNode", "double"],
          "name": "pass"},
         {"type": ["TreeNode", "double"],
          "name": "fail"}]}
    '''.replace('{{enum_schema}}', enum_schema)
    
    # create tree nodes
    root_node = _sklearn_regression_tree(dt)
    node_data = root_node.to_json()

    cells = PfaCells()
    cells.add_cell('tree', treenode_type, node_data)
    doc.set_cells(cells)

    # define action
    action = '''
    {"model.tree.simpleTree": [
        "input",
        {"cell": "tree"}]}
    '''
    pfa_action = PfaAction(action)
    doc.set_action(pfa_action)

    return doc


def _get_sklearn_column_names(model: BaseEstimator) -> List[str]:
    '''
    Get column names from scikit-learn estimator if available,
    otherwise use names x0, x1, x2 ... etc.
    '''
    if hasattr(model, 'feature_names_in_'):
        column_names = list(model.feature_names_in_)
    else:
        column_names = [f'x{i}' for i in range(model.n_features_in_)]
    return column_names


def _make_columns_schema(col_names: Sequence[str],
                         col_types: Sequence[str]) -> str:
    '''
    Create a lists of schemas, each schema being an avro formatted string.
    Each schema denotes the type and name of the column.
    '''
    assert len(col_names) == len(col_types)
    info = []
    for name, col_type in zip(col_names, col_types):
        info.append('{"type": "%s", "name": "%s"}' % (col_type, name))
    return '[' + ', '.join(info) + ']'


def _make_enums_schema(col_names: Sequence[str]) -> str:
    '''
    Create a schema as an avro formatted string. Schema to be an enum of
    the provided names.
    '''
    quoted_names = [f'"{name}"' for name in col_names]
    names_list = '[' + ', '.join(quoted_names) + ']'
    enum_schema = '''
    {"type": "enum",
     "name": "ColumnNames",
     "symbols": {{names}} }
    '''.replace('{{names}}', names_list)
    return enum_schema


@dataclasses.dataclass
class RegressionTreeNode:
    '''
    Node to represent sklearn regression tree in PFA format.
    if column[x] <= value go left
    if column[x] > value go right
    
    Implementation note: dataclass decorator automatically generates
    the __init__ method
    '''
    _column_name: str
    _value: float
    _left: Optional['RegressionTreeNode'] = None
    _right: Optional['RegressionTreeNode'] = None

    def is_leaf(self) -> bool:
        # in scikit-learn both left and right are TREE_LEAF
        # so only need to check one
        return self._left is None

    def to_json(self) -> Any:
        '''
        Returns a python dict that represents the JSON form of decision tree
        used by the PFA function "model.tree.simpleTree".
        Except for leaf nodes, when just the value is returned
        '''
        if self.is_leaf():
            return self._value
        # type hint needed to avoid mypy errors
        node: Dict[str, Any] = OrderedDict()
        node['field'] = self._column_name
        node['operator'] = '<='
        node['value'] = self._value
        # type check needed to avoid mypy errors
        if self._left is not None and self._right is not None:
            node['pass'] = self._left.to_json()
            node['fail'] = self._right.to_json()
        return node


def _sklearn_regression_tree(dt: DecisionTreeRegressor,
                             i: int=0) -> RegressionTreeNode:
    '''
    Creates a tree of nodes from the scikit-learn model.
    The root node is at i=0.
    '''
    if dt.n_outputs_ > 1:
        raise NotImplementedError('multi-output regression not supported')
    else:
        value = dt.tree_.value[i][0][0]
    if dt.tree_.children_left[i] == TREE_LEAF:
        # in scikit-learn both left and right child are TREE_LEAF
        # so only need to check one
        return RegressionTreeNode('', value)
    
    col_number = dt.tree_.feature[i]
    col_name = _get_sklearn_column_names(dt)[col_number]
    threshold = dt.tree_.threshold[i]
    left_i = dt.tree_.children_left[i]
    right_i = dt.tree_.children_right[i]

    return RegressionTreeNode(
        col_name,
        threshold,
        _sklearn_regression_tree(dt, left_i),
        _sklearn_regression_tree(dt, right_i)
    )


def register_converters(supported: Type[Supported]) -> None:
    supported.register_converter(
        DecisionTreeRegressor(),
        sklearn_decision_tree_regressor_2pfa
    )
