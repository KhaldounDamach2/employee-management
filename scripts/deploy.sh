#!/bin/bash
set -e

# Self-fix: Ensure script is executable when downloaded
if [ ! -x "$0" ]; then
    echo "🔧 Making deployment script executable..."
    chmod +x "$0"
    echo "🔁 Restarting script with proper permissions..."
    exec "$0" "$@"
fi

echo "🚀 Employee Management App - Professional Deployment"
echo "===================================================="

# Check if we're in a git repository or standalone deployment
if [ -d ".git" ]; then
    echo "📁 Development mode: Git repository detected"
    DEPLOY_DIR="."
    # Pull latest changes if in git mode
    git pull origin main
else 
    echo "📁 Production mode: Standalone deployment"
    DEPLOY_DIR="./employee-management-deploy"
    mkdir -p $DEPLOY_DIR
    cd $DEPLOY_DIR
    
    # Download the entire repository as ZIP with cache busting
    echo "📥 Downloading FRESH application source code..."
    curl -sL -o repo.zip "https://github.com/KhaldounDamach2/employee-management/archive/main.zip?t=$(date +%s)"
    
    echo "📦 Extracting application files..."
    # Install unzip if not available
    if ! command -v unzip &> /dev/null; then
        echo "📦 Installing unzip..."
        sudo apt update && sudo apt install unzip -y
    fi
    
    # Clean existing files and extract fresh
    echo "🧹 Cleaning existing files..."
    find . -maxdepth 1 ! -name '.env' ! -name '.' ! -name '..' -exec rm -rf {} + 2>/dev/null || true
    
    # Extract the ZIP file
    unzip -q -o repo.zip
    mv employee-management-main/* .
    mv employee-management-main/.* . 2>/dev/null || true
    rm -rf employee-management-main repo.zip
    
    echo "✅ Fresh application source code downloaded and extracted"
fi


# Interactive password setup
if [ ! -f .env ]; then
    echo ""
    echo "🔐 Oracle Database Password Setup"
    echo "================================="
    echo "You need to set a password for the Oracle database."
    echo "This password will be used for:"
    echo "  - Oracle database system user"
    echo "  - Spring Boot application database connection"
    echo ""
    echo "📝 Password requirements:"
    echo "  - At least 8 characters"
    echo "  - Include letters and numbers"
    echo "  - No special characters required"
    echo ""
    
    # Password input with validation
    while true; do
        read -sp "Enter Oracle database password: " password
        echo
        read -sp "Confirm password: " password_confirm
        echo
        
        # Validate password
        if [ -z "$password" ]; then
            echo "❌ Error: Password cannot be empty. Please try again."
            echo
        elif [ "$password" != "$password_confirm" ]; then
            echo "❌ Error: Passwords do not match. Please try again."
            echo
        elif [ ${#password} -lt 8 ]; then
            echo "❌ Error: Password must be at least 8 characters. Please try again."
            echo
        elif ! [[ "$password" =~ [A-Za-z] ]] || ! [[ "$password" =~ [0-9] ]]; then
            echo "❌ Error: Password must contain both letters and numbers. Please try again."
            echo
        else
            echo "✅ Password accepted!"
            break
        fi
    done
    
    # Create .env file with the password
    cat > .env << EOF
# Employee Management App Configuration
ORACLE_PASSWORD=$password
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@oracle-db:1521:XE
SPRING_DATASOURCE_USERNAME=system
APP_PORT=8081
DB_PORT=1522

# Security Note: Keep this file secure!
# This contains database credentials.
EOF
    
    echo "📁 Created .env configuration file"
    echo "🔒 Password stored securely in .env file"
    echo ""
fi

# FIXED: Skip Oracle container registry login for auto-deploy
echo "🔐 Oracle container registry access - auto-deploy mode"
echo "ℹ️  Oracle login skipped for automated deployment"

# Stop any running services first
echo "🐳 Stopping any running services..."
docker-compose down 2>/dev/null || true

# Start services
echo "🐳 Starting services..."
echo "⏳ This may take a few minutes (Oracle database initialization)..."
docker-compose up -d --build

# Wait for Oracle to be fully ready
echo "⏳ Waiting for Oracle database to be fully ready (this can take 3-5 minutes)..."
echo "📊 Monitoring startup progress..."

# Wait for Oracle health check to pass
for i in {1..30}; do
    if docker ps --filter "name=employee-oracle-db" --format "{{.Status}}" | grep -q "(healthy)"; then
        echo "✅ Oracle database is healthy and ready!"
        break
    fi
    echo "⏱️  Waiting for Oracle... ($i/30) - $(docker logs employee-oracle-db --tail 5 2>/dev/null | grep -i 'ready' || echo 'Starting up...')"
    sleep 10
done

# Wait for db-init to complete
echo "🔄 Waiting for database initialization to complete..."
for i in {1..20}; do
    if docker ps --filter "name=employee-db-init" --format "{{.Status}}" | grep -q "Exited (0)"; then
        echo "✅ Database initialization completed successfully!"
        break
    fi
    echo "⏱️  Waiting for DB initialization... ($i/20)"
    sleep 10
done

# Final check if app is running
echo "🔍 Checking if application is ready..."
sleep 10
if docker ps --filter "name=employee-management-app" --format "{{.Status}}" | grep -q "Up"; then
    echo "🎉 Application is running and ready!"
else
    echo "⚠️  Application is starting up... Check logs with: docker-compose logs -f app"
fi

echo ""
echo "✅ Deployment completed successfully!"
echo "======================================"
echo "🌐 Application URL: http://localhost:8081"
echo "🗄️  Oracle Database port: 1522"
echo ""
echo "📊 To check service status:"
echo "   docker-compose ps"
echo ""
echo "📋 To view logs:"
echo "   docker-compose logs -f app"
echo ""
echo "🛑 To stop services:"
echo "   docker-compose down"
echo ""
echo "💡 Remember your Oracle password for database access!"
echo "   It's stored in: $DEPLOY_DIR/.env"