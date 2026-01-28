#!/bin/sh
# ==============================================================================
# Docker Entrypoint Script for LMS Application
# ==============================================================================

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# ==============================================================================
# Wait for database to be ready
# ==============================================================================
wait_for_database() {
    if [ -n "$SPRING_DATASOURCE_URL" ]; then
        # Extract host and port from JDBC URL
        DB_HOST=$(echo "$SPRING_DATASOURCE_URL" | sed -n 's/.*\/\/\([^:\/]*\).*/\1/p')
        DB_PORT=$(echo "$SPRING_DATASOURCE_URL" | sed -n 's/.*:\([0-9]*\)\/.*/\1/p')
        
        if [ -n "$DB_HOST" ] && [ -n "$DB_PORT" ]; then
            log_info "Waiting for database at $DB_HOST:$DB_PORT..."
            
            MAX_RETRIES=${DB_WAIT_RETRIES:-30}
            RETRY_INTERVAL=${DB_WAIT_INTERVAL:-2}
            
            for i in $(seq 1 $MAX_RETRIES); do
                if nc -z "$DB_HOST" "$DB_PORT" 2>/dev/null; then
                    log_info "Database is ready!"
                    return 0
                fi
                log_warn "Database not ready (attempt $i/$MAX_RETRIES). Waiting ${RETRY_INTERVAL}s..."
                sleep $RETRY_INTERVAL
            done
            
            log_error "Database connection timeout after $MAX_RETRIES attempts"
            return 1
        fi
    fi
    
    log_warn "No database URL configured, skipping database wait"
    return 0
}

# ==============================================================================
# Handle graceful shutdown
# ==============================================================================
graceful_shutdown() {
    log_info "Received shutdown signal, gracefully stopping..."
    if [ -n "$APP_PID" ]; then
        kill -TERM "$APP_PID" 2>/dev/null
        wait "$APP_PID" 2>/dev/null
    fi
    log_info "Application stopped gracefully"
    exit 0
}

trap graceful_shutdown SIGTERM SIGINT

# ==============================================================================
# Main execution
# ==============================================================================
main() {
    log_info "=============================================="
    log_info "LMS Application Starting"
    log_info "=============================================="
    log_info "Java Version: $(java -version 2>&1 | head -n 1)"
    log_info "Spring Profile: ${SPRING_PROFILES_ACTIVE:-default}"
    log_info "Timezone: ${TZ:-UTC}"
    log_info "=============================================="
    
    # Wait for database if configured
    if [ "${WAIT_FOR_DB:-true}" = "true" ]; then
        wait_for_database || exit 1
    fi
    
    # Build the Java command
    JAVA_CMD="java"
    
    # Add JAVA_OPTS if set
    if [ -n "$JAVA_OPTS" ]; then
        JAVA_CMD="$JAVA_CMD $JAVA_OPTS"
    fi
    
    # Add application arguments
    JAVA_CMD="$JAVA_CMD -jar /app/app.jar"
    
    # Add any additional arguments passed to the script
    if [ $# -gt 0 ]; then
        JAVA_CMD="$JAVA_CMD $@"
    fi
    
    log_info "Starting application with command:"
    log_info "$JAVA_CMD"
    log_info "=============================================="
    
    # Execute the Java application
    exec $JAVA_CMD &
    APP_PID=$!
    
    # Wait for the application to finish
    wait $APP_PID
}

# Run main function with all arguments
main "$@"
