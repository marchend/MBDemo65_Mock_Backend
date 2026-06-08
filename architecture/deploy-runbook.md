# Deployment Runbook — mbdemo65-mock-backend

> Applied by the **MothershipCode CI/CD hardener**. The deploy pipeline is
> config-driven: `.github/workflows/deploy.yml` reads `deploy/deploy.config.yml`
> at run time. Edit that file to retarget an environment — don't hand-edit the
> workflow.

## Where each branch deploys

| Environment | Branch | Target | Auto-deploy | URL |
|---|---|---|---|---|
| `develop` | `develop` | local | yes | http://localhost:8080 |
| `qa` | `qa` | cloud | yes | https://mbdemo65-mock-backend-qa.example.com |
| `uat` | `uat` | cloud | no (approval gate) | https://mbdemo65-mock-backend-uat.example.com |
| `prod` | `main` | cloud | no (approval gate) | https://mbdemo65-mock-backend.example.com |

A merge landing on a branch triggers `deploy.yml`, which resolves the
environment from the table above and deploys on the runner the config names.
Environments with **auto-deploy: no** are gated by GitHub Environment
**required reviewers** — the deploy job pauses in the Actions tab until a human
approves.

## Local targets — register a self-hosted runner

A **local** deploy for `develop` runs on YOUR machine via a
self-hosted runner. One-time setup (start Docker Desktop first):

```bash
# Settings -> Actions -> Runners -> New self-hosted runner, or via gh:
#   gh api repos/<owner>/mbdemo65-mock-backend/actions/runners/registration-token
mkdir actions-runner && cd actions-runner
curl -o runner.tar.gz -L \
  https://github.com/actions/runner/releases/latest/download/actions-runner-osx-arm64.tar.gz
tar xzf runner.tar.gz
./config.sh --url https://github.com/<owner>/mbdemo65-mock-backend \
            --token <REGISTRATION_TOKEN> \
            --labels mbdemo65-mock-backend-dev \
            --name "$(hostname)-mbdemo65-mock-backend"
./run.sh        # or: ./svc.sh install && ./svc.sh start
```

The `mbdemo65-mock-backend-dev` label is
what `runs_on` in the config targets. Deploy to `develop` lands at
**http://localhost:8080**.


### Self-hosted runner safety

- **Private repo only.** `deploy.yml` triggers on `push` to release branches
  (never `pull_request`), so a fork PR can't run on your machine — but keep the
  repo private regardless.
- **Scope the runner to this one repo**, not the org.
- Stop the runner (`./svc.sh stop`) when you're done.

## Cloud targets

Cloud rollout runs through `deploy/cloud/deploy.sh <environment> <image-ref>` —
a swappable stub today (it logs the image it would roll). Drop in your real
rollout (ssh + compose, Cloud Run, ECS, Kubernetes, …). Per-environment secrets
live on the matching GitHub Environment, so the same workflow gets different
secrets per branch with no branching logic.
