# Mini Accounting App

Tiny Java 17 + Maven app (no external libraries) to track income and expenses.

## Build and run

    mvn clean package
    java -jar target/accounting-app.jar      # http://localhost:5000

## Run with Docker

    docker compose up --build

## API

| Method | Path                 | Purpose                                              |
|--------|----------------------|------------------------------------------------------|
| GET    | `/health`            | health check                                         |
| GET    | `/transactions`      | list all                                             |
| POST   | `/transactions`      | `{"description":"..","amount":10,"type":"income"}`   |
| DELETE | `/transactions/<id>` | delete one                                           |
| GET    | `/summary`           | income, expense, balance                             |

## CI

`Jenkinsfile` runs: checkout, build, test, archive the jar (optional Docker build).
