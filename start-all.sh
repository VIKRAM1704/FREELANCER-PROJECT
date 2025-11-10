#!/bin/bash
set -e

# --- STEP 1: Start PostgreSQL and RabbitMQ---
echo "🚀 Starting all services..."
docker stop postgres-freelance rabbitmq
docker start postgres-freelance rabbitmq
sleep 10

# --- STEP 2: Start Config Server ---
echo "🔧 Starting Config Server..."
cd config-server
mvn clean install -DskipTests
nohup mvn spring-boot:run > config-server.log 2>&1 &
cd ..

# Wait for config server to start
echo "Waiting for Config Server..."
sleep 10

# --- STEP 3: Start Eureka Server ---
echo "🌐 Starting Eureka Server..."
cd eureka-server
mvn clean install -DskipTests
nohup mvn spring-boot:run > eureka-server.log 2>&1 &
cd ..
sleep 10

# --- STEP 4: Start Gateway Service ---
echo "Starting Gateway Service..."
cd gateway-service
mvn clean install -DskipTests
nohup mvn spring-boot:run > gateway-service.log 2>&1 &
cd ..
sleep 10

# --- STEP 4: Start Microservices ---
SERVICES=("user-service" "project-service" "freelancer-service" "payment-service" "notification-service")

for i in ${!SERVICES[@]}; do
  echo "🔹 Starting ${SERVICES[$i]} ..."
  cd ${SERVICES[$i]}
  mvn clean install -DskipTests
  nohup mvn spring-boot:run > ../${SERVICES[$i]}.log 2>&1 &
  cd ..
  sleep 5
done

# --- STEP 5: Start Frontend ---
echo "💻 Starting React Frontend..."
cd freelance-nexus-frontend
npm install
nohup npm start > ../frontend.log 2>&1 &
cd ..

echo "✅ All services started!"
echo "Check logs with: tail -f *.log"
