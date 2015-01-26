require 'net/http'
require 'json'
require 'securerandom'
require 'digest'

module CloudshareSDK
  def self.token_generator
    if RUBY_VERSION >= "1.9"
      SecureRandom.hex(10/2) # generated result string from #hex is double the size of given n...
    else
      o = [('a'..'z'), ('A'..'Z'), ('0'..'9')].map { |i| i.to_a }.flatten
      (0...10).map { o[rand(o.length)] }.join
    end
  end

  def self.prettify_json(s)
    begin
      JSON.pretty_generate(Hash[JSON.parse(s).sort])
    rescue
      s
    end
  end

  class ApiException < StandardError
    attr_reader :content, :code
    def initialize(content, code)
      @content = content
      @code = code
    end

    def to_s
      "\ncontent:\n\n#@content\n\nhttp status code: #@code\n\n"
    end
  end


  class ApiResponse
    def initialize(content, code)
      @content = content
      @code = code
    end

    def pretty_content
      CloudshareSDK::prettify_json(@content)
    end

    def json
      JSON.parse(@content)
    end
  end


  class CSLowApi
    DEFAULT_HOST = "use.cloudshare.com"
    DEFAULT_VERSION = "v2"

    def initialize(id, key, host=DEFAULT_HOST, version=DEFAULT_VERSION)
      @id = id
      @key = key
      @host = host
      @version = version
    end

    def call(category, command, params={})
      uri = gen_uri(category, command, params)
      begin
        http = Net::HTTP.new(uri.host, uri.port)
        http.use_ssl = true
        http.verify_mode = OpenSSL::SSL::VERIFY_NONE
        request = Net::HTTP::Get.new(uri.request_uri)
        f = http.request(request)
        raise ApiException.new(f.body , f.code) if f.code != '200'
        raise ApiException.new('Empty Response' , f.code) if f.body.length == 0
      rescue StandardError => ex
        raise ApiException.new((ex.respond_to?(:content) ? ex.content : ex.message),
                               (ex.respond_to?(:code) ? ex.code : -1))
      end

      ApiResponse.new(f.body, f.code)
    end

    def gen_uri(category, command, params)
      hmac = Digest::SHA1.new
      hmac.update(@key.encode('utf-8'))
      hmac.update(command.downcase.encode('utf-8')) if @version != 'v1'

      params['timestamp'] = Time.now.to_i.to_s
      params['UserApiId'] = @id
      params['token'] = CloudshareSDK::token_generator if @version != 'v1'

      query = {}
      params.keys.sort_by {|k| [k.downcase]}.each do |pkey|
        pkey_lower = pkey.downcase
        continue if pkey_lower == "hmac"

        hmac.update(pkey_lower.encode('utf-8'))
        pvalue = params[pkey]
        if !pvalue.nil? and pvalue.length > 0
          hmac.update(pvalue.encode('utf-8'))
          query[pkey] = pvalue.encode('utf-8')
        end
      end

      query['HMAC'] = hmac.hexdigest
      uri = URI("https://#{@host.encode('utf-8')}/Api/#{@version.encode('utf-8')}" +
                    "/#{category.encode('utf-8')}/#{command.encode('utf-8')}")
      uri.tap do |u|
        u.query = URI.encode_www_form(query)
      end
    end

    def check_keys
      call('ApiTest', 'Ping').json['data']
    end
  end
end
