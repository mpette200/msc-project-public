from typing import Any, Sequence, Optional, Type
from .pfa import PfaDoc
from .converters import register_converters
from .register_converters import Supported

from sklearn.utils.validation import check_is_fitted as sklearn_check_is_fitted # type: ignore

def to_pfa(model: Any,
           row_of_data: Any=None,
           type_info: Optional[Sequence[Type[type]]]=None,
           pfa_version: str='',
           options: Sequence[Any]=(None,)) -> PfaDoc:
    '''
    Takes a scikit-learn model and converts it to a PFA representation. The model must have
    already been fitted (trained) on the data.

    :param model: the scikit-learn model, which must have already been fitted to the data
    :param row_of_data: some converters need a row of data to infer the types of the input
    :param pfa_version: version string of the PFA specification. This argument is here
        in case it becomes necessary to support backward compatibility. If not provided
        then use latest PFA specification.
    :param options: provide additional options to the converter
    :return: a pfa document representing the model
    '''
    sklearn_check_is_fitted(model)
    converter = Supported.get_converter(model)
    return converter(model, row_of_data, type_info, pfa_version, options)

register_converters(Supported)
