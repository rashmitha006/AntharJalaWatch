# AntharJalaWatch

AntharJalaWatch is an Android-based cybercrime awareness and reporting application developed using Kotlin and Android Studio. The application helps users learn about cyber threats, report suspicious activities, and access cyber safety information through a simple mobile interface.

---

## Features

- User-friendly Android interface
- Cybercrime awareness dashboard
- Buyer/User dashboard
- Navigation-based UI
- Firebase-ready architecture
- Kotlin-based Android application
- Modern Material Design components

---

## Technologies Used

- Kotlin
- Android Studio
- XML Layouts
- Android Jetpack Navigation
- Gradle
- Material Design Components

---

## Project Structure

```text
AntharJalaWatch/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/antharjalawatch/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── ...
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── drawable/
│   │   │   │   └── values/
│   │   │   └── AndroidManifest.xml
│   └── build.gradle
│
├── gradle/
├── build.gradle
└── settings.gradle
```

---

## Installation Steps

### 1. Clone Repository

```bash
git clone https://github.com/rashmitha006/AntharJalaWatch.git
```

### 2. Open in Android Studio

- Open Android Studio
- Click **Open**
- Select the cloned project folder

### 3. Sync Gradle

```text
File → Sync Project with Gradle Files
```

### 4. Run Application

- Connect Android device or emulator
- Click **Run ▶**

---

## Gradle Configuration

### app/build.gradle

```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.example.antharjalawatch'
    compileSdk 34

    defaultConfig {
        applicationId "com.example.antharjalawatch"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = '17'
    }

    buildFeatures {
        viewBinding true
    }
}

dependencies {

    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.7'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.7'
}
```

---

## MainActivity Example

```kotlin
package com.example.antharjalawatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.antharjalawatch.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
```

---

## AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:label="AntharJalaWatch"
        android:supportsRtl="true"
        android:theme="@style/Theme.AppCompat.Light.DarkActionBar">

        <activity
            android:name=".MainActivity"
            android:exported="true">

            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>

        </activity>

    </application>

</manifest>
```

---

## Screenshots

Add screenshots here after uploading project images.

```text
screenshots/home.png
screenshots/dashboard.png
```

---

## Future Enhancements

- Firebase Authentication
- Real-time cybercrime reporting
- AI-based fraud detection
- Emergency contact integration
- Admin dashboard
- Cloud database support

---

## Author

**Rashmitha M S**

GitHub: https://github.com/rashmitha006

---

## License

This project is developed for educational and academic purposes.
