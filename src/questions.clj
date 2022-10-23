(ns questions
  (:require [question1 :refer [print-question1]]
            [question2 :refer [print-question2]]
            [question3 :refer [print-question3]])
  (:gen-class))


;; Simple entry point that aggregates all the questions together
(defn -main []
  (print-question1)
  (println "")
  (print-question2)
  (println "")
  (print-question3))