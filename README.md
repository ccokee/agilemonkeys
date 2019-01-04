# Test Agile Monkeys

## Build/Run
This is a spring boot application with Graddle as automation system

* **Build and run** ```./gradlew build -x test && java -jar build/libs/agile-monkeys-test-1.0.jar```
* **Build and run tests** ```./gradlew build```

The goal of this project is to manage customer data for a small Shop. It will work as the backend side for a CRM interface. There are 2 main entities, Customer and User.

## User
*  username
*  password: Will be auto-generated by the system
*  name
*  surname
*  role: Can be ADMIN or USER.

All the operations related to User can only be called by an ADMIN User.

## Create New User

* **URL** /user
    
* **Method** `POST`

* **Head** Content-Type: application/json

* **Data** {"username":"test-user", "name":"John", "surname":"Mayer", "role":"USER"}

* **Success Response:**

  * **Code:** 200 <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"User {username} successfully added."}`

* **Error Responses:**

  * **Condition:** Username is not included in request or is empty or is a white space <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Username must contain at least one non-whitespace character."}`

  * **Condition:** Username has less than 5 characters or has more than 32. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Username must have between 5 and 32 characters."}`

  * **Condition:** Name has less than 1 character or has more than 50. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Name must be between 1 and 50 characters."}`

  * **Condition:** Surname has less than 1 character or has more than 50. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Surname must be between 1 and 50 characters."}`

  * **Condition:** Password is provided in the request. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Password is auto-generated."}`

  * **Condition:** Role is not provided in the request. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Role must be provided."}`

  * **Condition:** Role is different from ADMIN or USER <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Role can only be ADMIN or USER."}`

  * **Condition:** Provided username already exists in the DB. <br/>
    **Code:** 409 Conflict <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"User {username} already exists."}`

* **Sample Call:**
  ```sh
    $ curl -u username:password -d '{"username":"test-user", "name":"John", "surname":"Mayer", "role":"USER"}' -H "Content-Type: application/json" -X POST http://localhost:8080/user
  ```


## Find User by username

* **URL** /user/{username}

* **Method** `GET`

* **URL Params** username

* **Success Response:**

  * **Code:** 200 <br/>
    **Content:** `{"username":"test-user","name":"John","surname":"Mayer","role":"USER"}`

* **Error Responses:**

  * **Condition:** Provided username can't be found in the DB. <br/>
    **Code:** 404 Not Found <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"User with username {username} not found."}`

* **Sample Call:**
  ```sh
    $ curl -u username:password http://localhost:8080/user/test-user
  ```


## Find all users

* **URL** /users

* **Method** `GET`

* **Success Response:**

  * **Code:** 200 <br/>
    **Content:** `[{"username":"test-user","name":"John","surname":"Mayer","role":"USER"}, {"username":"test-user2","name":"Tony","surname":"Baier","role":"ADMIN"}]`

* **Sample Call:**
  ```sh
    $ curl -u username:password http://localhost:8080/users
  ```


## Update User

* **URL** /update-user

* **Method** `PUT`

* **Head** Content-Type: application/json

* **Data** {"username":"test-user", "name":"John", "surname":"Mayer", "role":"USER"}

* **Success Response:**

  * **Code:** 200 <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"User {username} successfully updated."}`

* **Error Responses:**

  * **Condition:** Username is not included in request or is empty or is a white space <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Username must contain at least one non-whitespace character."}`

  * **Condition:** Name has less than 1 character or has more than 50. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Name must be between 1 and 50 characters."}`

  * **Condition:** Surname has less than 1 character or has more than 50. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Surname must be between 1 and 50 characters."}`

  * **Condition:** Role is not provided in the request. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Role must be provided."}`

  * **Condition:** Role is different from ADMIN or USER <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Role can only be ADMIN or USER."}`

  * **Condition:** Password is provided in the request. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Password needs to be reset from another API."}`

  * **Condition:** Provided username can't be found in the DB. <br/>
    **Code:** 404 Not Found <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Username {username} doesn't exist in DB."}`

* **Sample Call:**
  ```sh
    $ curl -u username:password -d '{"username":"test-user", "name":"Jack", "surname":"Wilson", "role":"ADMIN"}' -H "Content-Type: application/json" -X PUT http://localhost:8080/update-user
  ```

## Delete user by username

* **URL** /user/{username}

* **Method** `DELETE`

* **Success Response:**

  * **Code:** 200 <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"User {username} successfully deleted."}`

* **Error Responses:**

  * **Condition:** Provided username can't be found in the DB. <br/>
    **Code:** 404 Not found <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Username {username} doesn't exist in DB."}`

* **Sample Call:**
  ```sh
    $ curl -u username:password -X DELETE http://localhost:8080/user/test-user
  ```

## Customer
* id: uuid auto generated by system
* name
* surname
* createdBy: generated by system
* lastModifiedBy: generated by system
* photo: Will be provided as part of a multi par request. When retrieving, We'll have the URL of the picture.
* email

All the operations related to Customer can be called by either an ADMIN or USER role.


## Create New Customer

* **URL** /customer

* **Method** `POST`

* **Head** Content-Type: multipart/form-data

* **Data, First part** {"name":"Peter", "surname":"King", "email":"peterk@gmail.com"}

* **Data, Second part** test.png


* **Success Response:**

  * **Code:** 200 <br/>
    **Content:** `3b748abc-9e60-4de0-9bd5-0d44cd956272` (Representing the generated ID which is a UUID)

