#!/usr/bin/env bash
# deploy/cloud/deploy.sh — the cloud-rollout seam for mbdemo65-mock-backend.
#
# This is intentionally a thin, swappable stub so the full develop -> qa ->
# uat -> prod flow is demonstrable without standing up real cloud infra.
# Replace the body with your target's rollout, e.g.:
#   - VM + compose:   ssh "$HOST" "docker compose pull && docker compose up -d"
#   - Cloud Run:      gcloud run deploy mbdemo65-mock-backend --image "$IMAGE" ...
#   - ECS:            aws ecs update-service --force-new-deployment ...
#   - Kubernetes:     kubectl set image deploy/mbdemo65-mock-backend app="$IMAGE"
set -euo pipefail

ENVIRONMENT="${1:?usage: deploy.sh <environment> <image-ref>}"
IMAGE="${2:?usage: deploy.sh <environment> <image-ref>}"

echo "[deploy] mbdemo65-mock-backend: would roll ${IMAGE} to '${ENVIRONMENT}'"
echo "[deploy] stub — replace deploy/cloud/deploy.sh with your real cloud rollout."
