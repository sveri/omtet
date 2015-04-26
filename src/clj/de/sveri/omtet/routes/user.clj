(ns de.sveri.omtet.routes.user
  (:require [compojure.core :refer [routes GET POST]]
    [buddy.hashers :as hashers]
    [ring.util.response :as resp]
    [noir.session :as sess]
    [noir.validation :as vali]
    [taoensso.timbre :as timb]
    [clojure-miniprofiler :as cjmp]
    [de.sveri.omtet.layout :as layout]
    [de.sveri.omtet.db.users :as db]
    [de.sveri.omtet.service.user :as uservice]
    [de.sveri.omtet.service.auth :as auth])
  (:import (net.tanesha.recaptcha ReCaptchaImpl)))

(defn- connectReCaptch [recaptcha_response_field recaptcha_challenge_field]
  (let [reCaptcha (new ReCaptchaImpl)]
    (.setPrivateKey reCaptcha "private-recaptcha-key")
    (.checkAnswer reCaptcha "yourdomain.com", recaptcha_challenge_field, recaptcha_response_field)))

(defn vali-password? [pass confirm & [form-current-pass current-pass]]
  (vali/rule (vali/min-length? pass 5)
             [:pass "Password must be at least 5 characters."])
  (vali/rule (= pass confirm)
             [:confirm "Entered passwords do not match."])
  (when current-pass
    (vali/rule (hashers/check form-current-pass current-pass)
               [:oldpass "Current password was incorrect."])))

(defn valid-register? [email pass confirm & [recaptcha_response_field recaptcha_challenge_field]]
  (vali/rule (vali/has-value? email)
             [:id "An email address is required."])
  (vali/rule (vali/is-email? email)
             [:id "A valid email is required."])
  (vali/rule (not (db/username-exists? email))
             [:id "This username already exists. Choose another."])
  (when recaptcha_challenge_field
    (vali/rule (.isValid (connectReCaptch recaptcha_response_field recaptcha_challenge_field))
               [:captcha "Please provide the correct captcha input."]))
  (vali-password? pass confirm)
  (not (vali/errors? :id :pass :confirm :captcha)))

(defn admin-page [params]
  (let [users (cjmp/trace "all users" (db/get-all-users (get params :filter)))]
    (layout/render "user/admin.html" (merge {:users users :roles auth/available-roles}
                                            params))))

(defn login-page [& [content]]
  (layout/render "user/login.html" content))

(defn account-created-page [& [_]]
  (layout/render "user/account-created.html"))

(defn account-activated-page []
  (layout/render "user/account-activated.html"))

(defn signup-page [& [errormap]]
  (layout/render "user/signup.html" errormap))

(defn activate-account [id]
  (if (db/get-user-by-act-id id)
    (do (db/set-user-active id)
        (account-activated-page))
    (signup-page {:email-error "Please provide a correct activation id."})))

(defn changepassword-page [& [msgmap]]
  (layout/render "user/changepassword.html" msgmap))

(defn logout [] (sess/clear!) (resp/redirect "/"))

(defn login [request]
  (let [username (get-in request [:form-params "username"])
        password (get-in request [:form-params "password"])
        nexturl (get-in request [:form-params "nexturl"])]
    (if-let [user (db/get-user-by-email username)]
      (try
        (cond
          (= false (:is_active user)) (login-page {:error "Please activate your account first."})
          (= false (hashers/check password (get user :pass ""))) (login-page {:error "Please provide a correct password."})
          :else (do (sess/put! :role (:role user)) (sess/put! :identity username)
                    (resp/redirect (or nexturl "/"))))
        (catch Exception e (timb/error e "Something messed up while logging in user: " username " with error: " e)
                           (login-page {:error "Some error occured."})))
      (login-page {:error "Please provide a correct username."}))))

