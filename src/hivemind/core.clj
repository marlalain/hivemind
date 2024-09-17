(ns hivemind.core
  (:gen-class)
  (:require
   [clojure.algo.generic.functor :refer [fmap]]
   [clojure.tools.logging :refer [info]]
   [jackdaw.serdes]
   [jackdaw.serdes.resolver :as resolver]
   [jackdaw.serdes.edn :as jse]
   [jackdaw.streams :as js])

  (:import
   [org.apache.kafka.clients.producer ProducerConfig]))

(defn topic-config ([topic-name]
                    (topic-config topic-name (jse/serde) (jse/serde)))
  ([topic-name key-serde value-serde]
   {:topic-name topic-name
    :partition-count 1
    :replication-factor 1
    :key-serde key-serde
    :value-serde value-serde
    :topic-config {}}))

(defn- build-properties []
  {"application.id" "hivemind"
   ProducerConfig/BOOTSTRAP_SERVERS_CONFIG "localhost:9092"})

(defn build-topology
  "returns a configured topology builder"
  [builder]
  (-> (js/kstream builder (topic-config "tasks"))
      (js/peek (fn [[k v]]
                 (info (str {:key k :value v}))))
      (js/to (topic-config "tasks-stream")))
  builder)

(defn start-app [app-config]
  (let [builder (js/streams-builder)
        topology (build-topology builder)
        app (js/kafka-streams topology app-config)]
    (js/start app)
    (info "hivemind booted")
    app))

(defn stop-app [app]
  (js/close app)
  (info "hivemind is shutting down..."))

(defn -main [& _] (start-app (build-properties)))
