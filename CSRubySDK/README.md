CloudShare Ruby SDK 
=====================

## Description

An SDK for developing applications in Ruby to connect to the CloudShare service using the CloudShare API. 


The SDK includes Ruby methods for the following API activities:

* Creating and Managing Environments and VMs
* Taking Snapshots
* Using CloudFolders
 
The SDK has two modes of use: 

### High Level API

The High Level API (`CSHighApi` class) implements the most popular API calls. It also parses the JSON response blocks into Ruby Hash objects. 
See the example below.

### Low Level API 

The Low Level API (`CSLowApi` class) provides a way to easily create and send API requests. It handles the authentication required
by the CloudShare API. The `call()` method in `CSLowApi` can be used to call the CloudShare API.

## Requirements

* Ruby 1.9+
* API-ID (assigned by CloudShare)
* API-KEY (assigned by CloudShare)

## Installation

gem install cloudshare-sdk

## Usage Example

```ruby
require 'cloudshare-sdk'
api = CloudshareSDK::CSHighApi.new('<API-ID>', '<API-KEY>')
puts api.get_environment_status_list
```


## References

[CloudShare API](http://docs.cloudshare.com/rest-api/v2/overview/)

License
=======

Copyright 2014 CloudShare, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
