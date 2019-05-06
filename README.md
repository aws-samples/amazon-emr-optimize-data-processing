## Optimizing downstream data processing with Amazon Kinesis Data Firehose and Amazon EMR running Apache Spark

Optimizing downstream data processing with Amazon Kinesis Data Firehose and Amazon EMR running Apache Spark

## License Summary

This sample code is made available under the MIT-0 license. See the LICENSE file.

## Solution Overview

### AWS Blog link
##### For complete blog details, check AWS blog @ ""
### The steps we follow in this blog post are:
##### 1.Create a virtual private cloud (VPC) and an Amazon S3 bucket.
##### 2.Provision a Kinesis Data data stream, and an AWS Lambda function to process the messages from the Kinesis data stream.
##### 3.Provision Kinesis Data Firehose to deliver messages to Amazon S3 sent from the Lambda function in step 2. This step also provisions provisions an Amazon EMR cluster to process the data in Amazon S3.
##### 4.Generate test data with custom code running on an Amazon EC2 instance.
##### 5.Run a sample Spark program from the Amazon EMR cluster’s master instance to read the files from Amazon S3, convert them into parquet format and write back to an Amazon S3 destination.

