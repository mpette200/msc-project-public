# Historical Price Fetching Script
## Note About API Keys
API Keys only need access to a single endpoint, the 'DescribeSpotPriceHistory' endpoint.

## Usage Demonstration
First setup credentials as environment variables  
Bash Shell
```bash
export AWS_ACCESS_KEY_ID=xxxxxxxx
export AWS_SECRET_ACCESS_KEY=xxxxxxxx
```
or Windows Shell
```cmd
set AWS_ACCESS_KEY_ID=xxxxxxxx
set AWS_SECRET_ACCESS_KEY=xxxxxxxx
```

The package requires Python version 3.9 or greater.
To avoid polluting your Python base installation with packages it is recommended to create a
virtual environment either using venv [https://docs.python.org/3/library/venv.html](https://docs.python.org/3/library/venv.html)
or conda [https://conda.io/en/latest/index.html](https://conda.io/en/latest/index.html).  
Instructions below based on conda.

Create a Python environment using conda, Python version 3.9 or greater is required:
```bash
conda create --name tmp_demo_script python=3.9
```

In this example the name of the environment is `tmp_demo_script` which can be activated:
```bash
conda activate tmp_demo_script
```

Clone the repository into repo_clone
```bash
git clone https://github.com/Birkbeck/msc-computer-science-project-2021_22-mpette200.git repo_clone
```

Then change directory into repo_clone
```bash
cd repo_clone
```

Then install requirements
```bash
pip install -r src/main/python/requirements.txt
```

Then change to script directory
```bash
cd src/main/python/scripts
```

Display usage information
```bash
python store_aws_prices.py -h
```

Context help
```text
usage: store_aws_prices.py [-h] --days DAYS output_path

Fetch EC2 spot prices from AWS and save compressed file to given path. Files are compressed in
lzma format. File will be named based on region and time when data fetched.

positional arguments:
  output_path  path to write output file

optional arguments:
  -h, --help   show this help message and exit
  --days DAYS  number of days past history to fetch. Max=90.
```

Try fetching too many days
```bash
python store_aws_prices.py --days 91 tmp_store
```

Error message
```text
usage: store_aws_prices.py [-h] --days DAYS output_path
store_aws_prices.py: error: argument --days: Spot price history only available for the past 90 days.
```

Fetch historical prices
```bash
python store_aws_prices.py --days 1 tmp_store
```

Example output
```text
... Retrieving Set 1 ...
... Retrieving Set 2 ...
... Retrieving Set 3 ...
... Retrieving Set 4 ...
... Retrieving Set 5 ...
... Retrieving Set 6 ...
... Retrieving Set 7 ...
... Retrieving Set 8 ...
... Retrieving Set 9 ...
... Retrieving Set 10 ...
... Retrieving Set 11 ...
... Retrieving Set 12 ...
... Retrieving Set 13 ...
... Retrieving Set 14 ...
... Retrieving Set 15 ...
... Retrieving Set 16 ...
... Retrieving Set 17 ...
Writing Output.
```

File is now saved under ../tmp_store
