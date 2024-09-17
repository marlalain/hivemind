(ns hivemind.core
  (:gen-class)
  (:require
   [clojure.tools.logging :refer [info]]
   [hivemind.produce]
   [hivemind.topics :refer [create-topics topic-config]]
   [jackdaw.serdes]
   [jackdaw.streams :as js])

  (:import
   [org.apache.kafka.clients.producer ProducerConfig]))

(defn app-config
  "generates app configuration"
  []
  {"application.id" "hivemind"
   ProducerConfig/BOOTSTRAP_SERVERS_CONFIG "localhost:9092" ; TODO move to env
   })

(defn build-topology
  "returns a configured topology builder"
  [builder]
  (hivemind.topics/create-topics ["tasks" "tasks-stream"])
  (-> (js/kstream builder (topic-config "tasks"))
      (js/peek (fn [[k v]]
                 (info (str {:key k :value v}))))
      (js/to (topic-config "tasks-stream")))
  builder)

(defn start-app [app-config]
  (let [builder (js/streams-builder)
        topology (build-topology builder)
        app (js/kafka-streams topology app-config)]
    (create-topics ["tasks" "tasks-stream"])
    (js/start app)
    (info "hivemind booted")
    app))

(defn stop-app [app]
  (js/close app)
  (info "hivemind is shutting down..."))

(defn -main [& _] (start-app (app-config)))
