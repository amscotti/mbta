(ns core
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [question3 :as q])
  (:gen-class))

;; pulling data from MBTA API, and creat lookup table for validation
(def data (q/get-routes-with-stops))
(def table (q/create-look-up-table data))

;; command line options
(def cli-options
  [["-s" "--start START" "Starting subway stop"
    :validate [#(contains? table %) "Must be a valid stop"]]
   ["-d" "--destination DESTINATION" "Destination subway stop"
    :validate [#(contains? table %) "Must be a valid stop"]]
   ["-h" "--help"]])

(defn- error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn- validate-args [args]
  (let [{:keys [options errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) {:exit-message summary :ok? true}
      errors {:exit-message (error-msg errors)}
      (not (:start options)) {:exit-message (error-msg ["Start must be specified"])}
      (not (:destination options)) {:exit-message (error-msg ["Destination must be specified"])}
      :else {:options options})))

(defn- exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (let [{start :start, destination :destination} options
            graph (q/create-graph data)
            path (q/find-shortest-path graph start destination)
            routes (q/routes-from-path table path)]
        (println start "to" destination)
        (q/print-path-and-route path routes)))))