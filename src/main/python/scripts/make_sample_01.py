import lzma
import pathlib
import sys

# can override the root path if necessary
# if None, automatically search for folder containing 'src'
ROOT_OVERRIDE = None

INPUT_FILE = 'data/latest/eu-west-1_2022-06-07T10_14_24.xz'
OUTPUT_FILE = 'data/latest/price_sample_01.tsv'


def find_root_path(start_dir: str,
                   search_str: str) -> str:
    '''
    Keep moving up one parent at a time, until the search str is
    found in the directory listing.
    '''
    path = pathlib.Path(start_dir).resolve()
    found = None
    while path.name != '':
        found = any(search_str in x.name for x in path.iterdir())
        if found:
            break
        path = path.parent
    if not found:
        raise LookupError(f'{search_str} not found in any parent of {start_dir}')
    return str(path)


def join_paths(*args: str) -> str:
    '''
    Wrapper around pathlibs joinpath. For example
    join_paths('a/b', 'c/d') -> 'a/b/c/d'
    '''
    path = pathlib.Path().joinpath(*args)
    return str(path)


def test_condition(line: str) -> bool:
    return 'g4ad' in line and 'Linux' in line


if __name__ == '__main__':
    if ROOT_OVERRIDE is None:
        root_path = find_root_path(sys.path[0], 'src')
    else:
        root_path = ROOT_OVERRIDE

    input_name = join_paths(root_path, INPUT_FILE)
    output_name = join_paths(root_path, OUTPUT_FILE)

    with lzma.open(input_name, mode='rt') as fp_i:
        with open(output_name, mode='wt') as fp_o:
            for line in fp_i:
                if test_condition(line):
                    fp_o.write(line)
