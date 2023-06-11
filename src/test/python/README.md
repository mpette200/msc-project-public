## Testing Notes
Can run tests using
```bash
python3 -m unittest discover -v -s test_sklearn2pfa
```

### Import Paths
Due to the tests and the module code being located in different filesystem folders,
the test files modify `sys.path` to include the path to the main module. This allows
the imports to be found even if symbolic links (see below) are not supported on a system.

Additionally unix symbolic links have been created in the directory, linking back
to the corresponding location in the main filesystem folder. This has the benefit
that the autocompletion feature of my IDE (Visual Studio Code) can work.