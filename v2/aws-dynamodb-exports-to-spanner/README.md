# Dataflow Flex Template to ingest data from Azure Event hub to Pub/Sub

A Dataflow pipeline to read DynamoDB exports stored in AWS S3 and write to Cloud FSpanner.

## Requirements

These are common requirements for all of the templates in this collection.

*   Java 11
*   Maven
*   Docker

## Getting Started

### Building Template

This is a Flex Template meaning that the pipeline code will be containerized and
the container will be used to launch the Dataflow pipeline.

### Compiling the pipeline

Execute the following command from the directory containing the parent pom.xml
(DataflowTemplates/):

```shell
mvn clean compile -pl v2/aws-dynamodb-exports-to-spanner -am
```

### Executing unit tests

Execute the following command from the directory containing the parent pom.xml
(DataflowTemplates/):

```shell
mvn clean test -pl v2/aws-dynamodb-exports-to-spanner -am
```

## Uploading Templates

NOTE: This requires [Cloud SDK](https://cloud.google.com/sdk/downloads) version
284.0.0 or higher.

The Template should be build from the parent pom.xml (DataflowTemplates/).

This Template can also be launched directly from the Google Cloud Console. These
steps are primarily for development purposes.

### Building Container Image

__Set environment variables that will be used in the build process.__

```sh
export PROJECT=${USER}-playground
export IMAGE_NAME=aws-dynamodb-exports-to-spanner
export MODULE_NAME=aws-dynamodb-exports-to-spanner
export TARGET_GCR_IMAGE=gcr.io/${PROJECT}/${IMAGE_NAME}
export BASE_CONTAINER_IMAGE=gcr.io/dataflow-templates-base/java11-template-launcher-base
export BASE_CONTAINER_IMAGE_VERSION=latest
export APP_ROOT="/template/${MODULE_NAME}"
export COMMAND_SPEC="${APP_ROOT}/resources/${MODULE_NAME}-command-spec.json"
```

__Build and push image to Google Container Repository__

```sh
mvn clean install -pl "v2/${MODULE_NAME}" -am \
  -Dimage="${TARGET_GCR_IMAGE}" \
  -Dbase-container-image="${BASE_CONTAINER_IMAGE}" \
  -Dbase-container-image.version="${BASE_CONTAINER_IMAGE_VERSION}" \
  -Dapp-root="${APP_ROOT}" \
  -Dcommand-spec="${COMMAND_SPEC}" \
  -Djib.applicationCache="/tmp/"
```

### Creating Image Spec

Create a file with the metadata required for launching the Flex template. Once
created, this file should be placed in GCS.

The `aws-dynamodb-exports-to-spanner-metadata.json` file in resources directory
contains most of the content for this file.

__Set environment variables that will be used in the build process.__
```shell
export BUCKET_NAME=flex_containers  #bucket where image spec file will be stored
export METADATA_FILEPATH=v2/aws-dynamodb-exports-to-spanner/src/main/resources/aws-dynamodb-exports-to-spanner-metadata.json
export TEMPLATE_SPEC_GCSPATH="gs://${BUCKET_NAME}/templates/specs/aws-dynamodb-exports-to-spanner"
```
__To build image spec file on GCS, use following-:__
```shell
gcloud dataflow flex-template build "${TEMPLATE_SPEC_GCSPATH}" \
    --image "${TARGET_GCR_IMAGE}" \
    --sdk-language "JAVA" \
    --metadata-file "${METADATA_FILEPATH}"
```

### Running the Template

The template requires the following parameters:
* TBU
* secret: Secret (within Secret Manager) to be used. It follows the pattern of `projects/{project}/secrets/{secret}/versions/{secret_version}`. This Secret will hold the SAS connection string which would be required for authentication with Azure Eventhub Namespace.

Note: More information on using connection string to connect to Azure Eventhub can be found [here](https://learn.microsoft.com/en-us/azure/event-hubs/event-hubs-java-get-started-send?tabs=connection-string%2Croles-azure-portal). In this template, the connection string needs to be stored as a secret in GCP secret manager and is provided to template via parameter (secretId, secretVersion).

__Set environment variables that will be used in the run tempalte.__

```sh
export JOB_NAME="${USER}-aws-dynamodb-exports-to-spanner"
export PROJECT=${USER}-playground
export REGION=us-central1   #update based on requirement
Template can be executed using the `gcloud` sdk:
```
__Use gcloud to execute the template:__
```sh
gcloud dataflow flex-template run "$JOB_NAME-$(date +'%Y%m%d%H%M%S')" \
  --project "$PROJECT" --region "$REGION" \
  --template-file-gcs-location "$TEMPLATE_SPEC_GCSPATH" \
  --parameters projectId=$PROJECT \
  --parameters spannerInstance="my_instance"  \
  --parameters databaseName="my_db"  \
  --parameters tableName="my_table"  \
  --parameters secretWithAWSCredsUri="projects/sampleproject/secrets/azurekey/versions/2" \
  --parameters awsRegion="us-east1"  \
  --parameters manifestS3URI="s3://my-s3_bucket/AWSDynamoDB/01704714307553-96d2a18c/manifest-files.json"
```

The template can also be launched from the portal by selecting "Custom Template"
from the list of templates.
