# LegendaryOS App

> Centrum kontroli dla **LegendaryOS**, **HackerOS** i **Android** — wszystko w jednej aplikacji.

![LegendaryOS App](docs/preview.png)

---

## 📁 Struktura projektu

```
LegendaryOS-App/
├── main/               ← Aplikacja desktop (Linux, Kotlin + Compose Multiplatform)
│   └── src/main/kotlin/legendaryos/
│       ├── Main.kt              # Entry point
│       ├── ui/
│       │   ├── LegendaryOSApp.kt   # Root UI + nawigacja
│       │   ├── theme/              # Kolory, motywy
│       │   └── pages/              # Dashboard, Android, HackerOS, System, Files, Terminal, Updates, Settings
│       ├── bridge/
│       │   └── AdbBridge.kt        # Komunikacja z Android przez ADB
│       └── core/
│           └── SystemInfo.kt       # Info o systemie Linux
│
├── android/            ← Aplikacja Android (Kotlin + Jetpack Compose)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/legendaryos/android/
│       │   ├── MainActivity.kt         # Cały UI Android
│       │   └── bridge/
│       │       └── LinuxBridge.kt      # SSH → LegendaryOS/HackerOS
│       └── res/values/themes.xml
│
├── cli/                ← Interfejs wiersza poleceń (Ruby)
│   ├── legendary               # Główny plik wykonywalny
│   └── lib/
│       ├── adb.rb              # ADB helper
│       ├── bootc.rb            # bootc helper
│       ├── ssh_bridge.rb       # SSH do HackerOS
│       └── display.rb          # Formatowanie output
│
├── build.gradle.kts            # Root Gradle
├── settings.gradle.kts
└── README.md
```

---

## 🖥️ Aplikacja desktop (main/)

Zbudowana w **Kotlin + Compose Multiplatform**. Działa natywnie na LegendaryOS (Linux).

### Budowanie

```bash
# Uruchom w trybie deweloperskim
./gradlew :main:run

# Zbuduj .deb / .rpm / .AppImage
./gradlew :main:packageDeb
./gradlew :main:packageRpm
./gradlew :main:packageAppImage
```

### Funkcje

| Moduł | Opis |
|---|---|
| Dashboard | Status systemu, metryki CPU/RAM/Disk, szybkie akcje |
| Android Bridge | ADB, mirror ekranu (scrcpy), transfer plików, schowek, SMS, kamera |
| System | Info o LegendaryOS, bootc status, upgrade/rollback |
| HackerOS | Połączenie SSH, zdalny terminal, SCP, VNC |
| Terminal | Zintegrowana powłoka bash |
| Aktualizacje | bootc upgrade, Flatpak |
| Ustawienia | Konfiguracja aplikacji |

---

## 📱 Aplikacja Android (android/)

Sterowanie komputerem z telefonu — jak **Motorola Smart Connect** ale na Linuxie.

### Budowanie APK

```bash
# Debug APK
./gradlew :android:assembleDebug

# Release APK (wymaga klucza podpisującego)
./gradlew :android:assembleRelease

# Zainstaluj bezpośrednio na podłączonym telefonie
./gradlew :android:installDebug
```

Plik `.apk` pojawi się w:
```
android/build/outputs/apk/debug/android-debug.apk
```

### Funkcje Android

- 📡 Połączenie z LegendaryOS przez WiFi (SSH/TCP)
- 🔒 Blokowanie/usypianie/wyłączanie komputera
- 📋 Synchronizacja schowka Linux ↔ Android
- 📁 Transfer plików
- 💬 SMS z poziomu Linuxa
- 🖥️ HackerOS SSH shell
- 🔔 Powiadomienia Android → Linux

---

## 💻 CLI (cli/)

Napisany w **Ruby 3.x**. Zarządzaj wszystkim z terminala.

### Instalacja

```bash
# Zainstaluj Ruby (jeśli brakuje)
sudo dnf install ruby   # LegendaryOS / Fedora

# Zrób plik wykonywalnym
chmod +x cli/legendary

# Opcjonalnie: dodaj do PATH
sudo ln -sf $(pwd)/cli/legendary /usr/local/bin/legendary
```

### Użycie

```bash
legendary help                          # Lista poleceń
legendary status                        # Status LegendaryOS
legendary upgrade                       # bootc upgrade
legendary rollback                      # bootc rollback
legendary info                          # CPU / RAM / Disk

legendary adb:devices                   # Lista urządzeń Android
legendary adb:connect 192.168.1.5:5555  # Połącz przez WiFi
legendary adb:shell                     # ADB Shell
legendary adb:mirror                    # Mirror ekranu (scrcpy)
legendary adb:sms                       # Ostatnie SMS-y

legendary hackeros:ssh user@192.168.1.20    # SSH do HackerOS
legendary hackeros:status --hackeros-host=192.168.1.20
legendary hackeros:copy plik.txt android:  # SCP

legendary gui                           # Uruchom GUI
```

---

## 🔗 Zależności

### Desktop
- Java 17+, Kotlin 1.9+
- Compose Multiplatform 1.6+
- `adb` (android-tools)
- `scrcpy` (opcjonalnie, mirror ekranu)
- `xclip` lub `wl-clipboard` (synchronizacja schowka)

### Android
- Android 8.0+ (API 26+)
- Uprawnienia: Internet, SMS, Powiadomienia

### CLI
- Ruby 3.0+
- `adb` (android-tools)
- `ssh`, `scp`

---

## 🏗️ Ekosystem

```
┌─────────────────────┐     SSH/SCP    ┌─────────────────┐
│   LegendaryOS App   │◄──────────────►│    HackerOS     │
│   (Desktop GUI)     │                │  (Pentesting)   │
└────────┬────────────┘                └─────────────────┘
         │  ADB / WiFi / USB
         ▼
┌─────────────────────┐
│   Android App       │
│  (Sterowanie z tel) │
└─────────────────────┘
```

---

## 📄 Licencja

GPLv3 — LegendaryOS Project 2026
