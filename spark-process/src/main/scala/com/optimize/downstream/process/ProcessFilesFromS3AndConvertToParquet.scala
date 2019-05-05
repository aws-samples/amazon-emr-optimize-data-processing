package com.optimize.downstream.process

import java.io.InputStream
import java.util.zip.GZIPInputStream
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{ListObjectsRequest, ObjectListing}
import org.apache.spark.sql._

import scala.collection.JavaConversions.{collectionAsScalaIterable => asScala}
import scala.io.Source

object ProcessFilesFromS3AndConvertToParquet {

  val pageLength = 1000
  def main(args: Array[String]) =
  {
    if(args.length != 3)
    {
      println("Requires 3 parameters")
      println("Usage: <sourceBucket> <s3InputLocation> <s3OutputLocation>")
      System.exit(-1)
    }
    val s3BucketName = args(0)
    val s3InputLocation = args(1)
    val s3OutputLocation = args(2)

    //def s3Client = new AmazonS3Client(new BasicAWSCredentials(accesskeyID, secretAccessKey))
    def s3Client = new AmazonS3Client()

    val spark = SparkSession
      .builder()
      .appName("AWS-Small-Blogs-Job")
      .getOrCreate()

    val request = new ListObjectsRequest()
    request.setBucketName(s3BucketName)
    request.setPrefix(s3InputLocation) //Get the prefix part only
    request.setMaxKeys(pageLength)


    var objs= new ObjectListing()
    objs = s3Client.listObjects(request)
    val s3ObjectKeys = objs.getObjectSummaries.map(x => x.getKey).toList
    println("Printing the keys")
    s3ObjectKeys.foreach { println }

    val allLinesRDD = spark.sparkContext.parallelize(s3ObjectKeys).flatMap
    { key => Source.fromInputStream(new GZIPInputStream(s3Client.getObject(s3BucketName, key).getObjectContent: InputStream)).getLines }

    var finalDF = spark.read.json(allLinesRDD).toDF()

    while(objs.isTruncated())
    {
      objs = s3Client.listNextBatchOfObjects(objs)
      val s3ObjectKeys = objs.getObjectSummaries.map(x => x.getKey).toList
      //println("Printing the keys")
      s3ObjectKeys.foreach { println }
      val allLinesRDD = spark.sparkContext.parallelize(s3ObjectKeys).flatMap
      { key => Source.fromInputStream(new GZIPInputStream(s3Client.getObject(s3BucketName, key).getObjectContent: InputStream)).getLines }

      val allLines = spark.read.json(allLinesRDD).toDF()
      finalDF = finalDF.union(allLines)
    }
    finalDF.write
      .mode("append")
      .parquet("s3://" + s3BucketName + "/" + s3OutputLocation)
  }
}
