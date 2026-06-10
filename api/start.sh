#!/bin/bash
set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"

# Garante que o Java 21 do Homebrew está no PATH
export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"

echo "▶ Iniciando backend (Spring Boot) em http://localhost:8080..."
cd "$ROOT/backend"
chmod +x mvnw
./mvnw spring-boot:run &
BACKEND_PID=$!

echo "▶ Iniciando frontend em http://localhost:3000..."
cd "$ROOT/frontend"
node server.js &
FRONTEND_PID=$!

echo ""
echo "✔ Tudo rodando!"
echo "  Frontend → http://localhost:3000"
echo "  Backend  → http://localhost:8080"
echo "  H2 Console → http://localhost:8080/h2-console"
echo ""
echo "Pressione Ctrl+C para encerrar tudo."

trap "echo ''; echo 'Encerrando...'; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null" INT TERM
wait
