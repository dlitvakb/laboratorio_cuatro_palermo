require 'sinatra'
require 'data_mapper'

DataMapper::setup(:default, "sqlite3://#{Dir.pwd}/ejemplo.sqlite3")

class Status
  include DataMapper::Resource

  property :id, Serial
  property :name, String
end

DataMapper.finalize
Status.auto_upgrade!

class App < Sinatra::Base
  before do
    content_type :json
  end

  get "/" do
    p "App de ejemplo"
  end

  get "/status/?" do
    Status.all.to_json
  end

  post "/status/?" do
    Status.create(name: params[:name]).to_json
  end

  get "/status/:id/?" do
    Status.get(params[:id]).to_json
  end

  put "/status/:id/?" do
    status = Status.get(params[:id])
    status.update(params)
    status.to_json
  end
end
