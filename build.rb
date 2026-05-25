.PHONY: run build-desktop build-android build-deb build-rpm build-appimage cli-install clean help

## Uruchom aplikację desktop
run:
	./gradlew :main:run

## Zbuduj aplikację desktop (JAR)
build-desktop:
	./gradlew :main:packageJar

## Zbuduj .deb
build-deb:
	./gradlew :main:packageDeb

## Zbuduj .rpm (dla LegendaryOS/Fedora)
build-rpm:
	./gradlew :main:packageRpm

## Zbuduj AppImage
build-appimage:
	./gradlew :main:packageAppImage

## Zbuduj APK dla Androida
build-android:
	./gradlew :android:assembleDebug
	@echo ""
	@echo "✓ APK gotowy: android/build/outputs/apk/debug/android-debug.apk"

## Zbuduj release APK
build-android-release:
	./gradlew :android:assembleRelease

## Zainstaluj APK na podłączonym urządzeniu
install-android:
	./gradlew :android:installDebug

## Zainstaluj CLI
cli-install:
	chmod +x cli/legendary
	sudo ln -sf $(CURDIR)/cli/legendary /usr/local/bin/legendary
	@echo "✓ CLI zainstalowane: legendary"

## Usuń artefakty
clean:
	./gradlew clean

## Pomoc
help:
	@echo ""
	@echo "  LegendaryOS App — dostępne polecenia:"
	@echo ""
	@echo "  make run                 Uruchom desktop GUI"
	@echo "  make build-desktop       Zbuduj JAR"
	@echo "  make build-deb           Zbuduj .deb"
	@echo "  make build-rpm           Zbuduj .rpm (LegendaryOS)"
	@echo "  make build-appimage      Zbuduj AppImage"
	@echo "  make build-android       Zbuduj debug APK"
	@echo "  make build-android-release  Zbuduj release APK"
	@echo "  make install-android     Zainstaluj APK na telefonie"
	@echo "  make cli-install         Zainstaluj CLI (legendary)"
	@echo "  make clean               Wyczyść artefakty"
	@echo ""
