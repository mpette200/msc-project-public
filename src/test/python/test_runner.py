import pathlib
import subprocess
import sys
from timeouts import decorate_runners, flat_test_list
from typing import List
import unittest

# define paths for mypy type checking
# relative to root
MYPY_PATHS = [
    'src/main/python/sklearn2pfa',
    'src/main/python/preprocess.py'
]

# define paths to search for unit tests
# relative to root
UNIT_TEST_PATHS = [
    'src/test/python/test_sklearn2pfa',
    'src/test/python/test_preprocess'
]

# define main path of source code
# relative to root
MAIN_PATH = 'src/main/python'

# can override the root path if necessary
# if None, automatically search for folder containing 'src'
ROOT_OVERRIDE = None

### EXPERIMENTAL ###
# if an individual test case exceeds timeout, raise error then skip to next.
# can be set to None to run test normally (without any timeout)
TIMEOUT_EACH = 5.0

STATUS_PASS = 0
STATUS_FAIL = 1


def find_root_path(start_dir, search_str):
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


def join_paths(*args):
    path = pathlib.Path().joinpath(*args)
    return str(path)


def main():
    print(sys.path[0])
    # define paths
    if ROOT_OVERRIDE is None:
        root_path = find_root_path(sys.path[0], 'src')
    else:
        root_path = ROOT_OVERRIDE
    
    # join path segments
    main_path = join_paths(root_path, MAIN_PATH)
    if main_path not in sys.path:
        sys.path.insert(0, main_path)
    mypy_paths = [join_paths(root_path, x) for x in MYPY_PATHS]
    unit_test_paths = [join_paths(root_path, x) for x in UNIT_TEST_PATHS]
    
    ### MYPY TESTS ###
    print('\nRunning mypy tests')
    # type hint helps with autocomplete in Visual Studio Code IDE
    mypy_results: List[subprocess.CompletedProcess] = []
    for x in mypy_paths:
        print(f'mypy --strict "{x}"', flush=True)
        res = subprocess.run(['mypy', '--strict', x])
        mypy_results.append(res)
    print('----------\n')
    
     # setup runners
    runner = unittest.TextTestRunner(sys.stdout, verbosity=2)
    loader = unittest.defaultTestLoader
    
    # discover tests
    test_list = [
        loader.discover(start_dir=p, top_level_dir=p)
        for p in unit_test_paths
    ]
    
    ### UNIT TESTS ###
    print('Running unit tests:\n', flush=True)
    if TIMEOUT_EACH is not None:
        print(f'Running with TIMEOUT_EACH={TIMEOUT_EACH}:\n', flush=True)
        decorate_runners(TIMEOUT_EACH, flat_test_list(test_list))
    suite = unittest.TestSuite(test_list)
    unit_test_result = runner.run(suite)
    
    if any(r.returncode != 0 for r in mypy_results) \
        or not unit_test_result.wasSuccessful():
        sys.exit(STATUS_FAIL)
    else:
        sys.exit(STATUS_PASS)


if __name__ == '__main__':
    main()
