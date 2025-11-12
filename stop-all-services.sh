#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================${NC}"
echo -e "${BLUE}  Stopping All Services${NC}"
echo -e "${BLUE}================================${NC}"
echo ""

# Ports to check and kill
PORTS=(8888 8761 8765 8081 8082 8083 8084 8085 3000)

# Function to kill process on port
kill_port() {
    local port=$1
    local pid=$(lsof -ti:$port)
    
    if [ -z "$pid" ]; then
        echo -e "${YELLOW}⚠️  No process running on port $port${NC}"
        return 1
    fi
    
    echo -e "${YELLOW}🛑 Stopping process on port $port (PID: $pid)...${NC}"
    kill -15 $pid 2>/dev/null
    
    # Wait for graceful shutdown
    sleep 2
    
    # Force kill if still running
    if kill -0 $pid 2>/dev/null; then
        echo -e "${RED}   Process still running, force killing...${NC}"
        kill -9 $pid 2>/dev/null
    fi
    
    echo -e "${GREEN}✅ Stopped process on port $port${NC}"
    return 0
}

# Stop all services
for port in "${PORTS[@]}"; do
    kill_port $port
done

# Also kill any remaining mvn or npm processes
echo ""
echo -e "${YELLOW}Cleaning up remaining Maven and Node processes...${NC}"

# Kill Maven processes
MVN_PIDS=$(pgrep -f "spring-boot:run")
if [ ! -z "$MVN_PIDS" ]; then
    echo "$MVN_PIDS" | xargs kill -15 2>/dev/null
    sleep 2
    echo "$MVN_PIDS" | xargs kill -9 2>/dev/null
    echo -e "${GREEN}✅ Stopped Maven processes${NC}"
fi

# Kill Node processes
NODE_PIDS=$(pgrep -f "node.*start")
if [ ! -z "$NODE_PIDS" ]; then
    echo "$NODE_PIDS" | xargs kill -15 2>/dev/null
    sleep 2
    echo "$NODE_PIDS" | xargs kill -9 2>/dev/null
    echo -e "${GREEN}✅ Stopped Node processes${NC}"
fi

echo ""
echo -e "${GREEN}✅ All services stopped!${NC}"
echo ""