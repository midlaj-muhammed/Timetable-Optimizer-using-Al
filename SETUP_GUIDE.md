# Timetable Optimizer AI - Setup Guide

## Quick Start

### Prerequisites
- **Android Studio**: Arctic Fox (2020.3.1) or later
- **Android SDK**: API level 24 (Android 7.0) or higher
- **Java**: JDK 8 or later
- **Kotlin**: 1.9.10 (handled by Gradle)

### 1. Project Setup

```bash
# Clone or extract the project
cd "Time Table Optimizer Using AI"

# Make build script executable (Linux/Mac)
chmod +x build_and_test.sh

# Run the build script
./build_and_test.sh
```

### 2. Android Studio Setup

1. **Open Project**:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory

2. **Sync Dependencies**:
   - Wait for Gradle sync to complete
   - If prompted, update Gradle or Android Gradle Plugin

3. **Configure SDK**:
   - Go to File > Project Structure
   - Ensure Android SDK is set to API 24 or higher
   - Set compile SDK to API 34

### 3. Build and Run

#### Option A: Using Android Studio
1. Click the "Sync Project" button
2. Select a device/emulator
3. Click "Run" (green play button)

#### Option B: Using Command Line
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

### 4. Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run all tests
./gradlew check
```

## Project Structure Overview

```
Timetable Optimizer AI/
├── app/
│   ├── src/main/
│   │   ├── java/com/timetableoptimizer/ai/
│   │   │   ├── data/           # Database entities, DAOs
│   │   │   ├── optimization/   # AI optimization engine
│   │   │   ├── ml/            # Machine learning models
│   │   │   ├── export/        # PDF/image export
│   │   │   └── ui/            # Compose UI components
│   │   ├── res/               # Resources (layouts, strings, etc.)
│   │   └── AndroidManifest.xml
│   ├── src/test/              # Unit tests
│   └── build.gradle           # App-level dependencies
├── build.gradle               # Project-level configuration
├── settings.gradle            # Project settings
└── README.md                  # Main documentation
```

## Key Features Implemented

### ✅ Core Functionality
- **AI-Powered Optimization**: CSP solver with Choco Solver
- **Machine Learning**: TensorFlow Lite for preference learning
- **Database**: Room database with comprehensive entities
- **Modern UI**: Jetpack Compose with Material Design 3

### ✅ Advanced Features
- **Export Options**: PDF, image, calendar integration
- **Smart Scheduling**: Priority-based optimization
- **Constraint Management**: Flexible constraint system
- **Analytics**: Performance tracking and insights

### ✅ Technical Implementation
- **Architecture**: MVVM with Repository pattern
- **Concurrency**: Kotlin Coroutines and Flow
- **Testing**: Unit tests and instrumented tests
- **Build System**: Gradle with proper dependency management

## Troubleshooting

### Common Issues

#### 1. Gradle Sync Failed
```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

#### 2. SDK Not Found
- Update `local.properties` with correct SDK path:
```
sdk.dir=/path/to/your/android/sdk
```

#### 3. Font Files Missing
- The app uses system default fonts as fallback
- For Apple-style fonts, add SF Pro fonts to `app/src/main/res/font/`
- See `app/src/main/res/font/fonts_readme.txt` for details

#### 4. Build Errors
- Ensure Android SDK Build Tools are installed
- Update Android Gradle Plugin if needed
- Check `build.gradle` for dependency conflicts

#### 5. App Crashes on Launch
- Check device API level (minimum API 24)
- Verify permissions in AndroidManifest.xml
- Check logcat for detailed error messages

### Performance Optimization

#### For Development
- Enable Gradle daemon: `org.gradle.daemon=true`
- Use parallel builds: `org.gradle.parallel=true`
- Increase heap size: `org.gradle.jvmargs=-Xmx4g`

#### For Production
- Enable R8/ProGuard for release builds
- Optimize images and resources
- Use APK Analyzer to check app size

## Development Workflow

### 1. Making Changes
```bash
# Create feature branch
git checkout -b feature/new-feature

# Make changes
# Test changes
./gradlew test

# Build and verify
./gradlew assembleDebug
```

### 2. Adding New Features
1. **Data Layer**: Add entities, DAOs if needed
2. **Business Logic**: Implement in appropriate packages
3. **UI Layer**: Create Compose screens and components
4. **Testing**: Add unit and integration tests

### 3. Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused

## Deployment

### Debug APK
- Located at: `app/build/outputs/apk/debug/app-debug.apk`
- Suitable for testing and development
- Includes debugging information

### Release APK
- Located at: `app/build/outputs/apk/release/app-release-unsigned.apk`
- Optimized for production
- Requires signing for Play Store distribution

### Signing for Release
1. Generate keystore:
```bash
keytool -genkey -v -keystore my-release-key.keystore -alias alias_name -keyalg RSA -keysize 2048 -validity 10000
```

2. Configure signing in `app/build.gradle`
3. Build signed APK: `./gradlew assembleRelease`

## Next Steps

### Immediate Tasks
1. **Test on Real Device**: Install and test core functionality
2. **Add Sample Data**: Create sample subjects and timetables
3. **UI Polish**: Fine-tune animations and transitions
4. **Performance Testing**: Test with large datasets

### Future Enhancements
1. **Cloud Sync**: Firebase integration for data backup
2. **Collaboration**: Share timetables with others
3. **Advanced ML**: More sophisticated preference learning
4. **Integrations**: LMS and calendar app integrations

## Support

### Getting Help
- Check the main README.md for detailed documentation
- Review code comments and KDoc documentation
- Use Android Studio's built-in debugging tools
- Check logcat for runtime issues

### Contributing
1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Submit a pull request

---

**Happy Coding!** 🚀📱✨

The Timetable Optimizer AI is now ready for development and testing. The foundation is solid, and you can build upon it to create an even more powerful scheduling application.
