# TaskFlow To-Do API

TaskFlow is a REST API for managing personal to-do tasks. Users can register and sign in, then create, view, update, and delete their own tasks. The API is built with Spring Boot and persists users and tasks in PostgreSQL.

## Features

- Account registration and login
- Stateless authentication using signed JWT bearer tokens
- Password hashing with BCrypt
- Create tasks with a name, description, due date, and completion status
- List a user's tasks with pagination and sorting
- Update task names, descriptions, due dates, and completion status
- Delete tasks
- Update the authenticated user's username
- Request validation and ownership checks for task operations

## Technology stack

- **Java 25**
- **Spring Boot 4.1.1**
- **Spring Web MVC** for REST endpoints
- **Spring Security** and **Spring Security OAuth2 Resource Server/JWT**
- **Spring Data JPA** with Hibernate
- **PostgreSQL** and the PostgreSQL JDBC driver
- **Jakarta Validation** for request validation
- **Lombok**
- **Maven 3.9.16** via the included Maven Wrapper
- **JUnit and Spring Boot test dependencies** for automated tests
- Development/testing tools: **IntelliJ IDEA**, **Visual Studio Code**, and **Postman**

## Requirements

Install the following on the computer that will run the application:

1. JDK 25
2. PostgreSQL
3. Git, if cloning the source repository
4. An IDE such as IntelliJ IDEA or Visual Studio Code (optional; the Maven Wrapper can run the app without an IDE)

## Set up and run locally

### 1. Get the source code

Clone the repository, or download and extract the project. Then open a terminal in the project directory (the directory containing `pom.xml` and `mvnw.cmd`).

### 2. Create a PostgreSQL database

Start PostgreSQL and create a database for the application. For example, using `psql`:

```sql
CREATE DATABASE todo_list_app;
```

Make sure the PostgreSQL user you will configure has permission to connect to and modify this database.

### 3. Configure the application

The application uses PostgreSQL on `localhost:5432` by default. Set these environment variables to match your local PostgreSQL installation. The variables override the corresponding application properties:

**Windows PowerShell**

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/todo_list_app"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "your-local-postgres-password"
$env:APP_JWT_SECRET = "replace-with-a-private-random-secret-of-at-least-32-characters"
$env:APP_JWT_EXPIRATION = "PT1H"
```

**macOS/Linux shell**

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/todo_list_app"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="your-local-postgres-password"
export APP_JWT_SECRET="replace-with-a-private-random-secret-of-at-least-32-characters"
export APP_JWT_EXPIRATION="PT1H"
```

`APP_JWT_SECRET` must contain at least 32 UTF-8 bytes. Use a unique, randomly generated value in real deployments. `APP_JWT_EXPIRATION` is an ISO-8601 duration; `PT1H` means one hour. Keep credentials and signing secrets private, and do not commit them to source control.

### 4. Build, test, and run

From the project directory:

**Windows PowerShell**

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

**macOS/Linux**

```bash
./mvnw clean test
./mvnw spring-boot:run
```

The API listens at `http://localhost:8080` by default. In an IDE, import the project as a Maven project and run `com.todolist.v2.ComTodolistV2Application`.

## API

All endpoints use the base URL `http://localhost:8080`. Request and response bodies are JSON. The `/auth/**` endpoints are public; all other endpoints require JWT authentication. Add a request header named `Authorization` and set its value to the `Bearer` scheme, a space, and the JWT returned by login in `response.token`. Tasks and username updates apply to the user identified by the token; clients do not provide a user ID for ownership.

### Authentication

| Method | Endpoint | Authentication | Description |
|---|---|---|---|
| `POST` | `/auth/register` | Public | Register a user |
| `POST` | `/auth/login` | Public | Sign in and receive a JWT |

Register request:

```json
{
  "userName": "alex",
  "role": "USER",
  "account": {
    "email": "alex@example.com",
    "password": "Example123!"
  }
}
```

The role must be a `UserRole` value (`USER` or `ADMIN`). Passwords must be 8–40 non-space characters and include a letter, a number, and one of `@$!%*?&.`.

Login request:

```json
{
  "email": "alex@example.com",
  "password": "Example123!"
}
```

On successful login, the response contains a bearer token, token type, expiration timestamp, and user details. The common API response envelope is:

```json
{
  "response": {},
  "message": "Request completed",
  "successful": true,
  "responseDate": "2030-01-01T12:00:00Z"
}
```

### User

| Method | Endpoint | Authentication | Description |
|---|---|---|---|
| `PUT` | `/user/update/name` | JWT required | Update the authenticated user's username |

Request:

```json
{
  "toApply": "alexander"
}
```

### Tasks

| Method | Endpoint | Authentication | Description |
|---|---|---|---|
| `GET` | `/tasks/your-tasks` | JWT required | Get the authenticated user's paginated, sorted tasks |
| `POST` | `/tasks/create` | JWT required | Create a task |
| `PUT` | `/tasks/update/name/{id}` | JWT required | Update a task name |
| `PUT` | `/tasks/update/description/{id}` | JWT required | Update a task description |
| `PUT` | `/tasks/update/due/{id}` | JWT required | Update a task due date |
| `PUT` | `/tasks/update/mark-complete/{id}` | JWT required | Set a task's completion status |
| `DELETE` | `/tasks/delete` | JWT required | Delete a task by ID |

List tasks request body:

```json
{
  "number": 0,
  "size": 10,
  "property": "createdAt",
  "ascending": false
}
```

`number` is the zero-based page index, `size` is the page size, `property` is the task property to sort by (defaults to `createdAt` when omitted or blank), and `ascending` selects sort direction. This endpoint expects a JSON request body on a `GET`; in Postman, select **Body → raw → JSON** and provide the body above.

Create task request:

```json
{
  "name": "Plan the week",
  "description": "Write down the tasks for this week",
  "dueAt": "2030-01-15T17:00:00Z",
  "completed": false
}
```

The name is required and limited to 50 characters. The description is required and limited to 500 characters. `dueAt` is a required ISO-8601 timestamp and cannot be in the past. `completed` is optional and defaults to `false`.

Each task update endpoint uses a `toApply` field with a value of the appropriate type:

```json
{ "toApply": "Updated task name" }
```

```json
{ "toApply": "Updated task description" }
```

```json
{ "toApply": "2030-01-15T17:00:00Z" }
```

```json
{ "toApply": true }
```

Replace `{id}` in the URL with the task ID. Delete request:

```json
{
  "taskId": 1
}
```

The list endpoint returns a Spring Data page of task responses. A task response currently names its due-date property `duaAt` (as declared by the API model).

## Testing with Postman

1. Start the application and set the Postman base URL to `http://localhost:8080`.
2. Call `POST /auth/register` with a registration body, then call `POST /auth/login`.
3. Copy `response.token` from the login response.
4. For `/user/**` and `/tasks/**`, configure Postman's JWT bearer authorization and paste the token returned by login.
5. Send the documented JSON bodies for task operations. For listing tasks, configure a raw JSON body on the `GET` request.

## Project Link: https://roadmap.sh/projects/todo-list-api
