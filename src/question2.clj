(ns question2
  (:require [clojure.set :as set]
            [clojure.string :as string]
            [mbta :refer [get-routes-subway get-stops-names-for-route]])
  (:gen-class))

;; Get and format data from MBTA API

(defn- get-routes-with-stops []
  (let [ids (map #(get % :id) (:data (get-routes-subway)))
        stops (map #(set (get-stops-names-for-route %)) ids)]
    (zipmap ids stops)))

;; Data manipulation to answer question

(defn get-max-min-stop-counts
  "Get the routes with the max and min stops"
  [data] (let [counts (->> (map (fn [[key value]] {:name key :count (count value)}) data)
                           (sort-by (fn [{value :count}] value)))]
           {:max (last counts) :min (first counts)}))


(defn add-intersect [a b result intersect]
  (assoc-in result [intersect] (set/union (get result intersect #{}) #{a b})))


(defn get-intersects
  "Get the stops with intersects to other routes"
  [data] (let [keys (keys data)
               routes (for [a keys b keys :when (not= a b)] [a b])]
           (loop [routes routes result {}]
             (if (empty? routes)
               result
               (let [[a b] (first routes)
                     intersect (set/intersection (get data a) (get data b))]
                 (recur (rest routes)
                        (reduce #(add-intersect a b %1 %2) result intersect)))))))

;; Format and print results

(defn print-question2
  "Pulls data from API if not provided, and then answers question 2"
  ([] (print-question2 (get-routes-with-stops)))
  ([data]
   (let [counts (get-max-min-stop-counts data) intersects (get-intersects data)]
     (println "Question 2:")
     (println "\n1. The name of the subway route with the most stops as well as a count of its stops.")
     (println (str "Name: " (get-in counts [:max :name]) " Stops: " (get-in counts [:max :count])))
     (println "\n2. The name of the subway route with the fewest stops as well as a count of its stops.")
     (println (str "Name: " (get-in counts [:min :name]) " Stops: " (get-in counts [:min :count])))
     (println "\n3. A list of the stops that connect two or more subway routes along with the relevant route names for
each of those stops.")
     (doseq [[stop routes] intersects]
       (println (str "Stop: " stop " on " (string/join ", " routes) " routes"))))))


(defn -main []
  (print-question2))