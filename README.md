# 🌿 Paryavaran-Kavalu

> *Paryavaran-Kavalu* means **"Environment Guardian"** in Kannada.

A native Android civic-tech application that empowers citizens to **geo-tag, photograph, and report illegal waste dumping sites** — and connects them with volunteer cleanup units through a live, color-coded map backed by Firebase.

Built as part of the **MindMatrix VTU Internship Program — Project Title 80**.

---

## 📱 Screenshots

## Application Screenshots

_Add screenshots here after capturing from device_

| Map Screen | Report Screen | Confirmation Screen | Karma Screen |
|---|---|---|---|
| <img width="250" alt="Map Screen" src="https://github.com/user-attachments/assets/edf1b35f-f4f5-41ee-93d1-8da524935377" /> | <img width="250" alt="Report Screen" src="https://github.com/user-attachments/assets/f8732d06-be75-406a-86f6-1fc7f942da2a" /> | <img width="250" alt="Dashboard" src="https://github.com/user-attachments/assets/f7b45a34-07c8-44e7-aa34-b74556134936" /> | <img width="250" alt="Karma Screen" src="https://github.com/user-attachments/assets/938d060c-d8cd-4079-9d09-cd15da3145f3" /> |

| Volunteer Dashboard |
|---|
| <img width="250" alt="Profile Screen" src="https://github.com/user-attachments/assets/05c0fb6e-4084-4734-a02e-3de222d1825b" /> |
---

## 🚩 Problem Statement

Illegal garbage dumping in open spaces — known as **"Waste Blackspots"** — poses serious public health risks across Indian cities and towns. The problem is structurally multi-layered:

- **Reporting Gap** — Citizens who observe waste blackspots have no standardized, frictionless channel to report them with precise location data
- **Coordination Gap** — Volunteer cleanup units operate reactively with no live map of blackspot density or priority
- **Accountability Gap** — No mechanism exists to track whether a reported site has actually been cleaned
- **Engagement Gap** — Without recognition or reward, citizen reporting initiatives lose momentum quickly

Paryavaran-Kavalu directly disrupts this cycle by connecting citizen reporters and volunteer cleanup units through a real-time geo-tagged map.

---

## ✨ Features

### 🗺️ Live Cleanliness Map
- Interactive Google Maps view showing all reported waste blackspots
- **🔴 Red pins** = Pending cleanup
- **🟢 Green pins** = Cleaned
- Filter by All / Pending / Cleaned with live count badges
- Real-time sync — pins update across all devices instantly via Firebase

### 📸 Quick Report Submission
- Tap **+ Report** → choose **Camera** or **Gallery**
- Select waste category: Plastic · Organic · E-Waste · Paper · Glass · Others
- Set severity: 🟢 LOW · 🟡 MEDIUM · 🔴 HIGH
- GPS auto-captured and reverse-geocoded to city/state
- Report synced to Firebase Firestore in background
- Offline-first — report saved locally even without internet

### 🤖 Gemini AI Integration
- Photo analyzed by **Gemini Vision API** for waste severity scoring
- Returns severity level + one-line reasoning
- Graceful fallback when offline or quota exceeded

### 🛡️ Volunteer Dashboard
- Lists all pending reports sorted by severity
- **Mark as Cleaned** button → updates status on Firestore → pin turns green on all devices
- Awards **+25 Eco-Karma** to reporter when their spot is cleaned
- Real-time cleanup count and weekly progress indicator

### 🌱 Eco-Karma Points System
- Gamified reward engine to sustain citizen engagement
- Points persist across sessions via SharedPreferences

| Action | Points |
|---|---|
| Submit any report | +10 |
| HIGH severity report | +15 bonus |
| MEDIUM severity report | +5 bonus |
| First report of the day (streak) | +5 |
| Your reported spot gets cleaned | +25 |
| Every 10 reports milestone | +50 |

### 🏅 Badge System

| Badge | Threshold |
|---|---|
| 🌱 Sapling | 0 pts — everyone starts here |
| 🛡️ Guardian | 100 pts |
| ⚔️ Warrior | 500 pts |
| 🏆 Champion | 1,000 pts |
| 🌟 Legend | 2,500 pts |

---

## 🏗️ Architecture

This app follows **MVVM (Model-View-ViewModel)** architecture with a Repository pattern — ensuring clean separation between UI and business logic.

