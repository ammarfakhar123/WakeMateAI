# ⏰ WakeMate AI — Smart Alarm & Personal Productivity Assistant

**WakeMate AI** is a state-of-the-art Android application engineered to solve the chronic morning snooze cycle. It combines a smart alarm clock, interactive AI chat assistant (supporting English, Urdu, and Roman Urdu), and an intelligent to-do reminder system wrapped in an electric dark cyberpunk/neon interface.

---

## 🌟 Key Features

### 1. Smart Alarm System
- **Precision Alarms**: Schedule one-time or recurring alarms for any time of day with custom labels and days of the week.
- **Audio & Vibration Engine**: Utilizes Android's `AudioAttributes.USAGE_ALARM` to guarantee audible alarms even when the phone is on silent or vibrate.
- **Lock-Screen Fullscreen Intent**: Wakes the display and shows over the lock screen (`setShowWhenLocked(true)`, `setTurnScreenOn(true)`).
- **Snooze & Stop Controls**: Custom snooze intervals and graceful stop triggers.

### 2. 5-Minute Wake-Up Verification
- Exactly **5 minutes** after stopping the initial alarm, WakeMate launches a verification check.
- Delivers a Heads-Up notification: *"Hey! Are you awake? 😴"* with instant actions.
- Tapping opens the in-app assistant chat, initiating a **10-second circular countdown timer**.
- Quick reply buttons: `"Yes, I'm awake ✅"` and `"No, still sleepy 😴"`, plus custom text replies.
- **Verification Cleared**: If confirmed within 10 seconds, the alarm flow is completed and marked successful.
- **Timeout Trigger**: If 10 seconds elapse without response (or if the user reports being sleepy), the **Second Alarm (Charging Challenge)** triggers automatically!

### 3. Second Alarm: Charging Challenge 🔌
- **Intense Wake-Up Alarm**: Faster urgent vibration pattern and escalated siren audio.
- **Plug-In Requirement**: The alarm **cannot** be stopped with normal dismiss or snooze buttons. It continues ringing until Android detects that the phone is plugged into a power source (AC, USB, or Wireless).
- **Hardware Charging Detection**: Listens to system broadcasts (`ACTION_POWER_CONNECTED`, `ACTION_BATTERY_CHANGED`) and continuously polls `BatteryManager.isCharging`.
- **System Safety & OS Compliance**: Complies with Android system controls and includes an Emergency Override safeguard.

### 4. Personal Assistant Chat (English, Urdu & Roman Urdu)
- **Natural Language Task Creation**:
  - Roman Urdu: *"Mujhe 6 ghante baad study yaad dilana"* ➔ Schedules reminder for 6 hours later and confirms: *"Done! Main tumhe 6 ghante baad study ka reminder dunga. 📚"*
  - English: *"Remind me to exercise in 2 hours"* ➔ Parses duration, creates task, schedules notification.
  - Urdu: *"مجھے 4 گھنٹے بعد مطالعہ یاد دلانا"*
- **Task Summaries & Motivation**: Ask *"Show pending tasks"* or *"What's on my schedule?"*.
- **Local Chat History**: Stored securely in Room database.
- **Offline NLP Rule Engine**: Fully functional offline; supports optional Google Gemini AI API integration for open-ended conversation.

### 5. To-Do List & Task Manager
- Task creation with:
  - Title & detailed description
  - Due date & time
  - Priority levels (High 🔴, Medium 🟡, Low 🟢)
  - Categories: *Study, Work, Health, Personal, General*
  - Reminder intervals (1h, 3h, 6h, 12h, 24h)
  - Repeat options (None, Daily, Weekly)
- **Automatic 6-Hour Background Reminders**:
  - Periodically reminds users of high-priority pending tasks via `WorkManager`.
  - Notification action button: `"Mark Done ✅"` allows completing tasks without opening the app.
  - Device reboot persistence (`BootReceiver`).

### 6. Smart Daily Dashboard
- Digital neon clock and real-time date display.
- Next scheduled alarm preview with relative countdown (*"In 6h 23m"*).
- Daily progress percentage ring: `(Completed Tasks / Total Tasks) * 100%`.
- Pending tasks preview with quick check-off.
- Motivational AI card adapting dynamically to time of day and workload.

---

## 🛠️ Architecture & Tech Stack

- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture
- **Language**: Kotlin 1.9.24
- **UI Toolkit**: Jetpack Compose with Material 3 components
- **Local Database**: Room 2.6.1 with Coroutines Flow & TypeConverters
- **Preferences**: Jetpack DataStore Preferences
- **Alarm Scheduling**: `AlarmManager.setExactAndAllowWhileIdle()`
- **Background Tasks**: AndroidX `WorkManager` (PeriodicWorkRequest)
- **Power Monitoring**: `BatteryManager` & BroadcastReceivers
- **Audio & Haptics**: `MediaPlayer` with `USAGE_ALARM` & `VibrationEffect`
- **Network & AI**: OkHttp 4.12 + Gson (for optional Google Gemini API)

---

## 📱 Android OS Restrictions & Implementation Notes

Android enforces strict power management and background execution boundaries. Here is how WakeMate AI reliably handles each platform restriction:

### 1. Exact Alarm Permissions (Android 12, 13, 14, 15)
- Android 12+ (API 31+) restricts background exact alarm firing unless `SCHEDULE_EXACT_ALARM` or `USE_EXACT_ALARM` is granted.
- WakeMate AI checks `alarmManager.canScheduleExactAlarms()`. If disabled, it directs users directly to `Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM` from the in-app Settings screen.
- Uses `setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ...)` to fire through Android Doze mode.

### 2. Foreground Services (Android 14+ API 34 & Android 15 API 35)
- Android 14 mandates strict `foregroundServiceType` declarations.
- `AlarmService` and `ChargingChallengeService` specify `android:foregroundServiceType="specialUse|mediaPlayback"`, with associated manifest property tags for runtime compliance.

### 3. Full-Screen Intent (Lockscreen Wakeup)
- Declares `USE_FULL_SCREEN_INTENT`.
- Ringing activities use `setShowWhenLocked(true)`, `setTurnScreenOn(true)`, and request dismiss keyguard so the alarm interface appears immediately over locked devices.

### 4. Background Timing & 10-Second Response Check
- In Android, non-exact background work cannot be guaranteed down to the sub-second when the device is in deep sleep.
- WakeMate AI implements a dual strategy:
  1. **In-Foreground**: An exact millisecond-precision coroutine countdown animates the `CircularCountdown` widget.
  2. **In-Background**: An exact alarm (`VerificationTimeoutReceiver`) is registered with `AlarmManager` for the exact timeout duration (10 seconds), ensuring the transition to the Charging Challenge fires even if the app process was suspended.

---

## 🚀 How to Build and Run in Android Studio

### Prerequisites
- **Android Studio**: Iguana (2023.2.1), Jellyfish (2023.3.1), Koala (2024.1.1), or Ladybug (2024.2.1)+
- **JDK**: Java 17
- **Android SDK**: Compile SDK 35, Min SDK 26

### Step-by-Step Instructions
1. Open **Android Studio**.
2. Select **File > Open...** and browse to:
   ```
   C:\Users\USER 1\.gemini\antigravity\scratch\WakeMateAI
   ```
3. Allow Gradle to sync dependencies automatically.
4. Connect an Android device (or launch an Android Emulator running API 26 to 35).
5. Click **Run > Run 'app'** (`Shift + F10`).

---

## 🧪 Testing the Complete Workflow

1. **Test Standard Alarm**:
   - Go to the **Alarms** tab and tap the Play button (`▶`) on any alarm card.
   - The fullscreen alarm screen will launch with audio and vibration.
   - Tap **Stop Alarm**.
2. **Test 5-Minute Wake-Up Check**:
   - In Settings, set "Wake-Up Verification Delay" to `1m (Test)` for fast verification.
   - Stop an alarm and wait 1 minute.
   - You will receive the notification: *"Hey! Are you awake? 😴"*.
   - Tap the notification or open the Chat tab: the 10-second countdown will start!
3. **Test the Charging Challenge**:
   - Let the 10-second timer expire (or tap *"Test Challenge"* on the Alarms screen).
   - The phone will ring urgently with the message: *"Wake up! Connect your phone to the charger to stop this alarm! 🔌"*.
   - Connect your USB charging cable or place the phone on a wireless charging pad.
   - Within 1 second, WakeMate detects the charger, displays *"Charger Connected! You are awake! ⚡"*, stops the audio, and dismisses the alarm!
4. **Test Multilingual Chat**:
   - Open the **Chat** tab and type:
     - `"Mujhe 6 ghante baad study yaad dilana"`
     - `"Remind me to exercise in 2 hours"`
     - `"Show pending tasks"`
   - Watch the assistant parse the duration, schedule the task in the database, and confirm in Roman Urdu or English!
