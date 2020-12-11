(ns defntly.core
  (:require [defntly.specs]
            [clojure.spec.alpha :as s]
            [clojure.core.specs.alpha :as specs]))

(defn update-conf [{[arity] :fn-tail :as conf} body-update-fn]
  (case arity
    :arity-1 (update-in conf [:fn-tail 1 :body 1] body-update-fn)
    :arity-n (update-in conf [:fn-tail 1 :bodies] (fn [bodies]
                                                    (map (fn [body]
                                                           (update-in body [:body 1] body-update-fn))
                                                         bodies)))))

(comment
  (throw (Exception. "aa")))
(defn defn-update-body
  "Return a `defn` form where the body is updated by `f`.
  `f` receives `name` and `body` and returns an updated body.

  It supports all the `defn` arguments (doctrings, metadata, multi-artities etc..)

  It is meant to by used to create `defn` like macros.
  Based on https://blog.klipse.tech/clojure/2019/03/08/spec-custom-defn.html
  Example:

  Let's create a `defn-try` macro that automatically wraps the function body in a `try/catch` form.

  (defn wrap-try [name body]
  `((try ~@body
      (catch Throwable ~'e
        (throw (Exception. (str \"Exception caught in function \" ~name \": \" ~'e)))))))

  (defmacro defn-try [& args]
    (defn-update-body wrap-try args))

  We use `defn-try` exactly like `defn`:

  (defn-try foo [a b]
      (+ a b))

  is macroexpanded into:

  (defn foo [a b]
  (try
    (+ a b)
    (catch
      java.lang.Throwable
      e
      (throw (str \"Exception caught in function \" \"foo\" \": \" e)))))

"
  [f args]
  (let [{:keys [fn-name] :as conf} (s/conform ::specs/defn-args args)
        new-conf (update-conf conf (partial f  (str fn-name)))
        new-args (s/unform ::specs/defn-args new-conf)]
    (cons `defn new-args)))

(defn wrap-try [name body]
  `((try ~@body
         (catch Throwable ~'e
           (throw (str "Exception caught in function " ~name ": " ~'e))))))

(defn-update-body wrap-try '(foo [a b] (+ a b)))

(comment
  (defmacro defn-try [& args]
    (defn-update-body wrap-try args))

  (defn-try foo [a b]
    (+ a b))

  (defn-try foo
    "foo has a docstring"
    ([a] (foo a 42))
    ([a b] (+ a b))))
