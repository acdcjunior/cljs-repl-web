(ns cljs-bootstrap.common
  (:require [clojure.string :as string]))

(defn echo-callback
  "Echoes the input success and result, returning [success,
  result]. Useful for debugging."
  [success result]
  [success result])

(def result "Returns the result of an evaluation" second)

(def success? "Returns if the evaluation was successful" first)

(defn wrap-success
  "Wraps the message in a success map."
  [message]
  {:value message})

(defn wrap-error
  "Wraps the message in a error map."
  [message]
  {:error message})

(defn inline-newline?
  "Returns true if the string contains the newline \\\\n or \\\\r as
  characters."
  [s]
  (re-matches #"\\{2,}n|\\{2,}r" s))

(defn valid-eval-result?
  "Is the string returned from an evaluation valid?"
  [result]
  (and (string? result) (not (inline-newline? result))))

(defn valid-eval-error?
  "Is the string returned from an evaluation valid?"
  [error]
  (instance? js/Error error))

(defn extract-message
  "Iteratively extracts messages inside (nested #error objects), returns
  a string. Be sure to pass #error object here."
  [err]
  (loop [e err msgs [(.-message err)]]
    (if-let [next-err (.-cause e)]
      (recur next-err (conj msgs (.-message next-err)))
      (if (seq msgs)
        (string/join " - " msgs)
        ""))))

(defn error-keyword-not-supported
  "Yields a \"keyword not supported\" error map. Receives the
  symbol/keyword printed in the message and ex-info data."
  [keyword ex-info-data]
  (wrap-error (ex-info (str "The " keyword " keyword is not supported at the moment")
                       ex-info-data)))

(defn error-argument-must-be-symbol
  "Yields a \"Argument must a be a symbol\" error map. Receives the
  symbol/fn name printed in the message and ex-info data."
  [symbol ex-info-data]
  (wrap-error (ex-info (str "Argument to " symbol " must be a symbol") ex-info-data)))