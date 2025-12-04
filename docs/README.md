## **How to Run the Project**

### **Requirements**
- Java 17
- Maven
- PostgreSQL

---

## **Option 1: Run Locally**

### **Environment Variables Setup**

Create a `.env` file in the **root folder** of the project with the following variables:
```env
PORT=8080
DB_HOST=localhost
DB_USER=your_username
DB_PASSWORD=your_password
DB_PORT=5432
DB_NAME=database_name
```

In the application.properties file, set the profile to dev-local for local execution:
```
spring.profiles.active=dev-local
```

**Run the Application**
```
# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```


## **Option 2: Run with Docker**
In the root folder of the project, make sure the `.env` file is set up for Docker as well, then run:
```env
PORT=8080
DB_HOST=app-database
DB_USER=dev
DB_PASSWORD=dev
DB_PORT=5432
DB_NAME=database_name
```
```
# Start the containers
docker compose up -d --build

# View logs
docker logs app-backend -f

# Stop the containers
docker compose down
```


## **Access the Application**

- **API URL:** [http://localhost:8080](http://localhost:8080)

- **Swagger UI:** [http://localhost:8080/swagger](http://localhost:8080/swagger)  
  The Swagger UI provides an interactive interface to explore and test the API endpoints.

  **Credentials for Swagger UI:**
  - **Username:** `dev`
  - **Password:** `dev`


