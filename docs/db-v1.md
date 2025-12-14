# 📘 Subscriber Challenge Database

This database stores information about users, projects, and their associated tasks.

---

## Table: `users`

Stores user information.

| Column       | Type        | Description                                      | Notes           |
|:--------------|:-------------|:-------------------------------------------------|:----------------|
| `user_id`    | `uuid`      | Unique identifier for each user                  | **Primary Key** |
| `full_name`  | `string`    | Full name of the user                            |                 |
| `email`      | `string`    | User’s email address, used for login and contact | **Unique**      |
| `hash`       | `string`    | Password hash for authentication                 |                 |
| `role`       | `string`    | Role assigned to the user (e.g., admin, member)  |                 |
| `created_at` | `timestamp` | Timestamp when the user account was created      |                 |
| `updated_at` | `timestamp` | Timestamp when the user account was last updated |                 |

---

## Table: `project`

Contains information about each project.

| Column        | Type            | Description                                  | Notes           |
|:---------------|:----------------|:---------------------------------------------|:----------------|
| `project_id`  | `uuid`          | Unique identifier for the project            | **Primary Key** |
| `name`        | `string(3-100)` | Project name                                 | **Not Null**    |
| `description` | `string`        | Description of the project                   |                 |
| `start_at`    | `date`          | Date when the project started                |                 |
| `end_at`      | `date`          | Date when the project is expected to finish  |                 |

---

## Table: `task`

Contains the tasks associated with each project.

| Column        | Type             | Description                                                   | Notes           |
|:---------------|:-----------------|:--------------------------------------------------------------|:----------------|
| `task_id`     | `uuid`           | Unique identifier for the task                                | **Primary Key** |
| `title`       | `string(5-150)`  | Title of the task                                             | **Not Null**    |
| `description` | `string`         | Description of the task                                       |                 |
| `status`      | `<br/>enum`           | Indicates the state and progress of the task *(TODO / DOING / DONE)* |                 |
| `priority`    | `enum`           | Indicates the importance of the task *(LOW / MEDIUM / HIGH)*  |                 |
| `due_date`    | `date`           | Task deadline date                                            |                 |