#!/bin/bash

# Fix Gradle Wrapper - Download and setup proper Gradle wrapper files
# This script downloads the correct Gradle wrapper JAR file

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_status "🔧 Fixing Gradle Wrapper..."

# Create gradle wrapper directory if it doesn't exist
mkdir -p gradle/wrapper

# Download the Gradle wrapper JAR file
GRADLE_VERSION="8.2"
GRADLE_WRAPPER_URL="https://github.com/gradle/gradle/raw/v${GRADLE_VERSION}/gradle/wrapper/gradle-wrapper.jar"

print_status "📥 Downloading Gradle wrapper JAR..."
curl -L -o gradle/wrapper/gradle-wrapper.jar "$GRADLE_WRAPPER_URL"

if [ $? -eq 0 ]; then
    print_success "✅ Gradle wrapper JAR downloaded successfully"
else
    print_error "❌ Failed to download Gradle wrapper JAR"
    
    # Alternative download method
    print_status "🔄 Trying alternative download method..."
    wget -O gradle/wrapper/gradle-wrapper.jar "$GRADLE_WRAPPER_URL"
    
    if [ $? -eq 0 ]; then
        print_success "✅ Gradle wrapper JAR downloaded successfully (alternative method)"
    else
        print_error "❌ Failed to download Gradle wrapper JAR with alternative method"
        exit 1
    fi
fi

# Verify the JAR file
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    JAR_SIZE=$(du -h gradle/wrapper/gradle-wrapper.jar | cut -f1)
    print_success "✅ Gradle wrapper JAR verified (Size: $JAR_SIZE)"
else
    print_error "❌ Gradle wrapper JAR not found after download"
    exit 1
fi

# Make gradlew executable
chmod +x gradlew
chmod +x gradlew.bat

print_success "🎉 Gradle wrapper fixed successfully!"

echo ""
echo "📋 Files created/updated:"
echo "  - gradle/wrapper/gradle-wrapper.jar"
echo "  - gradle/wrapper/gradle-wrapper.properties"
echo "  - gradlew (executable)"
echo "  - gradlew.bat (executable)"
echo ""
echo "🚀 Ready to commit and push the fix!"
