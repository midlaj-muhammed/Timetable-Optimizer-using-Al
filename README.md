# Timetable Optimizer AI

A comprehensive Android application that uses artificial intelligence and constraint solving algorithms to create optimal class schedules and study timetables.

## Features

### 🤖 AI-Powered Optimization
- **Constraint Satisfaction Problem (CSP) Solver**: Uses Choco Solver for advanced constraint solving
- **Machine Learning Integration**: TensorFlow Lite for preference learning and intelligent suggestions
- **Smart Scheduling**: Considers user preferences, subject difficulty, and energy levels

### 📱 Modern Android UI
- **Apple-Inspired Design**: Clean, minimalist interface with smooth animations
- **Material Design 3**: Modern components with custom Apple-style aesthetics
- **Responsive Layout**: Works seamlessly across different screen sizes
- **Dark/Light Theme**: System-aware theme switching

### 📊 Comprehensive Timetable Management
- **Multiple Timetable Types**: Study schedules, class schedules, exam schedules
- **Interactive Grid View**: Visual weekly and daily timetable displays
- **Real-time Optimization**: Live constraint validation and scoring
- **Conflict Detection**: Automatic detection and resolution of scheduling conflicts

### 🎯 Smart Subject Management
- **Priority Levels**: Critical, High, Medium, Low priority classification
- **Difficulty Assessment**: Easy to Very Hard difficulty ratings
- **Time Estimation**: Estimated hours per week for each subject
- **Color Coding**: Visual organization with customizable colors

### ⚙️ Advanced Preferences
- **Study Patterns**: Intensive, Balanced, Relaxed, Flexible styles
- **Energy Optimization**: Morning, Afternoon, Evening, Night person settings
- **Time Constraints**: Preferred hours, maximum daily hours, break requirements
- **Workload Balancing**: Even distribution of difficult subjects

### 📤 Export & Sharing
- **PDF Export**: Professional timetable documents
- **Image Export**: PNG format for easy sharing
- **Calendar Integration**: Export to system calendar apps
- **Share Functionality**: Share timetables with others

### 📈 Analytics & Insights
- **Optimization Scores**: Track timetable efficiency
- **Study Hours Tracking**: Monitor weekly study time
- **Performance Metrics**: Subject coverage and time utilization
- **Progress Visualization**: Charts and statistics

## Technical Architecture

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite) for local storage
- **Concurrency**: Kotlin Coroutines and Flow

### AI & Optimization
- **Constraint Solver**: Choco Solver 4.10.10
- **Machine Learning**: TensorFlow Lite 2.13.0
- **Optimization Engine**: Custom CSP implementation
- **Preference Learning**: Rule-based ML model with user feedback

### Key Components
- **TimetableOptimizer**: Core optimization engine using CSP
- **PreferenceLearningModel**: ML model for user preference prediction
- **SchedulingSuggestionEngine**: Intelligent scheduling recommendations
- **TimetableExporter**: PDF, image, and calendar export functionality

## Project Structure

```
app/src/main/java/com/timetableoptimizer/ai/
├── data/
│   ├── entities/          # Room database entities
│   ├── dao/              # Data access objects
│   ├── database/         # Database configuration
│   └── converters/       # Type converters for Room
├── optimization/         # CSP solver and optimization logic
├── ml/                   # Machine learning models and engines
├── export/              # Export and sharing functionality
├── ui/
│   ├── components/      # Reusable UI components
│   ├── screens/         # Screen composables
│   ├── theme/           # Theme and styling
│   └── viewmodels/      # ViewModels for MVVM
└── TimetableOptimizerApplication.kt
```

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 (Android 7.0) or higher
- Kotlin 1.9.10 or later

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/timetable-optimizer-ai.git
   cd timetable-optimizer-ai
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it

3. **Sync Dependencies**
   - Android Studio will automatically sync Gradle dependencies
   - Wait for the sync to complete

4. **Build the Project**
   ```bash
   ./gradlew build
   ```

5. **Run the Application**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - Or use command line: `./gradlew installDebug`

### Building APK

To build a release APK:

```bash
./gradlew assembleRelease
```

The APK will be generated in `app/build/outputs/apk/release/`

## Usage Guide

### Getting Started

1. **First Launch**: Complete the onboarding process to set your preferences
2. **Add Subjects**: Create subjects with priorities, difficulty levels, and time requirements
3. **Set Time Slots**: Configure your available time slots and preferences
4. **Create Timetable**: Generate your first optimized timetable
5. **Optimize**: Use the AI optimizer to improve your schedule

### Creating Optimal Timetables

1. **Subject Configuration**:
   - Set realistic priority levels
   - Estimate weekly hours accurately
   - Choose appropriate difficulty ratings

2. **Time Preferences**:
   - Mark preferred time slots
   - Set energy peak times
   - Configure break requirements

3. **Optimization**:
   - Run the optimizer for best results
   - Review and adjust as needed
   - Export or share your timetable

### Advanced Features

- **Constraint Management**: Add custom constraints for specific requirements
- **Template Creation**: Save timetables as templates for reuse
- **Analytics**: Monitor your scheduling patterns and efficiency
- **Feedback Learning**: The AI learns from your preferences over time

## Testing

### Running Unit Tests
```bash
./gradlew test
```

### Running Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
The project includes comprehensive tests for:
- Database operations (Room DAOs)
- Optimization algorithms
- ML model functionality
- UI components (Compose tests)

## Dependencies

### Core Android
- Kotlin 1.9.10
- Android Gradle Plugin 8.1.2
- Compose BOM 2023.10.01
- Material Design 3

### Database & Storage
- Room 2.6.0
- Kotlin Coroutines 1.7.3

### AI & Optimization
- Choco Solver 4.10.10
- TensorFlow Lite 2.13.0

### Export & Sharing
- iText PDF 7.2.5
- Android FileProvider

### Testing
- JUnit 4.13.2
- Mockito 5.6.0
- Espresso 3.5.1

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- **Choco Solver Team** for the excellent constraint programming library
- **TensorFlow Team** for TensorFlow Lite
- **Android Jetpack Team** for Compose and modern Android development tools
- **Material Design Team** for design guidelines and components

## Support

For support, email support@timetableoptimizer.ai or create an issue in the GitHub repository.

## Roadmap

- [ ] Cloud synchronization
- [ ] Collaborative timetables
- [ ] Advanced ML models
- [ ] Integration with learning management systems
- [ ] iOS version
- [ ] Web application

---

**Timetable Optimizer AI** - Making scheduling intelligent and effortless.
