from typing import Callable, Any, Dict
from .pfa import PfaDoc

Converter_Type = Callable[..., PfaDoc]

class Supported:
    '''
    Holds a mapping of supported scikit-learn model types. Should never be instantiated, so
    instead use only the classmethods or staticmethods
    '''
    _models: Dict[str, Converter_Type] = {}

    def __init__(self) -> None:
        raise NotImplementedError('class has only static methods')

    @classmethod
    def register_converter(cls,
                            model: Any,
                            converter: Converter_Type) -> None:
        cls._models[cls.get_name(model)] = converter

    @classmethod
    def unregister_converter(cls,
                            model: Any) -> None:
        del cls._models[cls.get_name(model)]

    @staticmethod
    def get_name(model: Any) -> str:
        # type hint is needed for mypy type checker
        name: str = model.__class__.__name__
        return name

    @classmethod
    def get_converter(cls,
                       model: Any) -> Converter_Type:
        try:
            return cls._models[cls.get_name(model)]
        except KeyError:
            supported_models = list(cls._models.keys())
            raise LookupError(
                f'Cannot find converter for {model}. ' + \
                f'List of supported models: {supported_models}'
            ) from None
