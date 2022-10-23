(ns question2-test
  (:require [clojure.test :refer [deftest is]]
            [question2 :as subject]))

;; Just data for the Red and Orange routes for testing
(def data {"Red"
           #{"Downtown Crossing" "North Quincy" "Fields Corner" "Central" "Porter" "Quincy Center"
             "Harvard" "Ashmont" "Davis" "South Station" "Shawmut" "Savin Hill" "Wollaston" "Andrew"
             "Quincy Adams" "Braintree" "Broadway" "Charles/MGH" "JFK/UMass" "Park Street" "Kendall/MIT"
             "Alewife"},
           "Orange"
           #{"North Station" "Roxbury Crossing" "Haymarket" "Downtown Crossing" "Back Bay" "Oak Grove"
             "Chinatown" "Wellington" "Jackson Square" "Community College" "Sullivan Square" "Stony Brook"
             "Ruggles" "Tufts Medical Center" "Green Street" "State" "Massachusetts Avenue" "Forest Hills"
             "Assembly" "Malden Center"}})


(deftest should-return-route-with-most-stops
  (is (= {:name "Red", :count 22}
         (:max (subject/get-max-min-stop-counts data)))))

(deftest should-return-route-with-fewest-stops
  (is (= {:name "Orange", :count 20}
         (:min (subject/get-max-min-stop-counts data)))))

(deftest should-create-new-entry-in-result
  (is (= {"Something" #{1 2}}
         (subject/add-intersect 1 2 {} "Something"))))

(deftest should-add-to-entry-in-result
  (is (= {"Something" #{1 2 3 4}}
         (subject/add-intersect 1 2 {"Something" #{3 4}} "Something"))))

(deftest should-get-intersects-stops-for-Red-and-Orange
  (is (= {"Downtown Crossing" #{"Orange" "Red"}}
         (subject/get-intersects data))))