(ns question3
  (:require [clojure.set :as set]
            [clojure.string :as string]
            [mbta :refer [get-routes-subway get-stops-names-for-route]])
  (:gen-class))

;; Get and format data from MBTA API

(defn get-routes-with-stops
  "Calls the MBTA API to create a map of route with all the stops"
  [] (let [ids (map #(get % :id) (:data (get-routes-subway)))
           stops (map get-stops-names-for-route ids)]
       (zipmap ids stops)))

;; For creating and adding nodes/edges to the graph

(defn add-node [graph node edge-to-add]
  (let [edges (conj (get graph node #{}) edge-to-add)]
    (assoc-in graph [node] edges)))

(defn add-bi-directional-node [graph edge-pair]
  (-> (add-node graph (first edge-pair) (second edge-pair))
      (add-node (second edge-pair) (first edge-pair))))

(defn add-stops-to-graph [graph stops]
  (->> (partition 2 1 stops)
       (reduce add-bi-directional-node graph)))

(defn create-graph [data]
  (reduce (fn [graph [_ stops]] (add-stops-to-graph graph stops)) {} data))

;; Searching the graph for paths

(defn find-paths
  "Finds all valid paths in the graph for a start and end stop"
  ([graph start end]
   (find-paths graph start end #{} [start]))
  ([graph start end visited path]
   (let [edges (get graph start #{})]
     (if (contains? edges end)
       (let [path (conj path end) steps (count path)]
         [{:path path :steps steps}])
       (let [visited (conj visited start)
             not-visited (filter #(not (contains? visited %)) edges)]
         (if (empty? not-visited)
           nil
           (->> not-visited
                (map #(find-paths graph % end visited (conj path %)))
                (filter (comp not nil?))
                (flatten)
                (vec))))))))

(defn find-shortest-path
  "Returns the shortest path based on the number of steps"
  [graph start end]
  (->> (find-paths graph start end)
       (sort-by (fn [{value :steps}] value))
       (first)
       (:path)))

;; Creating look up table to use with displaying paths routes

(defn update-table [table stop route]
  (update table stop #(conj (if (nil? %) [] %) route)))

(defn add-routes-to-stops [table route stops]
  (reduce #(update-table %1 %2 route) table stops))

(defn create-look-up-table
  "Returns a map of stops with the associated route"
  [data]
  (reduce (fn [table [route stops]]
            (add-routes-to-stops table route stops)) {} data))

(defn routes-from-path
  "Returns the routes involved in the path's stops"
  [table path]
  (->> path
       (map #(get table %))
       (partition 2 1)
       (map (fn [[to from]] (set/intersection (set to) (set from))))
       (distinct)))

;; Format and print results

(defn print-path-and-route [path routes]
  (println "Path:" (string/join "->" path))
  (println "Routes:" (string/join ", " (map #(string/join " or " %) routes))))

(defn print-question3
  ([] (print-question3 (get-routes-with-stops)))
  ([data]
   (let [graph (create-graph data) table (create-look-up-table data)]
     (println "Question 3: Examples -")

     (println "\nDavis to Kendall/MIT")
     (let [path (find-shortest-path graph "Davis" "Kendall/MIT")
           routes (routes-from-path table path)]
       (print-path-and-route path routes))

     (println "\nAshmont to Arlington")
     (let [path (find-shortest-path graph "Ashmont" "Arlington")
           routes (routes-from-path table path)]
       (print-path-and-route path routes))

     (println "\nButler to Fenway")
     (let [path (find-shortest-path graph "Butler" "Fenway")
           routes (routes-from-path table path)]
       (print-path-and-route path routes)))))


(defn -main []
  (print-question3))