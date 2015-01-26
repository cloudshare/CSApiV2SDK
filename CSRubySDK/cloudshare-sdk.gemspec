$:.unshift File.expand_path("../lib", __FILE__)
require "cloudshare-sdk/version"

Gem::Specification.new do |s|
  s.name          = "cloudshare-sdk"
  s.version       = CloudshareSDK::VERSION
  s.platform      = Gem::Platform::RUBY
  s.license       = "Apache 2.0"
  s.authors       = "Muly Gottlieb"
  s.email         = "muly@cloudshare.com"
  s.homepage      = "http://www.cloudshare.com"
  s.summary       = "An SDK for developing applications in Ruby to connect to the CloudShare service using the CloudShare API."
  s.description   = "An SDK for developing applications in Ruby to connect to the CloudShare service using the CloudShare API. "

  s.require_paths = ["lib"]
  s.files = ["lib/cloudshare-sdk.rb", "lib/cloudshare-sdk/cs_high_api.rb", "lib/cloudshare-sdk/cs_low_api.rb"]

end
