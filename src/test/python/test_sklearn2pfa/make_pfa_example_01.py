'''
Script is intended to be manually run (not automated) to generate
an example PFA of a simple decision tree for testing
'''
import argparse
import numpy as np
from sklearn.tree import DecisionTreeRegressor
from sklearn2pfa import convert, utils


def make_pfa():
    x = np.array([
        [1.1, 2.2, 3.3, 4.4],
        [5.5, 4.4, 3.3, 2.2],
        [2.2, 2.2, 4.4, 5.5]
    ])
    y = np.array([7.0, 8.0, 9.0])
    dt = DecisionTreeRegressor()
    dt.fit(x, y)
    pfa_doc = convert.to_pfa(dt)
    return utils.pretty_json(pfa_doc.to_json_str())


def write_helper(fname, overwrite=False):
    mode = 'wt' if overwrite else 'xt'
    with open(fname, mode=mode) as fp:
        print(f'writing to: {fname}')
        fp.write(make_pfa())


def write_to_file(fname):
    try:
        write_helper(fname)
    except FileExistsError as e:
        print(f'File exists: {fname}')
        ans = input('Do you want to overwrite (Y/N)? ')
        if ans.lower() in ['y', 'yes']:
            write_helper(fname, overwrite=True)
        else:
            print('aborted')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Make example pfa and save to destination'
    )
    parser.add_argument(
        'destination',
        type=str,
        help='- Filename of destination'
    )
    args = parser.parse_args()
    
    write_to_file(args.destination)
