(ns cljs-browser-repl.console.cljs
  (:require [replumb.core :as replumb]
            [cljs-browser-repl.console :as console]))

;; ;TODO: commented out for now, because of hljs
;; (def default-matchings {})
;; {:match-round-brackets  [\( \)]
;;  :match-square-brackets [\[ \]]
;;  :match-curly-brackets  [\{ \}]
;;  :match-string          [\" \"] })

(defn handle-result!
  [console result]
  (let [write-fn (if (replumb/success? result) console/write-return! console/write-exception!)]
    (write-fn console (replumb/unwrap-result result))))

(defn cljs-read-eval-print!
  [console line]
  (try
    (replumb/read-eval-call (partial handle-result! console) line)
    (catch js/Error err
      (println "Caught js/Error during read-eval-print: " err)
      (console/write-exception! console err))))

(defn cljs-console-prompt!
  [console]
  (doto console
    (.Prompt true (fn [input]
                    (cljs-read-eval-print! console input)
                    (.SetPromptLabel console (replumb/get-prompt)) ;; necessary for namespace changes
                    (cljs-console-prompt! console)))))

(defn cljs-console!
  "Create a console for ClojureScript."
  [console-opts]
  (doto (console/new-jqconsole ".cljs-console"
                               (merge {:prompt-label (replumb/get-prompt)
                                       :disable-auto-focus true
                                       :continue-label "  "}
                                      console-opts))
    ;; (console/register-matchings! default-matchings)
    (console/color-crap!)))

(defn cljs-reset-console-and-prompt!
  "Resets the console and forces the focus onto it."
  [console]
  (console/reset-console! console)
  (console/focus-console! console)
  (cljs-console-prompt! console))

(defn cljs-clear-console!
  "Clears the console and put forces the focus onto it."
  [console]
  (console/clear-console! console)
  (console/focus-console! console))
