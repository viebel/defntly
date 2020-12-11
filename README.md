# defntly

A utility function to create `defn` like macros in Clojure/Script

# Usage

Leiningen/Boot: `[viebel/defntly "0.0.1"]`

Clojure CLI/deps.edn: `viebel/defntly {:mvn/version "0.0.1"}`

```clj
(require '[dfntly.core :refer [defn-update-body]])
```

# Examples

Let's create a `defn-try` macro that automatically wraps the function body in a `try/catch` form.

```clj
(require '[dfntly.core :refer [defn-update-body]])

(defn wrap-try [name body]
  `((try ~@body
    (catch Throwable ~'e
      (throw (Exception. (str \"Exception caught in function \" ~name \": \" ~'e)))))))

(defmacro defn-try [& args]
  (defn-update-body wrap-try args))
```

We use `defn-try` exactly like `defn`:

```clj
(defn-try foo [a b]
    (+ a b))
```

is macroexpanded into:

```clj
 (defn foo [a b]
  (try
    (+ a b)
    (catch
      java.lang.Throwable
      e
      (throw (str "Exception caught in function " "foo" ": " e)))))
```

We can pass to our freshly created `defn-try` macro all the args that `defn` receive (metadata, docstring, multi arity).

For instance, a mutli arity function with a docstring like:

```clj
(defn-try foo
    "foo has a docstring"
    ([a] (foo a 42))
    ([a b] (+ a b)))
```

is macroexpanded into:

```clj
(defn foo
  "foo has a docstring"
  ([a]
    (try
      (foo a 42)
      (catch
        java.lang.Throwable
        e
        (throw (str "Exception caught in function " "foo" ": " e)))))
  ([a b]
    (try
      (+ a b)
      (catch
        java.lang.Throwable
        e
        (throw (str "Exception caught in function " "foo" ": " e))))))
```



# How it works

Explanations about you the internals of `defn-update-body` and how it leverages `clojure.spec` 
in [this article](https://blog.klipse.tech/clojure/2019/03/08/spec-custom-defn.html).

