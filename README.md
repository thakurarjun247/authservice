# AuthService
Once of the most crucial microservice of the project freedom.

https://www.youtube.com/watch?v=yodeo205pp0&t=84s

## how to create users and access app on swagger
open
http://localhost:8081/swagger-ui/index.html

### signup
go to /signup under HomeController  
press try it out  
in the request body paste this, make sure the role is ADMIN if you want to see all users, later:

`{
  "email": "admin@admin.com",
  "password": "admin",
  "userRoles": [
    "ADMIN"
  ],
  "authProvider": "LOCAL",
  "userStatus": "ACTIVE"
}`

### login
go to `swagger-login` endpoint  
don't use `login` its deperecated  
enter username and password  
you are done.

### view all users
go to `/adimin/users` under admincontroller  
try it out



## curl for endpoints

### login
When you call /login, Spring Security is handling it internally.

Spring expects a form-data POST (application/x-www-form-urlencoded) by default — not JSON.

When you send JSON, Spring Security's default UsernamePasswordAuthenticationFilter doesn't understand it.
It is expecting form fields, not JSON fields.

curl -X POST http://localhost:8081/login \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "username=arjun@example.com&password=password123"

### signup
curl -X POST http://localhost:8081/signup \
-H "Content-Type: application/json" \
-d '{
"email": "admin@gmail.com",
"password": "admin",
"userRoles": ["ADMIN"],
"authProvider": "LOCAL",
"userStatus": "ACTIVE",
"createdAt": "2025-04-26T00:00:00Z",
"updatedAt": "2025-04-26T00:00:00Z"
}'

### findbyemail
curl -X GET "http://localhost:8081/users?email=arjun@example.com"
### findAll
curl -X GET "http://localhost:8081/users"

## login flow on postman

Perfect plan! 🔥  
You're super clear — let's go step-by-step cleanly.

---

✅ **First step:**   
Create user:
```bash
curl -X POST http://localhost:8081/signup \
-H "Content-Type: application/json" \
-d '{
  "email": "JSG@Mahadev.com",
  "password": "Bhagvaan",
  "userRoles": ["ADMIN"],
  "authProvider": "LOCAL",
  "userStatus": "ACTIVE",
  "createdAt": "2025-04-26T00:00:00Z",
  "updatedAt": "2025-04-26T00:00:00Z"
}'
```


---

✅ **Second step:** 
**Login** and **capture the cookie** (`JSESSIONID`) into a cookie file:

```bash
curl -X POST http://localhost:8081/login \
-c cookies.txt \
-H "Content-Type: application/x-www-form-urlencoded" \
-d 'username=JSG@Mahadev.com&password=Bhagvaan'
```

- `-c cookies.txt` → save cookies (JSESSIONID) in a file.
- Important:  
  Spring Security expects login form data as `application/x-www-form-urlencoded`,  
  so **no JSON here** — that's why the `-d` body is like `username=...&password=...`.

---

✅ **Third step:**  
Now you are logged in (cookie captured).  
Time to **access the secured endpoint** by sending the cookie:

```bash
curl -X GET http://localhost:8081/secured \
-b cookies.txt
```
- `-b cookies.txt` → read and send cookie automatically!

---
