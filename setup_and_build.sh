#!/bin/bash

# Timetable Optimizer AI - Complete Setup and Build Script
# This script sets up Android SDK and builds APK without Android Studio

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# Configuration
ANDROID_HOME="$HOME/Android/Sdk"
JAVA_VERSION="11"
ANDROID_API="34"
BUILD_TOOLS="34.0.0"
CMDLINE_TOOLS_VERSION="9477386"

print_status "🚀 Starting Timetable Optimizer AI setup and build..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    print_status "Installing Java JDK $JAVA_VERSION..."
    
    if command -v apt-get &> /dev/null; then
        sudo apt-get update
        sudo apt-get install -y openjdk-$JAVA_VERSION-jdk
    elif command -v yum &> /dev/null; then
        sudo yum install -y java-$JAVA_VERSION-openjdk-devel
    elif command -v brew &> /dev/null; then
        brew install openjdk@$JAVA_VERSION
    else
        print_error "Please install Java JDK $JAVA_VERSION manually"
        exit 1
    fi
else
    print_success "Java is already installed"
fi

# Verify Java installation
java -version
if [ $? -ne 0 ]; then
    print_error "Java installation failed"
    exit 1
fi

# Create Android SDK directory
print_status "Setting up Android SDK..."
mkdir -p "$ANDROID_HOME"

# Download Android Command Line Tools if not exists
CMDLINE_TOOLS_ZIP="$ANDROID_HOME/commandlinetools.zip"
if [ ! -f "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" ]; then
    print_status "Downloading Android Command Line Tools..."
    
    wget -q "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip" -O "$CMDLINE_TOOLS_ZIP"
    
    if [ $? -ne 0 ]; then
        print_error "Failed to download Android Command Line Tools"
        exit 1
    fi
    
    # Extract command line tools
    cd "$ANDROID_HOME"
    unzip -q "$CMDLINE_TOOLS_ZIP"
    mkdir -p cmdline-tools
    mv cmdline-tools cmdline-tools/latest 2>/dev/null || mv cmdline-tools/* cmdline-tools/latest/
    rm "$CMDLINE_TOOLS_ZIP"
    
    print_success "Android Command Line Tools downloaded and extracted"
else
    print_success "Android Command Line Tools already installed"
fi

# Set environment variables
export ANDROID_HOME="$ANDROID_HOME"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"

# Update local.properties
print_status "Updating local.properties..."
echo "sdk.dir=$ANDROID_HOME" > local.properties

# Accept licenses and install SDK components
print_status "Installing Android SDK components..."
yes | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses > /dev/null 2>&1

"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" \
    "platforms;android-$ANDROID_API" \
    "platform-tools" \
    "build-tools;$BUILD_TOOLS" > /dev/null 2>&1

if [ $? -ne 0 ]; then
    print_error "Failed to install Android SDK components"
    exit 1
fi

print_success "Android SDK setup completed"

# Navigate to project directory
cd "$(dirname "$0")"

# Make gradlew executable
chmod +x gradlew

# Clean previous builds
print_status "Cleaning previous builds..."
./gradlew clean

# Build debug APK
print_status "Building debug APK..."
./gradlew assembleDebug

if [ $? -ne 0 ]; then
    print_error "Debug build failed"
    exit 1
fi

print_success "Debug APK built successfully"

# Build release APK
print_status "Building release APK..."
./gradlew assembleRelease

if [ $? -ne 0 ]; then
    print_warning "Release build failed, but debug APK is available"
else
    print_success "Release APK built successfully"
fi

# Find and display APK locations
print_status "Locating generated APKs..."

DEBUG_APK="app/build/outputs/apk/debug/app-debug.apk"
RELEASE_APK="app/build/outputs/apk/release/app-release-unsigned.apk"

echo ""
echo "📱 BUILD RESULTS:"
echo "=================="

if [ -f "$DEBUG_APK" ]; then
    APK_SIZE=$(du -h "$DEBUG_APK" | cut -f1)
    print_success "✅ Debug APK: $DEBUG_APK (Size: $APK_SIZE)"
    
    # Copy to easy access location
    cp "$DEBUG_APK" "./TimetableOptimizer-debug.apk"
    print_success "   Copied to: ./TimetableOptimizer-debug.apk"
fi

if [ -f "$RELEASE_APK" ]; then
    APK_SIZE=$(du -h "$RELEASE_APK" | cut -f1)
    print_success "✅ Release APK: $RELEASE_APK (Size: $APK_SIZE)"
    
    # Copy to easy access location
    cp "$RELEASE_APK" "./TimetableOptimizer-release.apk"
    print_success "   Copied to: ./TimetableOptimizer-release.apk"
fi

echo ""
echo "🎉 BUILD COMPLETED SUCCESSFULLY!"
echo ""
echo "📋 INSTALLATION INSTRUCTIONS:"
echo "1. Transfer the APK file to your Android device"
echo "2. Enable 'Install from Unknown Sources' in Settings > Security"
echo "3. Open the APK file on your device to install"
echo ""
echo "🔧 APK FILES READY:"
if [ -f "./TimetableOptimizer-debug.apk" ]; then
    echo "   • TimetableOptimizer-debug.apk (for testing)"
fi
if [ -f "./TimetableOptimizer-release.apk" ]; then
    echo "   • TimetableOptimizer-release.apk (optimized)"
fi
echo ""

# Optional: Create installation guide
cat > INSTALL_GUIDE.txt << EOF
TIMETABLE OPTIMIZER AI - INSTALLATION GUIDE
==========================================

APK Files Generated:
$([ -f "./TimetableOptimizer-debug.apk" ] && echo "✅ TimetableOptimizer-debug.apk")
$([ -f "./TimetableOptimizer-release.apk" ] && echo "✅ TimetableOptimizer-release.apk")

Installation Steps:
1. Transfer APK to your Android device (via USB, email, cloud storage)
2. On your Android device:
   - Go to Settings > Security (or Privacy)
   - Enable "Install from Unknown Sources" or "Allow from this source"
3. Open the APK file on your device
4. Tap "Install" when prompted
5. Open the app and enjoy optimal scheduling!

System Requirements:
- Android 7.0 (API 24) or higher
- At least 100MB free storage
- Internet connection for initial setup (optional)

Features:
✨ AI-powered timetable optimization
📅 Smart scheduling with constraint solving
🎨 Beautiful Apple-inspired interface
📊 Analytics and performance tracking
📤 Export to PDF, images, and calendar apps

Support:
If you encounter any issues, check the README.md file for troubleshooting tips.

Happy Scheduling! 🚀
EOF

print_success "Installation guide created: INSTALL_GUIDE.txt"

echo ""
print_success "🎯 All done! Your Timetable Optimizer AI APK is ready to install!"
