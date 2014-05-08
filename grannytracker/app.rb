require 'sinatra'
require 'sinatra/activerecord'
require './config/environments' #database configuration
require './models/model'        #Model class
require './models/place'        #Place class

get '/' do
  @places = Place.filter(Place.get_today)
	erb :index
end

post '/submit' do
	@model = Model.new(params[:model])
	if @model.save
		redirect '/models'
	else
		"Sorry, there was an error!"
	end
end

post '/checkin' do
  @place = Place.new(params[:place])
  if @place.save
    'OK'
  else
    'ERROR'
  end
end

get '/models' do
	@models = Model.all
	erb :models
end

after do
  # Close the connection after the request is done so that we don't
  # deplete the ActiveRecord connection pool.
  ActiveRecord::Base.connection.close
end