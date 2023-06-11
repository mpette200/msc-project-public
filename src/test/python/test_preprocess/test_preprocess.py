import pandas as pd
from preprocess import add_category_column, resample_timestamps
import unittest

class TestTransformations(unittest.TestCase):

    def test_add_category_column_names_match(self) -> None:
        df1 = pd.DataFrame(
            [[0.407400, 'c5n.2xlarge', 'SUSE Linux', 'eu-west-1b'],
            [0.160300, 'm6i.xlarge', 'Red Hat Enterprise Linux', 'eu-west-1c']],
        columns=['price', 'instance_type', 'instance_description', 'region_zone']
        )
        df2 = add_category_column(df1)
        column_names = ['instance_type', 'instance_description', 'region_zone', 'category']
        self.assertListEqual(
            ['c5n.2xlarge', 'SUSE Linux', 'eu-west-1b', 'c5n.2xlarge__SUSE Linux__eu-west-1b'],
            list(df2.loc[0, column_names])
        )
        self.assertListEqual(
            ['m6i.xlarge', 'Red Hat Enterprise Linux', 'eu-west-1c',
             'm6i.xlarge__Red Hat Enterprise Linux__eu-west-1c'],
            list(df2.loc[1, column_names])
        )

    def test_add_category_column_prices_match(self) -> None:
        df1 = pd.DataFrame(
            [[0.407400, 'c5n.2xlarge', 'SUSE Linux', 'eu-west-1b'],
            [0.160300, 'm6i.xlarge', 'Red Hat Enterprise Linux', 'eu-west-1c']],
        columns=['price', 'instance_type', 'instance_description', 'region_zone']
        )
        df2 = add_category_column(df1)
        self.assertAlmostEqual(
            0.407400,
            df2['price'][0]    
        )
        self.assertAlmostEqual(
            0.160300,
            df2['price'][1]    
        )

    def make_example_dataframe(self) -> pd.DataFrame:
        df1 = pd.DataFrame(
            [['2010-04-01T08:00', 0.2, 'a'],
             ['2010-04-02T12:00', 1.2, 'b'],
             ['2010-04-03T03:00', 2.2, 'c'],
             ['2010-04-04T17:00', 0.4, 'a'],
             ['2010-04-05T13:00', 1.4, 'b'],
             ['2010-04-06T11:00', 2.4, 'c']],
             columns=['date', 'price', 'category']
        )
        idx = pd.DatetimeIndex(df1['date'])
        df1.drop(columns=['date'], inplace=True)
        df1.index = idx
        return df1

    def test_resample_timestamps_column_names_match(self) -> None:
        df1 = self.make_example_dataframe()
        df2 = resample_timestamps(df1)
        self.assertListEqual(
            ['a__price', 'b__price', 'c__price'],
            sorted(list(df2.columns))
        )

    def test_resample_timestamps_date_range_match(self) -> None:
        df1 = self.make_example_dataframe()
        df2 = resample_timestamps(df1)
        expected = pd.DatetimeIndex([
            '2010-04-03T04:00',
            '2010-04-03T08:00',
            '2010-04-03T12:00',
            '2010-04-03T16:00',
            '2010-04-03T20:00',
            '2010-04-04T00:00',
            '2010-04-04T04:00',
            '2010-04-04T08:00',
            '2010-04-04T12:00',
            '2010-04-04T16:00',
            '2010-04-04T20:00',
            '2010-04-05T00:00',
            '2010-04-05T04:00',
            '2010-04-05T08:00',
            '2010-04-05T12:00',
            '2010-04-05T16:00',
            '2010-04-05T20:00',
            '2010-04-06T00:00',
            '2010-04-06T04:00',
            '2010-04-06T08:00'
        ])
        self.assertListEqual(
            list(map(str, expected)),
            list(map(str, df2.index))
        )

    def test_resample_timestamps_prices_match(self) -> None:
        df1 = self.make_example_dataframe()
        df2 = resample_timestamps(df1)
        # a__prices
        self.assertAlmostEqual(
            0.2 * 10,
            df2['a__price'].iloc[:10].sum()
        )
        self.assertAlmostEqual(
            0.4 * 10,
            df2['a__price'].iloc[10:].sum()
        )

        # b__prices
        self.assertAlmostEqual(
            1.2 * 15,
            df2['b__price'].iloc[:15].sum()
        )
        self.assertAlmostEqual(
            1.4 * 5,
            df2['b__price'].iloc[15:].sum()
        )

        # c__prices
        self.assertAlmostEqual(
            2.2 * 20,
            df2['c__price'].sum()
        )