(defn send-reg-mail [sendmail? email activationid config succ-cb-page error-cb-page]
  (if sendmail?
    (if (uservice/send-activation-email email activationid config)
      (succ-cb-page)
      (error-cb-page {:email-error "Something went wrong sending the email, please contact us."}))
    (succ-cb-page (layout/flash-result (str "User added.") "alert-success"))))

(defn add-user [email password confirm sendmail? succ-cb-page error-cb-page config
                & [recaptcha_response_field recaptcha_challenge_field]]
  (if (valid-register? email password confirm recaptcha_response_field recaptcha_challenge_field)
    (let [activationid (uservice/generate-activation-id)
          pw_crypted (hashers/encrypt password)]
      (db/create-user email pw_crypted activationid)
      (send-reg-mail sendmail? email activationid config succ-cb-page error-cb-page))
    (let [email-error (vali/on-error :id first)
          pass-error (vali/on-error :pass first)
          confirm-error (vali/on-error :confirm first)
          captcha-error (vali/on-error :captcha first)]
      (error-cb-page {:email-error email-error :pass-error pass-error :confirm-error confirm-error :email email
                      :captcha-error captcha-error}))))

(defn changepassword [oldpassword password confirm]
  (let [user (db/get-user-by-email (uservice/get-logged-in-username))]
    (vali-password? password confirm oldpassword (:pass user))
    (if (not (vali/errors? :oldpass :pass :confirm))
      (do (db/change-password (:email user) (hashers/encrypt password))
          (changepassword-page (layout/flash-result (str "Password changed.") "alert-success")))
      (let [old-error (vali/on-error :oldpass first)
            pass-error (vali/on-error :pass first)
            confirm-error (vali/on-error :confirm first)]
        (changepassword-page {:pass-error pass-error :confirm-error confirm-error :old-error old-error})))))

(defn really-delete-page [uuid]
  (layout/render "user/reallydelete.html" {:uuid uuid :username (:email (db/get-user-by-uuid uuid))}))

(defn really-delete [uuid delete_cancel]
  (if (= delete_cancel "Cancel")
    (do (layout/flash-result (str "Deletion canceled.") "alert-warning")
        (resp/redirect "/admin/users"))
    (do (db/delete-user uuid)
        (layout/flash-result (str "User deleted successfully.") "alert-success")
        (resp/redirect "/admin/users"))))

(defmulti update-user (fn [update_delete _ _ _] update_delete))
(defmethod update-user "Update" [_ user-uuid role active]
  (let [role (if (= "none" role) "" role)
        act (= "on" active)]
    (db/update-user user-uuid {:role role :is_active act}))
  (let [user (db/get-user-by-uuid user-uuid)]
    (admin-page (layout/flash-result (str "User " (:email user) " updated successfully.") "alert-success"))))

(defmethod update-user "Delete" [_ user-uuid _ _]
  (really-delete-page user-uuid))

(defn user-routes [config]
  (routes
    (GET "/user/login" [next] (login-page {:nexturl next}))
    (POST "/user/login" req (login req))
    (GET "/user/logout" [] (logout))
    (GET "/user/changepassword" [] (changepassword-page))
    (POST "/user/changepassword" [oldpassword password confirm] (changepassword oldpassword password confirm))
    (POST "/admin/user/update" [user-uuid role active update_delete] (update-user update_delete user-uuid role active))
    (POST "/admin/user/delete" [user-uuid delete_cancel] (really-delete user-uuid delete_cancel))
    (POST "/admin/user/add" [email password confirm] (add-user email password confirm false admin-page
                                                               admin-page config))
    (GET "/admin/users" [filter] (admin-page {:filter filter}))))

(defn registration-routes [config]
  (routes
    (GET "/user/accountcreated" [] (account-created-page))
    (GET "/user/activate/:id" [id] (activate-account id))
    (POST "/user/signup" [email password confirm recaptcha_response_field recaptcha_challenge_field]
          (add-user email password confirm true account-created-page signup-page config
                    recaptcha_response_field recaptcha_challenge_field))
    (GET "/user/signup" [] (signup-page))))
