/*
 * Copyright (C) 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.teleport.v2.templates;

import com.google.cloud.teleport.metadata.Template;
import com.google.cloud.teleport.metadata.TemplateCategory;
import com.google.cloud.teleport.v2.common.AWSCredentialsParser;
import com.google.cloud.teleport.v2.options.DynamoDBExportsToSpannerOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Dataflow templates which reads DyanmoDB exports from AWS S3 and writes them to DynamoDB. */
@Template(
    name = "AWS_DynamoDBExports_to_Spanner",
    category = TemplateCategory.BATCH,
    displayName = "AWS DynamoDBExports in S3 to Spanner",
    description =
        "A pipeline to read DynamoDB exports data stored in AWS S3 and loads them into Cloud Spanner.",
    optionsClass = DynamoDBExportsToSpanner.class,
    contactInformation = "https://cloud.google.com/support",
    documentation =
        "https://cloud.google.com/dataflow/docs/guides/templates/provided/aws-dynamodb-exports-to-spanner",
    flexContainerName = "aws-dynamodb-exports-to-spanner",
    streaming = false,
    preview = true,
    hidden = true)
public class DynamoDBExportsToSpanner {
  private static final Logger LOG = LoggerFactory.getLogger(DynamoDBExportsToSpanner.class);

  public static void main(String[] args) {
    DynamoDBExportsToSpannerOptions options =
        PipelineOptionsFactory.fromArgs(args)
            .withValidation()
            .as(DynamoDBExportsToSpannerOptions.class);
    new AWSCredentialsParser().updateCredetials(options);
    run(options);
  }

  public static PipelineResult run(DynamoDBExportsToSpannerOptions options) {
    LOG.info("Pipeline execution started...");
    Pipeline pipeline = Pipeline.create(options);

    PCollection<String> fileLines =
        pipeline.apply(
            "ReadDynamoDBManifestFromS3",
            TextIO.read().from(options.getManifestS3URI().get())); // Replace the file path

    fileLines.apply(
        "PrintLines",
        MapElements.via(
            new SimpleFunction<String, Void>() {
              @Override
              public Void apply(String lines) {
                System.out.println(lines);
                return null;
              }
            }));

    return pipeline.run();
  }
}
