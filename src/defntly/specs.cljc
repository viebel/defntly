(ns defntly.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.core.specs.alpha :as specs]))


;;there is a problem with this defn-args spec: unform and conform are not fully inlined (unform returns lists instead of vectors).
;; The cool thing is that we can monkey patch :defn-args so that unform and conform are fully inlined.

(s/def ::specs/seq-binding-form
  (s/and vector?
         (s/conformer identity vec)
         (s/cat :elems (s/* ::specs/binding-form)
                :rest (s/? (s/cat :amp #{'&} :form ::specs/binding-form))
                :as (s/? (s/cat :as #{:as} :sym ::specs/local-name)))))

(defn arg-list-unformer [a]
  (vec
   (if (and (coll? (last a)) (= '& (first (last a))))
     (concat (drop-last a) (last a))
     a)))

(s/def ::specs/param-list
  (s/and
   vector?
   (s/conformer identity arg-list-unformer)
   (s/cat :args (s/* ::specs/binding-form)
          :varargs (s/? (s/cat :amp #{'&} :form ::specs/binding-form)))))
