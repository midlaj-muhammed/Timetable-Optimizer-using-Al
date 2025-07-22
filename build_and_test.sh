#!/bin/bash

# Timetable Optimizer AI - Build and Test Script
# This script builds the project, runs tests, and generates APK

echo "🚀 Starting Timetable Optimizer AI build process..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if gradlew exists
if [ ! -f "./gradlew" ]; then
    print_error "gradlew not found. Make sure you're in the project root directory."
    exit 1
fi

# Make gradlew executable
chmod +x ./gradlew

print_status "Cleaning previous builds..."
./gradlew clean

if [ $? -ne 0 ]; then
    print_error "Clean failed"
    exit 1
fi

print_success "Clean completed"

print_status "Running unit tests..."
./gradlew test

if [ $? -ne 0 ]; then
    print_warning "Some unit tests failed, but continuing with build..."
else
    print_success "Unit tests passed"
fi

print_status "Building debug APK..."
./gradlew assembleDebug

if [ $? -ne 0 ]; then
    print_error "Debug build failed"
    exit 1
fi

print_success "Debug APK built successfully"

print_status "Building release APK..."
./gradlew assembleRelease

if [ $? -ne 0 ]; then
    print_warning "Release build failed, but debug APK is available"
else
    print_success "Release APK built successfully"
fi

print_status "Running lint checks..."
./gradlew lint

if [ $? -ne 0 ]; then
    print_warning "Lint checks found issues, check reports for details"
else
    print_success "Lint checks passed"
fi

# Find and display APK locations
print_status "Locating generated APKs..."

DEBUG_APK="app/build/outputs/apk/debug/app-debug.apk"
RELEASE_APK="app/build/outputs/apk/release/app-release-unsigned.apk"

if [ -f "$DEBUG_APK" ]; then
    print_success "Debug APK: $DEBUG_APK"
    APK_SIZE=$(du -h "$DEBUG_APK" | cut -f1)
    echo "  Size: $APK_SIZE"
fi

if [ -f "$RELEASE_APK" ]; then
    print_success "Release APK: $RELEASE_APK"
    APK_SIZE=$(du -h "$RELEASE_APK" | cut -f1)
    echo "  Size: $APK_SIZE"
fi

print_status "Generating test reports..."
echo "Test reports available at:"
echo "  - Unit tests: app/build/reports/tests/testDebugUnitTest/index.html"
echo "  - Lint report: app/build/reports/lint-results.html"

print_success "Build process completed! 🎉"

echo ""
echo "📱 Installation Instructions:"
echo "1. Enable 'Unknown Sources' in Android Settings > Security"
echo "2. Transfer APK to your Android device"
echo "3. Open the APK file to install"
echo ""
echo "🔧 Development Setup:"
echo "1. Open project in Android Studio"
echo "2. Sync Gradle files"
echo "3. Run on emulator or connected device"
echo ""
echo "📚 Documentation:"
echo "- README.md: Complete setup and usage guide"
echo "- Architecture: MVVM with Jetpack Compose"
echo "- AI Features: CSP optimization + ML preferences"
echo ""

# Optional: Open APK directory
if command -v open &> /dev/null; then
    read -p "Open APK directory? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        open app/build/outputs/apk/
    fi
elif command -v xdg-open &> /dev/null; then
    read -p "Open APK directory? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        xdg-open app/build/outputs/apk/
    fi
fi

print_success "All done! Happy scheduling! 📅✨"
