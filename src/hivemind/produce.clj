(ns hivemind.produce
  (:gen-class)
  (:require
   [hivemind.topics :refer [create-topic!]]
   [clojure.data.json :as json]
   [clojure.java.io :as io])
  (:import
   [java.util Properties]
   [org.apache.kafka.clients.producer
    Callback
    KafkaProducer
    ProducerConfig
    ProducerRecord]))

(defn- build-properties [config-fname]
  (with-open [config (io/reader config-fname)]
    (doto (Properties.)
      (.putAll
       {ProducerConfig/BOOTSTRAP_SERVERS_CONFIG "localhost:9092"
        ProducerConfig/KEY_SERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.StringSerializer"
        ProducerConfig/VALUE_SERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.StringSerializer"})
      (.load config))))

(defn producer! [config-fname topic]
  (let [props (build-properties config-fname)
        print-ex (comp println (partial str "failed to deliver message: "))
        print-metadata #(printf "produced record to topic %s partition [%d] @ offset %d\n"
                                (.topic %)
                                (.partition %)
                                (.offset %))
        create-msg #(let [k "alice"
                          v (json/write-str {:count %})]
                      (printf "producing record: %s\t%s\n" k v)
                      (ProducerRecord. topic k v))]
    (with-open [producer (KafkaProducer. props)]
      (create-topic! topic props)
      (let [callback (reify Callback
                       (onCompletion [_ metadata exception]
                         (if exception (print-ex exception) (print-metadata metadata))))]
        (doseq [i (range 5)]
          (.send producer (create-msg i) callback))
        (.flush producer)
        (let [futures (doall (map #(.send producer (create-msg %)) (range 5 10)))]
          (.flush producer)

          (while (not-every? future-done? futures)
            (Thread/sleep 50))
          (doseq [fut futures]
            (try (let [metadata (deref fut)]
                   (print-metadata metadata))
                 (catch Exception e (print-ex e))))))))
  (printf "10 messages were produced to topic %s" topic))

(defn -main [& args]
  (apply producer! args))
