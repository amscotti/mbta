(ns question1-test
  (:require [clojure.test :refer [deftest is]]
            [question1 :as subject]
            [clojure.java.io :as io]
            [cheshire.core :refer [parse-string]]))

(defn get-data-from-file [file]
  (-> file
      (io/resource)
      (slurp)
      (parse-string true)))

(deftest should-output-route-long-names
  (is (= ["Red Line" "Mattapan Trolley" "Orange Line" "Green Line B"
          "Green Line C" "Green Line D" "Green Line E" "Blue Line"]
         (subject/get-routes-long-name (get-data-from-file "routes.json")))))
