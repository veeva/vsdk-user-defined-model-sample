# Vault Java SDK Sample - vsdk-user-defined-model-sample

**Please see the [project wiki](https://github.com/veeva/vsdk-user-defined-model-sample/wiki) for a detailed walkthrough**.

The **vsdk-user-defined-model-sample** project covers the creation and use of the user-defined model(UDM). 
User-defined models allow developers to create reusable data access objects, or models, and annotate their getters and setters as user-defined properties. 
You can use user-defined models with the JsonService to translate data to and from JSON, or with HttpService to send and receive data using REST APIs.

This project will demonstrate how to define and use User-defined Models with the QueryService to handle Query responses. 
Additionally, it will demonstrate how to use User-defined models with the HTTPService to send and receive data.
Furthermore, it will demonstrate how to translate data to and from JSON using the JsonService and User-defined models.

## How to import

Import the project as a Maven project. This will automatically pull in the required Vault Java SDK dependencies. 

For Intellij this is done by:
-	File -> Open -> Navigate to project folder -> Select the 'pom.xml' file -> Open as Project

For Eclipse this is done by:
-	File -> Import -> Maven -> Existing Maven Projects -> Navigate to project folder -> Select the 'pom.xml' file


## Setup

For this project, the custom trigger and necessary vault components are contained in the two separate vault packages (VPK). The VPKs are located in the project's **deploy-vpk** directory  and **need to be deployed to your vault** prior to debugging these use cases:

1.  Clone or download the sample Maven project [vSDK User Defined Model Sample project](https://github.com/veeva/vsdk-user-defined-model-sample) from Github.
2.  Run through the [Getting Started](https://developer.veevavault.com/sdk/#getting-started) guide to set up your development environment.
3.  Log in to your vault and navigate to **Admin > Deployment > Inbound Packages** and click **Import**:
4.  Locate and select the following file in your downloaded project file:

    > Vault components: **\deploy-vpk\code\vsdk-user-defined-model-sample-components.vpk** file.
 
5.  From the **Actions** menu (gear icon), select **Review & Deploy**. Vault displays a list of all components in the package.   
6.  Review the prompts to deploy the package. You will receive an email when vault completes the deployment.
7.  Repeat steps 3-6 for the vault code, select the package that matches your vault type:

    >Deploy code: Select the \deploy-vpk\components\vsdk-user-defined-model-sample-code.vpk file.

## License

This code serves as an example and is not meant to be used for production use.

Copyright 2020 Veeva Systems Inc.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
