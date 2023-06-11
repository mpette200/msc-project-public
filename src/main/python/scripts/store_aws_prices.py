import argparse
import boto3
import time
import lzma
from botocore.config import Config
from datetime import datetime, timedelta, timezone

REGION = 'eu-west-1'
REDUCED = False
# If needed to reduce size of results try fewer instance types:
#     'c5.xlarge', 'c5.2xlarge', 'c5.4xlarge',
#     'm5.xlarge', 'm5.2xlarge', 'm5.4xlarge',
#     'r5.xlarge', 'r5.2xlarge', 'r5.4xlarge',
#     'c5a.xlarge', 'c5a.2xlarge', 'c5a.4xlarge',
#     'm5a.xlarge', 'm5a.2xlarge', 'm5a.4xlarge',
#     'r5a.xlarge', 'r5a.2xlarge', 'r5a.4xlarge'

def get_prices(num_days):
    'Fetch price history from Amazon AWS. num_days is number of days to fetch'
    my_config = Config(
        region_name = REGION,
        retries = {
            'max_attempts': 2,
            'mode': 'standard'
        }
    )

    ec2 = boto3.client('ec2', config=my_config)
    price_history = []
    response = None
    count = 0
    while True:
        count += 1
        print('... Retrieving Set {} ...'.format(count), flush=True)
        
        # setup argument list
        kwargs = {
            'MaxResults': 1000,
            'NextToken': response['NextToken'] if response else '',
            'StartTime': datetime.today() - timedelta(days=num_days)
        }
        # reduce result size
        if REDUCED:
            kwargs['InstanceTypes'] = ['c5.xlarge', 'c5.2xlarge', 'c5.4xlarge']
        
        # make api calls
        response = ec2.describe_spot_price_history(**kwargs)
        if response['ResponseMetadata']['HTTPStatusCode'] != 200:
            raise ConnectionError('Bad response: ' + str(response['ResponseMetadata']))
        price_history.extend(response['SpotPriceHistory'])
        if response['NextToken'] == '':
            break
        # wait to avoid hitting server
        time.sleep(0.1)
    return price_history


def write_prices(f, prices):
    'write prices in compressed lzma to output filename'
    for record in prices:
        f.write('SPOTINSTANCEPRICE' + '\t')
        f.write(record['SpotPrice'] + '\t')
        f.write(record['Timestamp']
            .astimezone(timezone.utc)
            .isoformat() + '\t')
        f.write(record['InstanceType'] + '\t')
        f.write(record['ProductDescription'] + '\t')
        f.write(record['AvailabilityZone'])
        f.write('\n')


class ActionDays(argparse.Action):
    'Class to raise error if argument is greater than 90'
    def __init__(self, option_strings, dest, nargs=None, const=None,
                default=None, type=None, choices=None,
                required=False, help=None, metavar=None):
        super().__init__(option_strings, dest, nargs, const,
                    default, type, choices, required, help, metavar)
    
    def __call__(self, parser, namespace, values, option_string):
        if values > 90:
            raise argparse.ArgumentError(
                self,
                'Spot price history only available for the past 90 days.'
            )
        setattr(namespace, self.dest, values)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Fetch EC2 spot prices from AWS and save compressed file to given path. '
            + 'Files are compressed in lzma format. File will be named based on region and '
            + 'time when data fetched.'
    )
    parser.add_argument(
        '--days',
        type=int,
        action=ActionDays,
        required=True,
        help='number of days past history to fetch. Max=90.'
    )
    parser.add_argument(
        'output_path',
        help='path to write output file'
    )
    args = parser.parse_args()

    current_time = datetime.utcnow().replace(microsecond=0).isoformat().replace(':', '_')
    filename = '{path}/{region}_{time}.xz'.format(
        path=args.output_path,
        region=REGION,
        time=current_time
    )
    # check that filepath is accessible before starting retrieval
    # because download can be quite slow
    with lzma.open(filename, mode='xt') as f:
      prices = get_prices(args.days)
      print('Writing Output.')
      write_prices(f, prices)
