(defproject kolmogorov-music "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [overtone "0.9.1"]
                 [leipzig "0.10.0"]]
  :profiles {:dev
             {:plugins [[lein-midje "3.1.3"]]
              :dependencies [[midje "1.6.3"]]}})
