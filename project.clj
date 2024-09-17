(defproject hivemind "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/data.json "2.5.0"]
                 [http-kit/http-kit "2.8.0"]
                 [lambdaisland/kaocha "1.91.1392"]
                 [lambdaisland/kaocha-cucumber "0.11.100"]
                 [ring "1.10.0"]
                 [ring/ring-json "0.5.1"]
                 [compojure "1.7.0"]
                 [org.apache.kafka/kafka-clients "3.5.1"]
                 [cheshire "5.11.0"]]
  :main ^:skip-aot hivemind.core
  :target-path "target/%s"
  :aliases {"producer" ["run" "-m" "hivemind.core"]})
