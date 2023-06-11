import numpy as np
import unittest


class TestSimple(unittest.TestCase):
    def test_yes(self):
        self.assertEqual('yes', 'yes')

    def test_other(self):
        self.assertEqual('no', 'no')

    def test_long_running(self):
        for _ in range(1000):
            x = [2.22 * i for i in range(20_000)]
        self.assertEqual(0, x[0])

    # used to test how timeout works with a 3rd party library
    def test_slow_matrix_solve(self):
        size = 700
        rng = np.random.default_rng(seed=3434)
        a = rng.random([size, size])
        x = rng.random([size, 1])
        y = a @ x
        
        def solve_linalg(a, y):
            for _ in range(20):
                x_sol = np.linalg.solve(a, y)
            return x_sol
        
        def solve_by_inv(a, y):
            for _ in range(20):
                x_sol = np.linalg.inv(a) @ y
            return x_sol
            
        x0 = solve_linalg(a, y)
        x1 = solve_by_inv(a, y)
        self.assertLess((x0 - x).sum(), 1e-7)
        self.assertLess((x1 - x).sum(), 1e-7)


if __name__ == '__main__':
    TestSimple().test_slow_matrix_solve()
