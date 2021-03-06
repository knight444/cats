;; Copyright (c) 2014, Andrey Antukh
;; Copyright (c) 2014, Alejandro Gómez
;; All rights reserved.
;;
;; Redistribution and use in source and binary forms, with or without
;; modification, are permitted provided that the following conditions
;; are met:
;;
;; 1. Redistributions of source code must retain the above copyright
;;    notice, this list of conditions and the following disclaimer.
;; 2. Redistributions in binary form must reproduce the above copyright
;;    notice, this list of conditions and the following disclaimer in the
;;    documentation and/or other materials provided with the distribution.
;;
;; THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
;; IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
;; OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
;; IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
;; INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
;; NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
;; DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
;; THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
;; (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
;; THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

(ns cats.protocols
  "Cathegory theory types definition.")

(defprotocol Context
  "A type that is part of a computational context."
  (get-context [_] "Get the monad instance for curent value.")
  (get-value [_] "Unwrap the value from context."))

(defprotocol Functor
  (fmap [ftor f fv] "Applies function f to the value(s) inside the context of the functor fv."))

(defprotocol Applicative
  (fapply [app af av]
    "Applies the function(s) inside ag's context to the value(s)
     inside av's context while preserving the context.")
  (pure [app v]
    "Takes any context monadic value ctx and any value v, and puts
     the value v in the most minimal context of same type of ctx"))

(defprotocol Monad
  (mreturn [m v])
  (mbind [m mv f]))

(defprotocol MonadZero
  "A `Monad` that supports the notion of an identity element."
  (mzero [m] "The identity element for `ctx`."))

(defprotocol MonadPlus
  "A `MonadZero` that supports the notion of addition."
  (mplus [m mv mv'] "An associative addition operation."))

(defprotocol MonadState
  "A `Monad` formed by functions from states to a new state
  and a (poosibly monadic) value."
  (get-state [m] "Return the current state.")
  (put-state [m newstate] "Update the state.")
  (swap-state [m f] "Apply a function to the current state and update it."))

(defprotocol MonadReader
  "A `Monad` with a read-only access to an environment value."
  (ask [m] "Return the current environment.")
  (local [m f reader] "Create a reader in a modified version of the environment."))

(defprotocol MonadWriter
  "A `Monad` that accumulates a log."
  (listen [m mv] "Given a writer, yield a (value, log) pair as a value.")
  (tell [m v] "Add the given value to the log.")
  (pass [m mv]
    "Given a writer whose value is a pair with a function as its second element,
     yield a writer which has the first element of the pair as the value and
     the result of applying the aforementioned function to the log as the new log."))

(defprotocol MonadTrans
  (base [mt] "Return the base monad of this transformer.")
  (inner [mt] "Return the monad that this transformer wraps.")
  (lift [m mv]
    "Lift a value from the parameterized monad to the transformer."))
