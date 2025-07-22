#!/bin/bash

# Upload Timetable Optimizer AI to GitHub Repository
# This script prepares and uploads all files to your GitHub repository

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

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Repository URL
REPO_URL="https://github.com/midlaj-muhammed/Timetable-Optimizer-using-Al.git"
REPO_NAME="Timetable-Optimizer-using-Al"

print_status "🚀 Preparing Timetable Optimizer AI for GitHub upload..."

# Check if git is installed
if ! command -v git &> /dev/null; then
    print_error "Git is not installed. Please install git first."
    exit 1
fi

# Check if we're in the right directory
if [ ! -f "build.gradle" ]; then
    print_error "build.gradle not found. Please run this script from the project root directory."
    exit 1
fi

# Create a temporary directory for the repository
TEMP_DIR="/tmp/timetable-optimizer-upload"
rm -rf "$TEMP_DIR"
mkdir -p "$TEMP_DIR"

print_status "📥 Cloning repository..."
git clone "$REPO_URL" "$TEMP_DIR"

# Copy all project files to the repository
print_status "📁 Copying project files..."

# Copy main project files
cp -r app/ "$TEMP_DIR/" 2>/dev/null || true
cp -r gradle/ "$TEMP_DIR/" 2>/dev/null || true
cp -r .github/ "$TEMP_DIR/" 2>/dev/null || true

# Copy individual files
FILES_TO_COPY=(
    "build.gradle"
    "settings.gradle"
    "gradle.properties"
    "gradlew"
    "gradlew.bat"
    "local.properties"
    "README.md"
    "SETUP_GUIDE.md"
    "GITHUB_ACTIONS_SETUP.md"
    ".gitignore"
    "Dockerfile"
    ".gitlab-ci.yml"
    "build_and_test.sh"
    "setup_and_build.sh"
)

for file in "${FILES_TO_COPY[@]}"; do
    if [ -f "$file" ]; then
        cp "$file" "$TEMP_DIR/"
        print_success "✅ Copied $file"
    else
        print_warning "⚠️  $file not found, skipping"
    fi
done

# Make scripts executable
chmod +x "$TEMP_DIR/gradlew" 2>/dev/null || true
chmod +x "$TEMP_DIR/build_and_test.sh" 2>/dev/null || true
chmod +x "$TEMP_DIR/setup_and_build.sh" 2>/dev/null || true

# Navigate to repository directory
cd "$TEMP_DIR"

# Configure git (if not already configured)
if [ -z "$(git config user.name)" ]; then
    print_status "⚙️  Configuring git..."
    echo "Please enter your GitHub username:"
    read -r github_username
    echo "Please enter your email:"
    read -r github_email
    
    git config user.name "$github_username"
    git config user.email "$github_email"
fi

# Add all files
print_status "📦 Adding files to git..."
git add .

# Check if there are changes to commit
if git diff --staged --quiet; then
    print_warning "No changes to commit. Repository is already up to date."
else
    # Commit changes
    print_status "💾 Committing changes..."
    git commit -m "🚀 Add complete Timetable Optimizer AI Android project

Features:
- ✅ AI-powered timetable optimization with CSP solver
- ✅ Machine learning for preference learning
- ✅ Modern Jetpack Compose UI with Apple-inspired design
- ✅ Room database for local storage
- ✅ Export to PDF, images, and calendar
- ✅ GitHub Actions workflow for automated APK building
- ✅ Comprehensive testing and documentation

Ready for automated APK building via GitHub Actions!"

    # Push to repository
    print_status "🚀 Pushing to GitHub..."
    git push origin main || git push origin master

    print_success "🎉 Successfully uploaded to GitHub!"
fi

# Clean up
cd - > /dev/null
rm -rf "$TEMP_DIR"

echo ""
echo "🎯 NEXT STEPS:"
echo "=============="
echo ""
echo "1. 🌐 Visit your repository:"
echo "   $REPO_URL"
echo ""
echo "2. 🔄 Check GitHub Actions:"
echo "   - Go to the 'Actions' tab"
echo "   - The build should start automatically"
echo "   - Wait for the green checkmark ✅"
echo ""
echo "3. 📱 Download APK:"
echo "   - Click on the completed workflow run"
echo "   - Scroll to 'Artifacts' section"
echo "   - Download 'TimetableOptimizer-Debug-APK' or 'TimetableOptimizer-Release-APK'"
echo ""
echo "4. 📲 Install on Android:"
echo "   - Transfer APK to your device"
echo "   - Enable 'Install from Unknown Sources'"
echo "   - Install the APK"
echo ""
echo "🔗 Repository: $REPO_URL"
echo "📊 Actions: $REPO_URL/actions"
echo ""
print_success "🚀 Your Timetable Optimizer AI is ready for automated building!"