```
app/
├── data/
│   ├── FirestoreRepository.kt    → Firebase Firestore CRUD + real-time listener
│   ├── KarmaStore.kt             → Karma points state + SharedPreferences persistence
│   └── ReportStore.kt            → Local in-memory report list + karma trigger
│
├── model/
│   ├── Report.kt                 → Core report entity (lat, lng, category, severity, status, docId)
│   ├── KarmaEntry.kt             → Individual karma event log entry
│   └── Badge.kt + BadgeCatalog   → Badge definitions and unlock logic
│
├── ui/
│   ├── navigation/
│   │   └── AppNavigation.kt      → Single Activity, NavHost, Bottom Navigation Bar
│   └── screens/
│       ├── HomeScreen.kt         → Map, pins, filters, Firestore listener
│       ├── ReportScreen.kt       → Photo capture, category, severity, GPS submit
│       ├── DashboardScreen.kt    → Volunteer view, Mark as Cleaned
│       ├── KarmaScreen.kt        → Points, badges, activity history
│       └── ReportViewModel.kt    → Gemini AI analysis, image state
│
└── MainActivity.kt               → Permission handling, KarmaStore.init()
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose |
| **Architecture** | MVVM + Repository Pattern |
| **Navigation** | Jetpack Navigation Component |
| **Maps** | Google Maps SDK for Android v18+ |
| **Location** | FusedLocationProviderClient |
| **Camera** | ActivityResultContracts.TakePicture + FileProvider |
| **Image Loading** | Coil |
| **Cloud Database** | Firebase Firestore |
| **Local State** | Compose mutableStateListOf / mutableStateOf |
| **Persistence** | SharedPreferences (Karma points) |
| **GenAI** | Gemini Vision API (gemini-1.5-flash-8b) |
| **Async** | Kotlin Coroutines |
| **Min SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 35 (Android 15) |

---

## 🔑 API Integrations

| API | Purpose |
|---|---|
| **Google Maps SDK** | Interactive map, markers, camera movement |
| **Geocoding (Reverse)** | Convert GPS coordinates → city/state address |
| **FusedLocationProvider** | Battery-efficient GPS capture on report submit |
| **Firebase Firestore** | Cloud storage for reports, real-time sync across devices |
| **Gemini Vision API** | AI waste severity analysis from photo |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android device or emulator running API 24+
- Google Cloud account (for Maps + Gemini API keys)
- Firebase project

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/ParyavaranKavalu.git
cd ParyavaranKavalu
```

### 2. Configure API Keys

Create or open `local.properties` in the project root and add:

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
MAPS_API_KEY=AIzaSy...yourGoogleMapsKey
GEMINI_API_KEY=AIzaSy...yourGeminiKey
```

> ⚠️ **Never commit `local.properties` to version control.** It is already listed in `.gitignore`.

### 3. Connect Firebase

1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Create a project named `ParyavaranKavalu`
3. Add an Android app with package name `com.example.paryavarankavalu`
4. Download `google-services.json` and place it in the `app/` folder
5. Enable **Cloud Firestore** in test mode

### 4. Enable Google APIs

In [Google Cloud Console](https://console.cloud.google.com):
- Enable **Maps SDK for Android**
- Enable **Geocoding API**
- Restrict your Maps API key to your app package + SHA-1 fingerprint

### 5. Build and Run

```bash
./gradlew assembleDebug
```

Or press **Run ▶** in Android Studio.

---

## 🔒 Security

- All API keys stored in `local.properties` — never hardcoded, never committed
- Keys injected at build time via `BuildConfig` fields in `build.gradle.kts`
- Maps API key restricted to package name + SHA-1 fingerprint in Google Cloud Console
- `network_security_config.xml` enforces HTTPS — no cleartext HTTP traffic
- Firebase Security Rules: public read, authenticated write, only `status` field updatable
- `CAMERA` permission requested at runtime only when user taps camera button
- Location permission requested at `Activity` level before any screen loads

---

## 🗄️ Firestore Data Model

```
reports/                          ← collection
  └── {documentId}/               ← auto-generated
        category:  "Plastic"
        severity:  "HIGH"
        status:    "Pending"      ← "Pending" | "Cleaned"
        lat:       12.2958
        lng:       76.6394
        address:   "📍 Mysuru, Karnataka"
        timestamp: 1715234567890
        imageUri:  "content://..."
        docId:     "{documentId}" ← stored for status updates
```

---

## 📊 App Flow

```
First Launch
    ↓
Permission request (Location + Camera)
    ↓
Map Screen loads → Firestore listener fires → existing pins appear
    ↓
Citizen taps "+ Report"
    ↓
Choose Camera / Gallery → Photo selected
    ↓
Gemini AI analyses photo → severity suggested
    ↓
User selects category + confirms severity
    ↓
GPS captured → Report saved locally + synced to Firestore
    ↓
🔴 Red pin appears on map (all devices)
    ↓
Volunteer opens Dashboard → sees pending report
    ↓
Volunteer taps "Mark as Cleaned"
    ↓
Firestore status → "Cleaned" → 🟢 Green pin on map (all devices)
    ↓
Reporter earns +25 Eco-Karma points
```

---

## ✅ Success Criteria (Project Requirements)

| # | Criteria | Status |
|---|---|---|
| 1 | App captures and displays user's current Latitude/Longitude | ✅ |
| 2 | Map pin color changes based on status (Pending/Cleaned) | ✅ |
| 3 | Photo upload compressed and stored | ✅ |
| 4 | MVVM architecture — zero business logic in Activity/Fragment | ✅ |
| 5 | GenAI integrated for waste severity analysis | ✅ |
| 6 | Firebase Firestore for cloud persistence | ✅ |
| 7 | Real-time sync across devices | ✅ |
| 8 | Eco-Karma gamification system | ✅ |
| 9 | Offline-first report submission | ✅ |
| 10 | Network security — HTTPS enforced | ✅ |

---

## 🌍 Impact Goals

- **Swachh Bharat 2.0** — Leveraging technology to support a garbage-free India
- **Environmental Stewardship** — Encouraging youth to take ownership of their surroundings
- **Public Health** — Reducing disease vectors by enabling faster cleanup of illegal dump sites
- **Civic Engagement** — Turning passive observers into active agents of change

---

## 👨‍💻 Developer

**Lead Developer & Team Leader**
MindMatrix VTU Internship Program — Project Title 80
Android App Development using GenAI

---

## 📄 License

This project is developed as part of an academic internship program.
All rights reserved © 2026 MindMatrix VTU Internship Program.
