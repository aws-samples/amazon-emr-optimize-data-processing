#!/usr/bin/env bash

# Change JAVA_HOME as per your java home location
JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home"
S3_PATH="aws-bigdata-blog/artifacts/aws-blog-optimize-downstream-data-processing"


if [[ ! -d "${JAVA_HOME}" ]]; then
   echo "JAVA_HOME path not found. Check the path : ${JAVA_HOME}"
   exit 99;
fi
export JAVA_HOME=${JAVA_HOME}
echo "Starting project build"
mvn clean compile assembly:single
echo "Project build completed"

# Copy to east-1 region - blog assumes everything is created in us-east-1 (N.VIRGINIA) region.

echo "Copying Files to S3 bucket path : ${S3_PATH}"
# Kinesis Producer Jar file
aws s3 cp sample-kinesis-producer/target/sample-kinesis-producer-1.0-SNAPSHOT-jar-with-dependencies.jar s3://${S3_PATH}/appjars/ --acl public-read

# Lambda Jar file
aws s3 cp kinesis-lambda/target/kinesis-lambda-1.0-SNAPSHOT-jar-with-dependencies.jar s3://${S3_PATH}/appjars/ --acl public-read

# Spark scala code to process files in S3.
aws s3 cp spark-process/target/spark-process-1.0-SNAPSHOT-jar-with-dependencies.jar s3://${S3_PATH}/appjars/ --acl public-read

# Cloudformation templates
aws s3 cp cloudformation-templates/allsteps_cf.template s3://${S3_PATH}/cloudformation-templates/ --acl public-read
aws s3 cp cloudformation-templates/step1_vpc.template s3://${S3_PATH}/cloudformation-templates/ --acl public-read
aws s3 cp cloudformation-templates/step2_iam.template s3://${S3_PATH}/cloudformation-templates/ --acl public-read
aws s3 cp cloudformation-templates/step3_firehose.template s3://${S3_PATH}/cloudformation-templates/ --acl public-read
aws s3 cp cloudformation-templates/step4_kinesisstream.template s3://${S3_PATH}/cloudformation-templates/ --acl public-read
aws s3 cp cloudformation-templates/step5_emr.template s3://${S3_PATH}/cloudformation-templates/ --acl public-read
aws s3 cp cloudformation-templates/step6_ec2_instance.template s3://${S3_PATH}/cloudformation-templates/ --acl public-read
