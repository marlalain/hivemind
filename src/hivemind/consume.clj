(ns hivemind.consume
  (:gen-class)
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as jio])
  (:import
   [java.time Duration]
   [java.util Properties]
   [org.apache.kafka.clients.consumer ConsumerConfig KafkaConsumer]))

(defn- build-properties [config-fname]
  (with-open [config (jio/reader config-fname)]
    (doto (Properties.)
      (.putAll {ConsumerConfig/GROUP_ID_CONFIG "clj_tasks_group"
                ConsumerConfig/BOOTSTRAP_SERVERS_CONFIG "localhost:9092"
                ConsumerConfig/AUTO_OFFSET_RESET_CONFIG "earliest"
                ConsumerConfig/KEY_DESERIALIZER_CLASS_CONFIG   "org.apache.kafka.common.serialization.StringDeserializer"
                ConsumerConfig/VALUE_DESERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.StringDeserializer"})
      (.load config))))

(defn consumer! [config-fname topic]
  (with-open [consumer (KafkaConsumer. (build-properties config-fname))]
    (.subscribe consumer [topic])
    (loop [tc 0 records []]
      (let [new-tc (reduce (fn [tc record]
                             (let [value (.value record)
                                   cnt (get (json/read-str value) "count")
                                   new-tc (+ tc cnt)]
                               (printf "consumed record [%s] -> [%s]. total: [%s]\n"
                                       (.key record)
                                       value
                                       new-tc) new-tc)) tc records)]
        (println "waiting for message in KafkaConsumer.pool")
        (recur new-tc (seq (.poll consumer (Duration/ofSeconds 1))))))))

(defn -main [& args] (apply consumer! args))
