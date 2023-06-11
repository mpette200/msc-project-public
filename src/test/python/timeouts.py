'''
Module to run tests with a timeout. If any individual test runs for longer
then the timeout, that test will be terminated and the runner will proceed
to the next test in the queue.
'''

import ctypes
import sys
import threading as th
import types


class TimedOutException(BaseException):
    def __init__(self):
        super().__init__('Test Timed Out')


class TRunner(th.Thread):
    '''
    Thread that can be killed after a specified timeout.
    Experimental.
    '''
    # Caught exception should match the one sent. So use global variable.
    EXCEPTION = TimedOutException
    
    def __init__(self, target, args):
        super().__init__()
        self.target = target
        self.args = args
        # need to check thread is not already in the process of finishing
        # so need a boolean protected with a lock
        self.finishing = False
        self.finishing_lock = th.Lock()
    
    def run(self):
        try:
            self.result = self.target(*self.args)
            self.set_finishing()
        except self.EXCEPTION:
            self.exception_info = sys.exc_info()
            return

    def send_exception(self):
        '''Send exception to the thread that is running.
        Loosely based on:
        https://stackoverflow.com/questions/323972/is-there-any-way-to-kill-a-thread
        '''
        if self.ident is None:
            raise RuntimeError(f'{self} No thread ID. Thread not started')
        if not isinstance(self.EXCEPTION, type):
            raise TypeError('Only classes can be raised (not instances)')
        api_call = ctypes.pythonapi.PyThreadState_SetAsyncExc
        api_call.argtypes = [ctypes.c_ulong, ctypes.py_object]

        # check thread is not finishing before sending the exception
        with self.finishing_lock:
            if not self.finishing:
                num_modified = api_call(self.ident, self.EXCEPTION)
                if num_modified != 1:
                    # if not a single thread critical error
                    sys.exit(1)
    
    def set_finishing(self):
        with self.finishing_lock:
            self.finishing = True

    @classmethod
    def run_w_timeout(cls, timeout, target, args):
        '''
        Run the specified target with args, subject to the timeout.
        If the timeout is exceeded thread will be killed.
        '''
        t = cls(target=target, args=args)
        t.start()
        t.join(timeout)
        if t.is_alive():
            t.send_exception()
            t.join(2)
            if t.is_alive():
                # critical error if still alive
                sys.exit(1)
        if hasattr(t, 'exception_info'):
            raise cls.EXCEPTION().with_traceback(t.exception_info[2])
        elif hasattr(t, 'result'):
            return t.result


def flat_test_list(test):
    '''
    Flatten the list of tests in a hierarchical suite of tests
    '''
    if not hasattr(test, '__iter__') or len(list(test)) == 0:
        return [test]
    out = []
    for t in test:
        out.extend(flat_test_list(t))
    return out


def timed_runner_curry(timeout, func):
    def timed_runner(test_obj, result):
        try:
            return TRunner.run_w_timeout(timeout, func, args=(result,))
        except TRunner.EXCEPTION:
            result.addError(test_obj, sys.exc_info())
    return timed_runner


def decorate_runners(timeout, tests):
    '''
    Function modifies the tests run() method so it points to the
    timeout version. Note also the function flat_test_list(test)
    which may be useful when applying this function.
    '''
    for t in tests:
        t.run = types.MethodType(timed_runner_curry(timeout, t.run), t)
