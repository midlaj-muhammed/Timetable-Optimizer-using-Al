FROM openjdk:11-jdk

# Install dependencies
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    lib32stdc++6 \
    lib32z1 \
    && rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_COMPILE_SDK=34
ENV ANDROID_BUILD_TOOLS=34.0.0
ENV ANDROID_SDK_TOOLS=9477386

# Download and install Android SDK
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_TOOLS}_latest.zip -O android-sdk.zip && \
    unzip -q android-sdk.zip -d ${ANDROID_HOME}/cmdline-tools && \
    mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest && \
    rm android-sdk.zip

# Set PATH
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

# Accept licenses and install SDK components
RUN yes | sdkmanager --licenses && \
    sdkmanager "platforms;android-${ANDROID_COMPILE_SDK}" \
               "platform-tools" \
               "build-tools;${ANDROID_BUILD_TOOLS}"

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Make gradlew executable
RUN chmod +x ./gradlew

# Build APKs
RUN ./gradlew assembleDebug assembleRelease

# Create output directory
RUN mkdir -p /output && \
    cp app/build/outputs/apk/debug/app-debug.apk /output/ && \
    cp app/build/outputs/apk/release/app-release-unsigned.apk /output/

CMD ["ls", "-la", "/output"]
