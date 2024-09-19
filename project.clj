(defproject hivemind "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/algo.generic "0.1.3"]
                 [com.brunobonacci/mulog "0.9.0"]
                 [com.brunobonacci/mulog-kafka "0.9.0"]
                 [danlentz/clj-uuid "0.1.7"]
                 ;; [http-kit/http-kit "2.8.0"]

                 ;; [lambdaisland/kaocha "1.91.1392"]
                 ;; [lambdaisland/kaocha-cucumber "0.11.100"]

                 [org.apache.kafka/kafka-clients "2.1.0"]
                 [org.apache.kafka/kafka-streams "2.1.0"]
                 [org.apache.kafka/kafka-streams-test-utils "2.1.0"]

                 [fundingcircle/jackdaw "0.6.4"]]
  :main ^:skip-aot hivemind.core
  :target-path "target/%s"
  :repositories [["confluent" "https://packages.confluent.io/maven/"]]
  :aliases {"producer" ["run" "-m" "hivemind.core"]})