* **Error Responses:**

  * **Condition:** id is provided in the request. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"id will be auto-generated."}`

  * **Condition:** Name has less than 1 character or has more than 50. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Name must be between 1 and 50 characters."}`

  * **Condition:** Surname has less than 1 character or has more than 50. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Surname must be between 1 and 50 characters."}`

  * **Condition:** createdBy is provided in the request. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"createdBy is generated by the system."}`

  * **Condition:** lastModifiedBy is provided in the request. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"lastModifiedBy is generated by the system."}`

  * **Condition:** Provided email value doesn't have an email complaint format. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Email format not complaint."}`

  * **Condition:** Provided file is not a PNG, GIF or JPEG and is more than 5 MB. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Provided file must be either a PNG, GIF or JPEG and must be less than 5 MB."}`

* **Sample Call:**
  ```sh
    $ curl -u username:password -X POST -H "Content-Type:multipart/form-data" -F 'customer={"name":"Peter", "surname":"King", "email":"peterk@gmail.com"};type=application/json' -F "photo=@test.png" http://localhost:8080/customer
  ```


## Find Customer by id

* **URL** /customer/{id}

* **Method** `GET`

* **URL Params** id

* **Success Response:**

  * **Code:** 200 <br/>
    **Content:** `{"id":"3b748abc-9e60-4de0-9bd5-0d44cd956272","name":"Peter","surname":"King","photoUrl":"https://www.googleapis.com/download/storage/v1/b/crm-photos/o/3b748abc-9e60-4de0-9bd5-0d44cd956272.png?generation=1546608588825152&alt=media","createdBy":"rdoriame","lastModifiedBy":"rdoriame","email":"peterk@gmail.com"}`

* **Error Responses:**

  * **Condition:** Provided id can't be found in the DB. <br/>
    **Code:** 404 Not Found <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Customer ID {id} not found."}`

* **Sample Call:**
  ```sh
    $ curl -u username:password http://localhost:8080/customer/3b748abc-9e60-4de0-9bd5-0d44cd956272
  ```


## Find all customers

* **URL** /users

* **Method** `GET`

* **Success Response:**

  * **Code:** 200 <br/>
    **Content:** `[{"id":"3b748abc-9e60-4de0-9bd5-0d44cd956272","name":"Peter","surname":"King","photoUrl":"https://www.googleapis.com/download/storage/v1/b/crm-photos/o/3b748abc-9e60-4de0-9bd5-0d44cd956272.png?generation=1546608588825152&alt=media","createdBy":"rdoriame","lastModifiedBy":"rdoriame","email":"peterk@gmail.com"},{"id":"b5d9210b-5e51-4823-bbd8-071e02212c1e","name":"Madelaine","surname":"Smith","photoUrl":null,"createdBy":"rdoriame","lastModifiedBy":"rdoriame","email":null}]`

* **Sample Call:**
  ```sh
    $ curl -u username:password http://localhost:8080/customers
  ```


## Update Customer

* **URL** /update-customer

* **Method** `PUT`

* **Head** Content-Type: multipart/form-data

* **Data, First part** {"id":"3b748abc-9e60-4de0-9bd5-0d44cd956272", "name":"Peter", "surname":"King", "email":"peterk@gmail.com"}

* **Data, Second part** test.png


* **Success Response:**

  * **Code:** 200 <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Customer b5d9210b-5e51-4823-bbd8-071e02212c1e successfully updated."}`

* **Error Responses:**

  * **Condition:** id not provided in the request. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Valid id must be provided."}`

  * **Condition:** Name has less than 1 character or has more than 50. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Name must be between 1 and 50 characters."}`

  * **Condition:** Surname has less than 1 character or has more than 50. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Surname must be between 1 and 50 characters."}`

  * **Condition:** createdBy is provided in the request. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"createdBy is generated by the system."}`

  * **Condition:** lastModifiedBy is provided in the request. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"lastModifiedBy is generated by the system."}`

  * **Condition:** Provided email value doesn't have an email complaint format. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Email format not complaint."}`

  * **Condition:** Provided file is not a PNG, GIF or JPEG and is more than 5 MB. <br/>
    **Code:** 400 Bad Request <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Provided file must be either a PNG, GIF or JPEG and must be less than 5 MB."}`

  * **Condition:** Provided id can't be found in the DB. <br/>
    **Code:** 404 Not Found <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Customer ID {id} doesn't exist in DB."}`

* **Sample Call:**
  ```sh
    $ curl -u username:password -X PUT -H "Content-Type:multipart/form-data" -F 'customer={"id":"3b748abc-9e60-4de0-9bd5-0d44cd956272","name":"John", "surname":"Smithers", "email":"johns@gmail.com"};type=application/json' -F "photo=@test2.png" http://localhost:8080/update-customer
  ```


## Delete Customer by id

* **URL** /customer/{id}

* **Method** `DELETE`

* **Success Response:**

  * **Code:** 200 <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"User {username} successfully deleted."}`

* **Error Responses:**

  * **Condition:** Provided id can't be found in the DB. <br/>
    **Code:** 404 Not found <br/>
    **Content:** `{"timestamp":"YYYY-MM-DDTHH:MM:SS.SSS+0000","message":"Customer ID {id} doesn't exist in DB."}`

* **Sample Call:**
  ```sh
    $ curl -u username:password -X DELETE http://localhost:8080/customer/3b748abc-9e60-4de0-9bd5-0d44cd956272
  ```