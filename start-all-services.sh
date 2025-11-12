#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project root directory
PROJECT_ROOT=$(pwd)

# Log directory
LOG_DIR="$PROJECT_ROOT/logs"
mkdir -p "$LOG_DIR"

# Service configuration
declare -A SERVICES=(
    ["config-server"]=8888
    ["eureka-server"]=8761
    ["gateway-service"]=8765
    ["user-service"]=8081
    ["project-service"]=8083
    ["freelancer-service"]=8082
    ["payment-service"]=8084
    ["notification-service"]=8085
)

# Frontend configuration
FRONTEND_DIR="freelance-nexus-frontend"
FRONTEND_PORT=3000

echo -e "${BLUE}================================${NC}"
echo -e "${BLUE}  Starting All Services${NC}"
echo -e "${BLUE}================================${NC}"
echo ""

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        return 0
    else
        return 1
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local service=$1
    local port=$2
    local max_wait=120
    local count=0
    
    echo -e "${YELLOW}⏳ Waiting for $service to be ready on port $port...${NC}"
    
    while ! check_port $port; do
        if [ $count -ge $max_wait ]; then
            echo -e "${RED}❌ $service failed to start within $max_wait seconds${NC}"
            return 1
        fi
        sleep 1
        count=$((count + 1))
    done
    
    echo -e "${GREEN}✅ $service is ready (port $port)${NC}"
    return 0
}

# Function to start a Spring Boot service
start_service() {
    local service=$1
    local port=$2
    local log_file="$LOG_DIR/$service.log"
    
    echo ""
    echo -e "${BLUE}📦 Starting $service on port $port...${NC}"
    
    # Check if already running
    if check_port $port; then
        echo -e "${YELLOW}⚠️  $service is already running on port $port${NC}"
        return 0
    fi
    
    # Check if directory exists
    if [ ! -d "$PROJECT_ROOT/$service" ]; then
        echo -e "${RED}❌ Directory $service not found${NC}"
        return 1
    fi
    
    cd "$PROJECT_ROOT/$service"
    
    # Start the service in background
    echo -e "${YELLOW}   Building and starting...${NC}"
    nohup mvn spring-boot:run > "$log_file" 2>&1 &
    local pid=$!
    
    echo -e "${GREEN}   Started with PID: $pid${NC}"
    echo -e "${GREEN}   Log file: $log_file${NC}"
    
    # Return to project root
    cd "$PROJECT_ROOT"
    
    # Wait for service to be ready
    wait_for_service "$service" "$port"
}

# Function to start frontend
start_frontend() {
    local log_file="$LOG_DIR/frontend.log"
    
    echo ""
    echo -e "${BLUE}🎨 Starting Frontend on port $FRONTEND_PORT...${NC}"
    
    # Check if already running
    if check_port $FRONTEND_PORT; then
        echo -e "${YELLOW}⚠️  Frontend is already running on port $FRONTEND_PORT${NC}"
        return 0
    fi
    
    # Check if directory exists
    if [ ! -d "$PROJECT_ROOT/$FRONTEND_DIR" ]; then
        echo -e "${RED}❌ Directory $FRONTEND_DIR not found${NC}"
        return 1
    fi
    
    cd "$PROJECT_ROOT/$FRONTEND_DIR"
    
    # Install dependencies if node_modules doesn't exist
    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}   Installing dependencies...${NC}"
        npm install > "$log_file" 2>&1
    fi
    
    # Start the frontend in background
    echo -e "${YELLOW}   Starting development server...${NC}"
    nohup npm start > "$log_file" 2>&1 &
    local pid=$!
    
    echo -e "${GREEN}   Started with PID: $pid${NC}"
    echo -e "${GREEN}   Log file: $log_file${NC}"
    
    # Return to project root
    cd "$PROJECT_ROOT"
    
    # Wait for frontend to be ready
    wait_for_service "frontend" "$FRONTEND_PORT"
}

# Start services in order
echo -e "${YELLOW}Starting services in dependency order...${NC}"
echo ""

# 1. Config Server (must start first)
start_service "config-server" 8888
sleep 5

# 2. Eureka Server (service registry)
start_service "eureka-server" 8761
sleep 10

# 3. Gateway Service
start_service "gateway-service" 8765
sleep 5

# 4. Microservices (can start in parallel, but we'll do sequential for safety)
start_service "user-service" 8081
sleep 3

start_service "freelancer-service" 8082
sleep 3

start_service "project-service" 8083
sleep 3

start_service "payment-service" 8084
sleep 3

start_service "notification-service" 8085
sleep 3

# 5. Frontend
start_frontend

echo ""
echo -e "${BLUE}================================${NC}"
echo -e "${GREEN}✅ All Services Started!${NC}"
echo -e "${BLUE}================================${NC}"
echo ""
echo -e "${YELLOW}Service URLs:${NC}"
echo -e "  Config Server:       http://localhost:8888"
echo -e "  Eureka Dashboard:    http://localhost:8761"
echo -e "  API Gateway:         http://localhost:8765"
echo -e "  User Service:        http://localhost:8081"
echo -e "  Freelancer Service:  http://localhost:8082"
echo -e "  Project Service:     http://localhost:8083"
echo -e "  Payment Service:     http://localhost:8084"
echo -e "  Notification Service: http://localhost:8085"
echo -e "  Frontend:            http://localhost:3000"
echo ""
echo -e "${YELLOW}Infrastructure:${NC}"
echo -e "  PostgreSQL:          localhost:5432"
echo -e "  RabbitMQ:            localhost:5672"
echo -e "  RabbitMQ Management: http://localhost:15672"
echo ""
echo -e "${BLUE}Log files are in: $LOG_DIR${NC}"
echo ""
echo -e "${YELLOW}To stop all services, run: ./stop-all-services.sh${NC}"
echo ""