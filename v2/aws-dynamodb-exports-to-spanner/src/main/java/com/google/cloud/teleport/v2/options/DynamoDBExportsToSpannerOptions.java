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
package com.google.cloud.teleport.v2.options;

import com.google.cloud.teleport.metadata.TemplateParameter;
import org.apache.beam.sdk.io.aws.options.AwsOptions;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.options.ValueProvider;

public interface DynamoDBExportsToSpannerOptions extends PipelineOptions, AwsOptions {

  // Spanner Options
  @TemplateParameter.Text(
      order = 1,
      optional = false,
      regexes = {"?:[^:]+:)?[a-z0-9\\\\-]+"},
      description = "GCP Project Id of where the Spanner table lives.",
      helpText = "GCP Project Id of where the Spanner table lives",
      example = "tokyo-rain-123")
  ValueProvider<String> getProjectId();

  void setProjectId(ValueProvider<String> value);

  @TemplateParameter.Text(
      order = 2,
      optional = false,
      description = "Cloud Spanner instance.",
      helpText = "Cloud Spanner instance",
      example = "my_instance")
  @Validation.Required
  ValueProvider<String> getSpannerInstance();

  void setSpannerInstance(ValueProvider<String> value);

  @TemplateParameter.Text(
      order = 3,
      optional = false,
      description = "Cloud Spanner database name.",
      helpText = "Cloud Spanner database name",
      example = "my_db")
  @Validation.Required
  ValueProvider<String> getDatabaseName();

  void setDatabaseName(ValueProvider<String> value);

  @TemplateParameter.Text(
      order = 4,
      optional = false,
      description = "Cloud Spanner table name.",
      helpText = "Cloud Spanner table name",
      example = "testTable")
  @Validation.Required
  ValueProvider<String> getTableName();

  void setTableName(ValueProvider<String> value);

  // AWS DynamoDB exports options.
  @TemplateParameter.Text(
      order = 5,
      optional = false,
      description =
          "GCP Secret manager URI with AWS credentials. Versions is optional, if not part of URI, latest will be assumed",
      helpText = "GCP Secret manager URI with AWS credentials",
      example = "projects/{project}/secrets/{secret}/versions/{secret_version}")
  @Description("")
  @Validation.Required
  ValueProvider<String> getSecretWithAWSCredsUri();

  void setSecretWithAWSCredsUri(ValueProvider<String> uri);

  @TemplateParameter.Text(
      order = 7,
      optional = false,
      description = "DynamoDB Exports manifest file uri in s3.",
      helpText = "DynamoDB Exports manifest file uri in s3",
      example = "s3://my-s3_bucket/AWSDynamoDB/01704714307553-96d2a18c/manifest-files.json")
  @Validation.Required
  ValueProvider<String> getManifestS3URI();

  void setManifestS3URI(ValueProvider<String> manifestS3URI);
}
