(ns mbta
  (:require [clj-http.client :as client])
  (:gen-class))

(def api-base-url "https://api-v3.mbta.com")
(def api-routes-subway-url (str api-base-url "/routes?filter[type]=0,1"))
(def api-routes-stops-url (str api-base-url "/stops?filter[route]="))


(defn get-routes-subway
  "Calls the MBTA routes API with filtering on 0 and 1 for Subway only"
  [] (:body (client/get api-routes-subway-url {:as :json})))

(defn get-route-stops
  "Calls the MBTA stops API with a route id to return all the stops for the route"
  [route] (:body (client/get (str api-routes-stops-url route) {:as :json})))

(defn get-stops-names-for-route [route-id]
  (->> (get-route-stops route-id)
       (:data)
       (map #(get-in % [:attributes :name]))))