[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/F0ieClPf)

### JDK version
- Version: 17.0.17

### Login credentials (if required)
- Teaching Support:
  - Email: teachingsupport1@sheffield.ac.uk
  - Password: password
  - Email: teachingsupport2@sheffield.ac.uk
  - Password: password
- Academic Staff:
  - Email: academic1@sheffield.ac.uk
  - Password: password
  - Email: academic2@sheffield.ac.uk
  - Password: password
- Exams Officer:
  - Email: examsofficer1@sheffield.ac.uk
  - Password: password
- External Examiner
  - Email: externalexaminer1@sheffield.ac.uk
  - Password: password

A full list of seeded data can be found in Com2008Team27Application.commandLineRunner

### Database details
- Type: H2 in-memory database
- JDBC URL: jdbc:h2:mem:postsdb
- Driver: org.h2.driver
- Username: sa
- Password: (empty)
- H2 console url: http://localhost:8080/h2-console

Notes:
- The initial data is seeded by `CommandLineRunner`

### Project Locations
- `/backend` - Server code (Spring Boot)
- `/frontend` - Client folder (contains Visual Studio solution)
- `/frontend/frontend` - Client code root for Node.js / Vite

### Run the program
- Backend
  - `cd /backend`
  - `./mvnw spring-boot:run`
- Frontend
  - `cd /frontend/frontend`
  - `npm install`
  - `npm run dev`
  - `o` (once running)


- Port in use: 52383
- URL: http://localhost:52383/

### Public-Private Key Instructions
Create a public/private key-pair

Not included in this repository is the key-pair required for JWT encoding/decoding, so you will have to recreate them as you did:

Open a mac/linux/WSL/Git Bash terminal and navigate (cd command) to the project directory and then to src/main/resources and enter the following commands

````
mkdir certs
cd certs
openssl genrsa -out keypair.pem 2048
openssl rsa -in keypair.pem -pubout -out public.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
rm keypair.pem
````

Now if you check the folder you are in (either with ls, dir or by looking in the folder browser in your IDE) you should see the generated key-pair public.pem and private.pem