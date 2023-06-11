import abc
import json
import avro.schema # type: ignore
from avro_json_serializer import AvroJsonSerializer # type: ignore
from collections import OrderedDict
from typing import Any, Union, Dict

class PfaBase(abc.ABC):
    '''
    Base class for all parts of the pfa document
    '''

    def _start_dict(self) -> Dict[str, Any]:
        '''
        Use to build a pythonized JSON object
        '''
        return OrderedDict()
    
    @abc.abstractmethod
    def to_json(self) -> Any:
        '''
        Convert part to pythonized JSON representation.
        '''
        pass

    def to_json_str(self) -> str:
        return json.dumps(
            self.to_json(),
            cls=ProxyJsonEncoder,
            sort_keys=False
        )


class ProxyJsonEncoder(json.JSONEncoder):
    def default(self,
                obj: Any) -> Any:
        if isinstance(obj, avro.schema.Schema):
            return obj.to_json()
        if isinstance(obj, avro.schema.MappingProxyType):
            return avro.schema.MappingProxyEncoder.default(self, obj)
        if isinstance(obj, PfaBase):
            return obj.to_json()
        return super().default(obj)


class PfaDoc(PfaBase):
    '''
    Represent the overall PFA document.
    '''
    _parts: Dict[str, Union[str, avro.schema.Schema, PfaBase]]

    def __init__(self) -> None:
        # use dict as a pythonized JSON
        self._parts = self._start_dict()
    
    def set_name(self,
                 name: str) -> None:
        assert type(name) is str
        self._parts['name'] = name
    
    def set_input(self,
                  schema: avro.schema.Schema) -> None:
        assert isinstance(schema, avro.schema.Schema)
        self._parts['input'] = schema

    def set_input_from_json(self,
                            schema: str) -> None:
        '''
        Set input to an Avro schema representing the data type of input
        provided to the scoring engine. Schema to be given as a JSON string.
        '''
        self.set_input(avro.schema.parse(schema))

    def set_output(self,
                  schema: avro.schema.Schema) -> None:
        assert isinstance(schema, avro.schema.Schema)
        self._parts['output'] = schema

    def set_output_from_json(self,
                            schema: str) -> None:
        '''
        Set output to an Avro schema representing the data type of output
        provided to the scoring engine. Schema to be given as a JSON string.
        '''
        self.set_output(avro.schema.parse(schema))

    def set_cells(self,
                  cells: 'PfaCells') -> None:
        '''
        Set cells to an object containing the cells of the PFA document.
        '''
        assert isinstance(cells, PfaCells)
        self._parts['cells'] = cells

    def set_action(self,
                   action: 'PfaAction') -> None:
        '''
        Set action to the given PfaAction object.
        '''
        assert isinstance(action, PfaAction)
        self._parts['action'] = action

    def to_json(self) -> Any:
        return self._parts


class PfaCell(PfaBase):
    '''
    Represent single cell from the PFA cells top-level field. Member values
    specify statically allocated, named, typed units of persistent
    state or embedded data.
    '''
    _parts: Dict[str, Any]

    def __init__(self,
                 avro_type: avro.schema.Schema) -> None:
        '''
        Create a cell object whose type is defined by the given schema 
        '''
        assert isinstance(avro_type, avro.schema.Schema)
        # use dict as a pythonized JSON
        self._parts = self._start_dict()
        self._parts['type'] = avro_type

    def set_initial_value(self,
                          value: Any) -> None:
        '''
        Set initial value of cell. In PFA this is the 'init' field of the cell.
        The value must be a python object that matches the schema defined for
        the cell.
        '''
        schema = self._parts['type']
        encoder = AvroJsonSerializer(schema)
        pythonized_json = encoder.to_ordered_dict(value)
        self._parts['init'] = pythonized_json

    def to_json(self) -> Any:
        return self._parts


class PfaCells(PfaBase):
    '''
    Represents collection of cells of the PFA top-level field. Each cell
    has a name and a definition as given by the PfaCell object.
    '''
    _cells: Dict[str, PfaCell]

    def __init__(self) -> None:
        self._cells = self._start_dict()
    
    def add_cell(self,
                 cell_name: str,
                 cell_type: str,
                 initial_value: Any) -> None:
        if cell_name in self._cells:
            raise ValueError(f'Cell {cell_name} already exists')
        cell_schema = avro.schema.parse(cell_type)
        cell = PfaCell(cell_schema)
        cell.set_initial_value(initial_value)
        self._cells[cell_name] = cell

    def to_json(self) -> Any:
        return self._cells


class PfaAction(PfaBase):
    '''
    Represents action definition of the PFA top-level field.
    '''
    _action: Any

    def __init__(self,
                 action_def: str) -> None:
        '''
        Create an action definition from the given JSON string. This
        constructor only checks the string is valid JSON. It does not
        do any validation that it represents a valid PFA expression or
        list of expressions.
        '''
        self._action = json.loads(action_def, object_hook=OrderedDict)

    def to_json(self) -> Any:
        return self._action
