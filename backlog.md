# Backlog Items
This file contains a list of problems with the source code for future consideration. These are grouped under the subheadings
based on which particular section of the source code they refer to.

# sklearn2pfa
register_converters.py - Type hint for Converter_Type ought to be more specific to allow more robust type checking.

test_convert.py - Mock of fitted converter may need to extend from scikit-learn base estimator to be more robust to
changes in the scikit-learn library.

test_pfa.py - Test only covers the input type and schema. Ought to cover outputs and cells as well.

test_converters.py - Test does not cover cases with different column names.

Dependencies - Dependency on avro-python3 library which is deprecated and no longer being updated. Should be replaced
by the updated avro 1.11.1 library, but that would break the Avro Json Serializer used in the Pfa class and would
also break the test environment that uses Titus2 which depends on the old avro-python3 library.

# Java Application
PriceApplication.java - Markdown template location ought to be configurable via the application.properties file.

PriceForecast.java - Consider whether this interface should be in the priceapp package instead of
the forecaster package.

PfaDecisionTree.java - Consider removing singleton pattern, since Spring beans are singleton by default anyway.

CredentialsProvider.java - This ought to use more secure methods for storing and retrieving secret keys.

Imports generally - Remove unused imports to clean up code

Testing - Consider testing that lists are unmodifiable for assurance that classes are thread safe.

Github Actions - When running automated tests via Github Actions, consider checking the test reports back into the
Github repository for a more detailed record of the results.

History Storage - Persistent storage of all requested forecasts, and a mechanism for querying the accuracy by comparing historical forecasts from the storage with the actual prices.

Performance Testing - Testing to confirm how many requests per second the application can process.

# Fetching script
Console output - Ought to display name of file that was written in the console output to improve
user experience
