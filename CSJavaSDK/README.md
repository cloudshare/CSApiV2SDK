CloudShare Java SDK API
=======================

## Description

An SDK for developing applications in Java to connect to the CloudShare service using the CloudShare REST API. The SDK
interfaces to the CloudShare API and tries to present an equivalent Java API for developers. 

The SDK includes Java methods for the following API activities:

* Creating and Managing Environments and VMs
* Taking Snapshots
* Using CloudFolders
 
The SDK has two modes of use: 
### High Level API

The High Level API implements the most popular REST API calls. It also parses the JSON response blocks into meaningful
objects. See the example below.


### Low Level API 

The Low Level API provides a way to easily create and send REST API requests. It handles the authentication required
by the CloudShare REST API. The low-level method callCSAPI can be used to call the CloudShare API. 


## Requirements

* Java 6 SE
* UserId (assigned by CloudShare)
* ApiKey (assigned by CloudShare)
* (Optional) Eclipse JDT


## Usage example

The example below uses the High Level API to send the *ListEnvironments* REST API request to the server to obtain
a list of all the user's environments, then send a *GetEnvironmentState* request for each environment in the list. 
The responses are filtered to show a list of the state of each environment.

    CSAPIHighLevel api = new CSAPIHighLevel(apiId, apiKey);
		
	try {
		List<EnvStatus> r = api.getEnvironmentStatusList();
		for (EnvStatus es : r) {
			System.out.println(es.toPrettyString());
		}
	} catch (ApiException e) {
		e.printStackTrace();
	}


## References

[CloudShare REST API](http://docs.cloudshare.com)

License 
=======

Copyright 2013 CloudShare, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Jackson 2.1 License
===================

Copyright 2009-2011  FasterXML, LLC.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.