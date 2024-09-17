(ns hivemind.topics
  (:gen-class)
  (:require
   [hivemind.core :refer [app-config]]
   [jackdaw.admin :as ja]
   [jackdaw.serdes.json :as json])


  (:import
   [org.apache.kafka.clients.admin AdminClient NewTopic]
   [org.apache.kafka.common.errors TopicExistsException]))

(defn topic-config
  "takes a TOPIC-NAME and maybe KEY-SERDE and VALUE-SERDE"
  ([topic-name]
   (topic-config topic-name (json/serde) (json/serde)))

  ([topic-name key-serde value-serde]
   {:topic-name topic-name
    :partition-count 1                  ; could be dynamic
    :replication-factor 1               ; could be dynamic
    :key-serde key-serde
    :value-serde value-serde
    :topic-config {}}))

(defn- create-topics-with-config-list [topic-config-list]
  (with-open [client (ja/->AdminClient (app-config))]
    (ja/create-topics! client topic-config-list)))

(defn create-topic!
  ([topic-name]
   (create-topic! topic-name app-config))

  ([topic partitions replication config]
   (let [ac (AdminClient/create config)]
     (try
       (.createTopics ac [(NewTopic. ^String topic (int partitions) (short replication))])
       (catch TopicExistsException _ nil)
       (finally (.close ac)))))

  ([topic config]
   (create-topic! topic 1 1 config)))

(defn create-topics [topic-name-list]
  (create-topics-with-config-list (map create-topic! topic-name-list)))
