import json
import token
from tokenize import generate_tokens
from io import StringIO
from typing import Any

def remove_whitespace(json_txt: str) -> str:
    '''
    Utility function to remove whitespace from JSON. Removes all newlines,
    indentation and unnecessary spaces. Adds a single space after commas
    and colons.
    '''
    out = []
    for t in generate_tokens(StringIO(json_txt).readline):
        if t.type in [token.NEWLINE, token.NL, token.INDENT, token.DEDENT]:
            continue
        if t.exact_type in [token.COMMA, token.COLON]:
            out.append(t.string + ' ')
        else:
            out.append(t.string)
    return ''.join(out)


def pretty_json(json_str: str,
                num_spaces: int=2,
                max_line_length: int=40) -> str:
    '''
    Reformat JSON string to include indentation.

    :param json_str: JSON string
    :param num_spaces: spacing between indentation levels
    :max_line_length: max active length of line (ignores leading whitespace)
                      before splitting across multiple lines
    :return: Reformatted string with indentation
    '''
    return _pretty_obj(
        json.loads(json_str),
        num_spaces=num_spaces,
        max_line_length=max_line_length
    )

def _pretty_obj(obj: Any,
                indent_level: int=0,
                add_new_line: bool=False,
                first_indent: int=0,
                num_spaces: int=2,
                max_line_length: int=40) -> str:
    '''Recursive helper to print json output with indentation.
    
    indent_level - controls the indentation
    add_new_line - begin with a new line but only when indent_level > 0
    first_indent - if not starting a new line, the current line may already
                   be indented so this controls the indent in that scenario
    num_spaces - number of spaces of each indent
    max_line_length - max active length of line (ignores leading whitespace)
                      before splitting across multiple lines
    '''
    spaces = num_spaces * ' '
    jobj = json.dumps(obj)
    if len(jobj) <= max_line_length:
        return jobj
    elif type(obj) is list:
        lines = []
        for i, x in enumerate(obj):
            if i == 0:
                line = _pretty_obj(x, 1 + indent_level, add_new_line=True)
            else:
                line = _pretty_obj(x, 1 + indent_level, first_indent=1)
            lines.append(line)
        delim0 = '['
        delim1 = ']'
    elif type(obj) is dict:
        lines = [_kv_to_json(k, v, 1 + indent_level) for k, v in obj.items()]
        delim0 = '{'
        delim1 = '}'
    else:
        return jobj
    # is either dict or list
    indent = spaces * indent_level
    elems = (',\n ' + indent).join(lines)
    txt = delim0 + elems + delim1
    if add_new_line:
        return '\n' + indent + txt
    elif first_indent > 0:
        return (spaces * first_indent)[:-1] + txt
    else:
        return txt

def _kv_to_json(key: str,
                value: Any,
                indent_level: int) -> str:
    '''
    Mutually recursive helper to output json with indentation
    '''
    json_rep = _pretty_obj(value, indent_level, add_new_line=True)
    return f'"{key}": {json_rep}'
