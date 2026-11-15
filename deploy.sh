
#!/bin/bash

set -euo pipefail

PROJECT_DIR="$HOME/policy-sim-dev"
DEPLOY_DIR="/var/www/policysim/dist"
CADDY_CONFIG="/etc/caddy/Caddyfile"
CONTAINER="policy_frontend"
CONTAINER_DIST="/app/frontend/dist"

echo "================================"
echo "   PolicySim Frontend Deploy"
echo "================================"

# 1. Start container if necessary
echo "[1/5] Starting frontend container..."

docker start "$CONTAINER" >/dev/null

# 2. Build frontend inside container
echo "[2/5] Building frontend..."

docker exec "$CONTAINER" sh -c \
    "cd /app/frontend && npm run build"

# 3. Verify build
echo "[3/5] Verifying build..."

docker exec "$CONTAINER" test -f "$CONTAINER_DIST/index.html"

# 4. Copy build to host
echo "[4/5] Deploying frontend..."

sudo mkdir -p "$DEPLOY_DIR"

docker cp "$CONTAINER:$CONTAINER_DIST/." "$DEPLOY_DIR/"

sudo chmod -R a+rX "$DEPLOY_DIR"

# 5. Validate and reload Caddy
echo "[5/5] Validating Caddy..."

sudo caddy validate --config "$CADDY_CONFIG"

sudo systemctl reload caddy

echo "================================"
echo " Deployment successful!"
echo "================================"
