(ns question3-test
  (:require [clojure.test :refer [deftest is]]
            [question3 :as subject]))

;; Just data for the Orange and Blue routes for testing
(def data {"Orange"
           ["Forest Hills" "Green Street" "Stony Brook" "Jackson Square" "Roxbury Crossing"
            "Ruggles" "Massachusetts Avenue" "Back Bay" "Tufts Medical Center" "Chinatown"
            "Downtown Crossing" "State" "Haymarket" "North Station" "Community College"
            "Sullivan Square" "Assembly" "Wellington" "Malden Center" "Oak Grove"],
           "Blue"
           ["Bowdoin" "Government Center" "State" "Aquarium" "Maverick" "Airport"
            "Wood Island" "Orient Heights" "Suffolk Downs" "Beachmont" "Revere Beach"
            "Wonderland"]})


(deftest should-return-a-graph-with-a-new-stop-and-edge
  (is (= {"Stop" #{"edge-1"}}
         (subject/add-node {} "Stop" "edge-1"))))

(deftest should-return-a-graph-with-an-edge-added-to-a-stop-with-edge
  (is (= {"Stop" #{"edge-1" "edge-2"}}
         (subject/add-node {"Stop" #{"edge-1"}} "Stop" "edge-2"))))

(deftest should-return-a-graph-with-bi-directional-edges
  (is (= {"stop-1" #{"stop-2"}, "stop-2" #{"stop-1"}}
         (subject/add-bi-directional-node {} '("stop-1", "stop-2")))))

(deftest should-return-a-graph-with-bi-directional-edges-with-pre-existing-edges
  (is (= {"stop-1" #{"stop-2"}, "stop-2" #{"stop-1" "stop-3"}, "stop-3" #{"stop-2"}}
         (subject/add-bi-directional-node {"stop-1" #{"stop-2"}, "stop-2" #{"stop-1"}}
                                          '("stop-2", "stop-3")))))

(deftest should-return-a-graph-from-list
  (is (= {"stop-1" #{"stop-2"}, "stop-2" #{"stop-1" "stop-3"}, "stop-3" #{"stop-2"}}
         (subject/add-stops-to-graph {} ["stop-1", "stop-2", "stop-3"]))))

(deftest should-return-a-graph-from-list-of-stops
  (let [graph (subject/add-stops-to-graph {} (get-in data ["Orange"]))]

    ;; Checking the ends of the Orange line
    (is (= #{"Malden Center"} (get-in graph ["Oak Grove"])))
    (is (= #{"Green Street"} (get-in graph ["Forest Hills"])))

    ;; Checking some stops to make sure they have linked in both directions
    (is (= #{"Community College" "Haymarket"} (get-in graph ["North Station"])))
    (is (= #{"Chinatown" "State"} (get-in graph ["Downtown Crossing"])))))


(deftest should-return-a-graph-with-intersecting-stops
  (let [graph-orange (subject/add-stops-to-graph {} (get-in data ["Orange"]))
        graph (subject/add-stops-to-graph graph-orange (get-in data ["Blue"]))]

    ;; Checking the ends of the Blue line
    (is (= #{"Government Center"} (get-in graph ["Bowdoin"])))
    (is (= #{"Revere Beach"} (get-in graph ["Wonderland"])))

    ;; Checking that intersections are added properly
    (is (= #{"Aquarium" "Downtown Crossing" "Government Center" "Haymarket"}
           (get-in graph ["State"])))))


(deftest should-create-graph-from-data
  (let [graph (subject/create-graph data)
        graph-orange (subject/add-stops-to-graph {} (get-in data ["Orange"]))
        graph-orange-blue (subject/add-stops-to-graph graph-orange (get-in data ["Blue"]))]

    ;; graph should create the same graph as orange + blue
    (is (= graph-orange-blue graph))))

(deftest should-find-path-for-next-stop
  (let [graph (subject/create-graph data)
        paths (subject/find-paths graph "Forest Hills" "Green Street")]
    (is (= 1 (count paths)))
    (is (= ["Forest Hills" "Green Street"] (get-in paths [0 :path])))
    (is (= 2 (get-in paths [0 :steps])))))


(deftest should-find-path-from-start-to-end-of-route
  (let [graph (subject/create-graph data)
        paths (subject/find-paths graph "Forest Hills" "Oak Grove")]
    (is (= 1 (count paths)))
    (is (= (get data "Orange") (get-in paths [0 :path])))
    (is (= 20 (get-in paths [0 :steps])))))

(deftest should-find-path-from-orange-to-blue
  (let [graph (subject/create-graph data)
        paths (subject/find-paths graph "North Station" "Airport")]
    (is (= 1 (count paths)))
    (is (= ["North Station", "Haymarket" "State" "Aquarium" "Maverick" "Airport"]
           (get-in paths [0 :path])))
    (is (= 6 (get-in paths [0 :steps])))))

(deftest should-find-shortest-path-from-orange-to-blue
  (let [graph (subject/create-graph data)
        path (subject/find-shortest-path graph "North Station" "Airport")]
    (is (= ["North Station", "Haymarket" "State" "Aquarium" "Maverick" "Airport"]
           path))))

(deftest should-update-table-with-new-stop
  (is (= {"stop" ["route"]}
         (subject/update-table {} "stop" "route"))))

(deftest should-update-table-with-route-for-a-stop
  (is (= {"stop" ["route-1" "route-2"]}
         (subject/update-table {"stop" ["route-1"]} "stop" "route-2"))))

(deftest should-add-all-stops-to-table
  (is (= {"stop-1" ["route"] "stop-2" ["route"]}
         (subject/add-routes-to-stops {} "route" ["stop-1" "stop-2"]))))

(deftest should-create-look-up-table
  (let [table (subject/create-look-up-table data)]
    ;; count of stops in table, Orange + Blue - 1 for State intersection
    (is (= 31 (count table)))
    (is (= ["Orange" "Blue"] (get table "State")))))

(deftest should-find-routes-for-path
  (let [table (subject/create-look-up-table data)
        graph (subject/create-graph data)
        path (subject/find-shortest-path graph "North Station" "Airport")]
    (is (= '(#{"Orange"} #{"Blue"}) 
           (subject/routes-from-path table path)))))