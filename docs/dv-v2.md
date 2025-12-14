# 📘 Subscriber Challenge Database

This database stores information about users, authentication, projects, and their associated tasks.

---

## Table: `TB_USERS`

Stores user information and authentication details.

| Column          | Type        | Description                                      | Notes            |
|:----------------|:------------|:-------------------------------------------------|:-----------------|
| `user_id`       | `uuid`      | Unique identifier for each user                  | **Primary Key** |
| `full_name`     | `string`    | Full name of the user                            | **Not Null** |
| `email`         | `string`    | User’s email address, used for login and contact | **Unique** |
| `password_hash` | `string`    | BCrypt password hash for authentication          | **Not Null** |
| `role`          | `enum`      | Role assigned to the user (ADMIN, PM, DEV, etc.) |                  |
| `is_verified`   | `boolean`   | Indicates if the user has verified their email   | Default: `false` |
| `create_at`     | `timestamp` | Timestamp when the user account was created      |                  |
| `update_at`     | `timestamp` | Timestamp when the user account was last updated |                  |

---

## Table: `tb_validation_codes`

Stores temporary codes used for email verification and password recovery.

| Column          | Type        | Description                                            | Notes           |
|:----------------|:------------|:-------------------------------------------------------|:----------------|
| `id`            | `uuid`      | Unique identifier for the validation code              | **Primary Key** |
| `code`          | `string`    | The actual numeric code sent to the user               |                 |
| `code_type`     | `enum`      | Type of code (e.g., EMAIL_VERIFICATION, PASSWORD_RESET)|                 |
| `expires_at`    | `timestamp` | When the code becomes invalid                          |                 |
| `confirmed_at`  | `timestamp` | When the code was successfully used                    | Nullable        |
| `create_at`     | `timestamp` | Timestamp when the code was generated                  |                 |
| `user_id`       | `uuid`      | Reference to the user who requested the code           | **Foreign Key** |

---

## Table: `TB_PROJECTS`

Contains information about each project.

| Column        | Type            | Description                                  | Notes           |
|:--------------|:----------------|:---------------------------------------------|:----------------|
| `project_id`  | `uuid`          | Unique identifier for the project            | **Primary Key** |
| `name`        | `string(3-100)` | Project name                                 | **Not Null** |
| `description` | `string`        | Description of the project                   |                 |
| `start_date`  | `timestamp`     | Date and time when the project started       |                 |
| `end_date`    | `timestamp`     | Date and time when the project ends          |                 |

---

## Table: `TB_TASKS`

Contains the tasks associated with each project.

| Column        | Type            | Description                                                  | Notes           |
|:--------------|:----------------|:-------------------------------------------------------------|:----------------|
| `task_id`     | `uuid`          | Unique identifier for the task                               | **Primary Key** |
| `title`       | `string(5-150)` | Title of the task                                            |                 |
| `description` | `string`        | Description of the task                                      |                 |
| `status`      | `enum`          | Indicates the state of the task *(TODO / DOING / DONE)* |                 |
| `priority`    | `enum`          | Indicates the importance of the task *(LOW / MEDIUM / HIGH)* |                 |
| `due_time`    | `timestamp`     | Task deadline date and time                                  |                 |
| `project_id`  | `uuid`          | Reference to the project this task belongs to                | **Foreign Key** |