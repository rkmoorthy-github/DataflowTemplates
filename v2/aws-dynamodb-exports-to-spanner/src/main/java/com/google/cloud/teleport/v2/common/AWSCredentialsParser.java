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
package com.google.cloud.teleport.v2.common;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.cloud.teleport.v2.options.DynamoDBExportsToSpannerOptions;
import org.json.JSONObject;

public class AWSCredentialsParser {

  private SecretManager secretManager;

  public AWSCredentialsParser() {
    this(new SecretManager());
  }

  AWSCredentialsParser(SecretManager secretManager) {
    this.secretManager = secretManager;
  }

  public void updateCredetials(DynamoDBExportsToSpannerOptions options) {
    String secretVersion = options.getSecretWithAWSCredsUri().get();
    if (secretVersion != null && !secretVersion.contains("/versions/")) {
      if (secretVersion.endsWith("/")) {
        secretVersion += "versions/latest";
      } else {
        secretVersion += "/versions/latest";
      }
    }
    String payload = secretManager.getPayload(secretVersion);
    JSONObject awsPayload = new JSONObject(payload);
    AWSCredentials awsCredentials =
        new BasicAWSCredentials(
            awsPayload.getString("accessKey"), awsPayload.getString("secretKey"));
    options.setAwsCredentialsProvider(new AWSStaticCredentialsProvider(awsCredentials));
  }
}
