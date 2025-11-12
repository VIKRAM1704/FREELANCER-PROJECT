#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================${NC}"
echo -e "${BLUE}  Service Status Check${NC}"
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

# Function to check service status
check_service() {
    local name=$1
    local port=$2
    local url=$3
    
    printf "%-25s Port: %-5s " "$name" "$port"
    
    if check_port $port; then
        echo -e "${GREEN}✅ Running${NC}"
        
        # Try to get health status if URL provided
        if [ ! -z "$url" ]; then
            if curl -s "$url" > /dev/null 2>&1; then
                echo -e "                                    ${GREEN}   Health check: OK${NC}"
            fi
        fi
    else
        echo -e "${RED}❌ Stopped${NC}"
    fi
}

echo -e "${YELLOW}Backend Services:${NC}"
check_service "Config Server" 8888 "http://localhost:8888/actuator/health"
check_service "Eureka Server" 8761 "http://localhost:8761/actuator/health"
check_service "Gateway Service" 8765 "http://localhost:8765/actuator/health"
check_service "User Service" 8081 "http://localhost:8081/actuator/health"
check_service "Freelancer Service" 8082 "http://localhost:8082/actuator/health"
check_service "Project Service" 8083 "http://localhost:8083/actuator/health"
check_service "Payment Service" 8084 "http://localhost:8084/actuator/health"
check_service "Notification Service" 8085 "http://localhost:8085/actuator/health"

echo ""
echo -e "${YELLOW}Frontend:${NC}"
check_service "React Frontend" 3000 "http://localhost:3000"

echo ""
echo -e "${YELLOW}Infrastructure:${NC}"
check_service "PostgreSQL" 5432
check_service "RabbitMQ" 5672
check_service "RabbitMQ Management" 15672

echo ""
echo -e "${BLUE}================================${NC}"
echo ""