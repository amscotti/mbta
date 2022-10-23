(ns question1
  (:require [clojure.string :as string]
            [mbta :refer [get-routes-subway]])
  (:gen-class))

;; Get and format data from MBTA API

(defn get-routes-long-name
  "Pulls data from API if not provided, and selects 'long_name' from data attributes"
  ([] (get-routes-long-name (get-routes-subway)))
  ([data] (map #(get-in % [:attributes :long_name]) (:data data))))

;; Format and print results

(defn print-question1 []
  (->> (get-routes-long-name)
       (string/join ", ")
       (println "Question 1:\n")))

(defn -main []
  (print-question1))