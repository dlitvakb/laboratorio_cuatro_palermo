require 'sinatra'
require 'sinatra/activerecord'

set :database, {adapter: "sqlite3", database: "ejemlo.sqlite3"}

class Status < ActiveRecord::Base
end

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
    Status.find(params[:id]).to_json
  end

  put "/status/:id/?" do
    status = Status.find(params[:id])
    status.update_attributes(params)
    status.to_json
  end
end
