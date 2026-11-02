# Mini Accounting App

Tiny Flask + SQLite app to track income and expenses.

## Run locally
```bash
python -m venv .venv && source .venv/bin/activate
pip install -r requirements-dev.txt
python run.py            # http://localhost:5000
pytest                   # run tests
flake8 .                 # lint
```

## Run with Docker
```bash
docker compose up --build
```

## API
| Method | Path | Purpose |
|---|---|---|
| GET | `/health` | health check |
| GET | `/transactions` | list all |
| POST | `/transactions` | `{"description","amount","type":"income\|expense"}` |
| DELETE | `/transactions/<id>` | delete one |
| GET | `/summary` | income, expense, balance |

## Commit to GitHub
1. Create an **empty** repo on github.com (no README/license), e.g. `mini-accounting`.
2. In this folder:
```bash
git init -b main
git add .
git commit -m "Initial commit: mini accounting app with CI pipeline"
git remote add origin https://github.com/<your-username>/mini-accounting.git
git push -u origin main
```
(If asked for a password, use a Personal Access Token, or set up SSH keys.)

## CI/CD pipeline (already included)
File: `.github/workflows/ci.yml`
- **Every push / PR to main:** lint (flake8) + tests (pytest)
- **Push to main only:** build Docker image and push to GitHub Container Registry (`ghcr.io/<you>/mini-accounting`)
- See results in your repo -> **Actions** tab.

## Practice path (good DevOps portfolio steps)
1. Push and watch the Action run (break a test on purpose, see it fail).
2. Add branch protection: Settings -> Branches -> require the `test` check before merge.
3. Add a `deploy` job: SSH into an AWS EC2 instance and `docker pull` + `docker run`.
4. Then try Terraform for the EC2, and monitoring (CloudWatch).
