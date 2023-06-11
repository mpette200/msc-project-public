# sklearn2pfa

## Introduction
sklearn2pfa converts machine learning models in the [scikit-learn](https://scikit-learn.org/stable/)
format to [PFA](https://dmg.org/pfa/index.html). Once in the PFA format, PFA engines can be used
to make predictions.

## Folder Structure
../scripts  
Contains scripts for fetching price data from Amazon AWS. This is used as an example application,
and is not part of the sklearn2pfa package.

../sklearn2pfa  
Python code for the sklearn2pfa package.

## Usage Example
The package requires Python version 3.9 or greater.
To avoid polluting your Python base installation with packages it is recommended to create a
virtual environment either using venv [https://docs.python.org/3/library/venv.html](https://docs.python.org/3/library/venv.html)
or conda [https://conda.io/en/latest/index.html](https://conda.io/en/latest/index.html).  
Instructions below based on conda.

Create a Python environment using conda, Python version 3.9 or greater is required:
```bash
conda create --name tmp_demo python=3.9
```

In this example the name of the environment is `tmp_demo` which can be activated:
```bash
conda activate tmp_demo
```

Clone the repository into repo_clone
```bash
git clone https://github.com/Birkbeck/msc-computer-science-project-2021_22-mpette200.git repo_clone
```

Install from the clone of the repository:
```bash
python -m pip install repo_clone/
```

Then start a Python REPL:
```bash
python
```

Import modules from scikit-learn:
```python
import sklearn.datasets
import sklearn.tree
```

Import modules from the sklearn2pfa package:
```python
import sklearn2pfa.convert
import sklearn2pfa.utils
```

Create a sample dataset:
```python
X, y = sklearn.datasets.make_regression()
```

Create a decision tree model in scikit-learn and fit to the data:
```python
dt = sklearn.tree.DecisionTreeRegressor(max_depth=5)
dt.fit(X, y)
```

Use the sklearn2pfa package to convert to pfa:
```python
pfa_obj = sklearn2pfa.convert.to_pfa(dt)
pfa_json = pfa_obj.to_json_str()
```

Use the sklearn2pfa utilities to print an indented JSON string:
```python
indented_json = sklearn2pfa.utils.pretty_json(pfa_json)
print(indented_json)
```

Sample output from beginning:
```json
{"name": "sklearn_DecisionTreeRegressor",
 "input":
  {"type": "record",
   "name": "SingleRow",
   "fields":
    [{"type": "double", "name": "x0"},
     {"type": "double", "name": "x1"},
"...",
"..."
]}}
```

Sample output of tree data:
```json
{"TreeNode":
  {"field": "x4",
   "operator": "<=",
   "value": 0.4177730083465576,
   "pass":
    {"TreeNode":
      {"field": "x69",
       "operator": "<=",
       "value": -1.551289141178131,
       "pass":
        {"TreeNode":
          {"field": "x2",
           "operator": "<=",
           "value": -0.17887511290609837,
           "pass": {"double": -432.772673611623},
           "fail": {"double": -450.4591132038446}}},
"...":"...",
"...":"..."
}}}}
```
