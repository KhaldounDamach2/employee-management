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
else 
    echo "📁 Production mode: Standalone deployment"
    DEPLOY_DIR="./employee-management-deploy"
    mkdir -p $DEPLOY_DIR
    cd $DEPLOY_DIR
    
    # Download the entire repository as ZIP
    echo "📥 Downloading complete application source code..."
    curl -sL -o repo.zip https://github.com/KhaldounDamach2/employee-management/archive/main.zip
    
    echo "📦 Extracting application files..."
    # Install unzip if not available
    if ! command -v unzip &> /dev/null; then
        echo "📦 Installing unzip..."
        sudo apt update && sudo apt install unzip -y
    fi
    
    # Extract the ZIP file
    unzip -q repo.zip
    mv employee-management-main/* .
    mv employee-management-main/.* . 2>/dev/null || true
    rm -rf employee-management-main repo.zip
    
    echo "✅ Complete application source code downloaded and extracted"
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

# Login to Oracle Container Registry
echo "🔐 Checking Oracle container registry access..."
docker login container-registry.oracle.com || {
    echo "⚠️  Oracle login skipped or failed - ensure you have access to container-registry.oracle.com"
}

# Start services
echo "🐳 Starting services..."
echo "⏳ This may take a few minutes (Oracle database initialization)..."
docker-compose up -d

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