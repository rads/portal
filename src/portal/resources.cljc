(ns ^:no-doc portal.resources
  #?(:cljs (:require-macros portal.resources))
  #?(:clj (:require [clojure.java.io :as io]))
  #?(:joyride (:require ["vscode" :as vscode]
                        ["ext://djblue.portal$resources" :as resources])))

(defn resource [_resource-name]
  #?(:joyride
     (let [vscode  (js/require "vscode")
           path    (js/require "path")
           ^js uri (-> vscode .-workspace .-workspaceFolders (aget 0) .-uri)
           fs-path (.-fsPath uri)]
       (.join path (if-not (undefined? fs-path) fs-path (.-path uri))
              "resources"
              _resource-name))))

(defonce ^:no-doc resources (atom {}))

#?(:clj
   (defmacro inline
     "For runtime resources, ensure the inline call happens at the namespace
     top-level to ensure resources are pushed into `resources for use as part of
     the inline fn."
     [resource-name]
     (try
       `(-> resources
            (swap! assoc ~resource-name ~(slurp (io/resource resource-name)))
            (get ~resource-name))
       (catch Exception e
         (println e))))
   :joyride (defn inline [resourece-name] (resources/inline resourece-name))
   :cljs    (defn inline [resourece-name] (get @resources resourece-name)))