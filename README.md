# Notes App

Create, view, edit, search and delete personal notes in a native Android app with a custom RecyclerView adapter and SQLite persistence.

Pynexia Android Development / Week 2.

[Hosted showcase](https://engeyad.github.io/pynexia-week2-notes/) · [PDF report](https://engeyad.github.io/pynexia-week2-notes/downloads/Notes-App-Report.pdf) · [APK](https://engeyad.github.io/pynexia-week2-notes/downloads/Notes-App.apk) · [Submission ZIP](https://engeyad.github.io/pynexia-week2-notes/downloads/Notes-App.zip)

## Implementation

### Lists and models

Entry models, a custom ListAdapter, DiffUtil and ViewHolder render the note list. Stable database IDs keep records distinct even when titles match.

### Local CRUD

SQLiteOpenHelper stores title and full text. A single background executor serializes reads and writes. Viewing opens the complete note; deletion requires confirmation.

### Input and lifecycle

Title: 1-120 trimmed characters. Note body: required, up to 10,000 characters. Search is literal and case-insensitive. Editor drafts and in-flight save state survive Activity recreation.

## Run

Open the repository root in Android Studio. Install SDK 35 and Build Tools 35.0.0, use JDK 17 or 21 and sync Gradle. Run `notes` on Android 8.0/API 26 or newer. AGP 8.9.1; Gradle wrapper 8.11.1. Initial sync needs internet.

```powershell
.\gradlew.bat :notes:assembleDebug :notes:testDebugUnitTest :notes:lintDebug
.\gradlew.bat :notes:connectedDebugAndroidTest
```

The second command requires an emulator/device. On macOS/Linux use `chmod +x gradlew` and `./gradlew`.

## Walkthrough

1. Tap New note. Try saving empty fields, then enter a title and body.
2. Save and select the note to read the full content.
3. Edit the note, search for part of its title or body, and reopen the app.
4. Cancel a deletion to keep the note, then confirm deletion to remove it.

## Verification

4 JVM tests and 3 device tests passed with no failures, errors or skips. Lint: 0 errors and 18 warnings without suppression. Tested 2 November 2026 on Pixel 8 emulator, Android 17/API 37 (37.1 image), x86_64. No physical-device or API 26 run. See `verification/` for results with local paths redacted.

Validation boundaries, literal search, SQLite CRUD and close/reopen persistence, UI add/edit/search/delete, cancellation and editor recreation.

## Scope

Local storage only. No cloud sync or encryption. Clearing app data/uninstalling removes notes. Duplicate titles are allowed; records have distinct IDs.

APKs are debug signed for evaluation. Hosted pages show actual screenshots and downloads, not Android emulation. No Pynexia form is submitted automatically.

## شرح عربي

يتيح تطبيق الملاحظات إضافة عنوان ونص، وعرض الملاحظة كاملة وتعديلها وحذفها مع تأكيد الحذف. تظهر البيانات في قائمة بمحوّل مخصص وتُحفظ في قاعدة بيانات محلية. يدعم البحث في العنوان والنص، ويحافظ على مسودة التحرير عند تدوير الشاشة.

## Submission

Copy `SUBMISSION-NOTES.txt` into the form. Attach the ZIP or PDF. APK is inside the ZIP because the earlier form does not list direct APK uploads. Verify the current Week 2 form accepts the same formats and limits.
