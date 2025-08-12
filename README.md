# 🎵 Vintage Records 2.0

_A modern Android app for managing vintage music albums, redesigned with Material 3 and fully tested development tools._

---

## 📘 Overview

**Vintage Records** is a Kotlin-based Android application that allows users to manage and explore a personalized collection of vintage music albums. This final version (v2.0) brings a complete design overhaul, enhanced navigation, embedded multimedia, and full developer tooling support.

---

## ✨ Features

### 📀 Album Management

- Create, read, update, and delete albums.
- Upload and display album cover images using Android’s file picker.
- Support for album details: title, artist, genre, cost, release date, rating, track list.

### 🔍 Search, Sort, Filter

- Search by **album title** or **artist**.
- Sort albums by:
  - Title (A–Z)
  - Price (Low → High / High → Low)
  - Rating
  - Genre
- Filter by **wishlist** or genre.

### ⭐ Wishlist

- Mark/unmark albums as wishlisted.
- Dedicated **Wishlist tab** with persistent state.

### 🎬 Multimedia Integration

- Embedded YouTube video preview using `YouTubePlayerView`.
- Clickable **Visit Band Website** link styled to match app branding.

### 🎶 Tracklist Management

- Add or remove tracks dynamically using a scrollable list.
- Input handled via AlertDialog and displayed with auto-numbering.

### ✅ **Data Validation**
- Prevents duplicate album names.
- Ensures required fields (name, artist, genre, release date, etc.) are filled before saving.

---

## 🎨 Design & UX

### 🌗 Theme Support

- Fully responsive **Light** and **Dark Mode** themes.
- Custom `colors.xml` and `colors-night.xml` implementations.
- Brand consistency using **Material 3 system**.

### 💖 Brand Styling

- Embedded `.otf` font styled as a **hot pink swirly font**.
- Used in toolbars across `AlbumListActivity`, `FavoritesActivity`, and detail views.
- Font and color rendering adjusted dynamically for both themes.

### 🧭 Navigation & Layout

- **Bottom Navigation Bar** for Home, Albums, Favorites.
- Material-themed **Top App Bars** with toolbar actions.
- Adjusted RecyclerView paddings to avoid overlap with nav bar.
- Album detail screen separated into editable and read-only UI paths.

---

## 🛠 Developer Tooling

### ✅ Code Quality

- **Ktlint** integration for automatic Kotlin code formatting and lint checking.
- Ran `./gradlew ktlintCheck` and `./gradlew ktlintFormat` to validate and fix style violations across source, test, and script files.

### 📚 Documentation

- **Dokka** integration to auto-generate KDoc-based documentation in HTML.
- Ensures project is well-documented for maintenance and scaling.
- Ran `./gradlew dokkaHtml` to build latest developer documentation.

### 🧪 Testing

- AlbumMemStoreTest implemented using JUnit4 with @Nested-style test grouping.
- Full CRUD, search, and favorite functionality tested and passing.
- All tests now run successfully.

---

## 🗂 Wireframe
![IMG_0414](https://github.com/user-attachments/assets/c7c9b192-9248-40c7-9dfc-f0a80207f894)

---

[![Watch the video](https://img.youtube.com/vi/F2xqWGbkhjs/maxresdefault.jpg)](https://www.youtube.com/watch?v=gDC4oeotj6I)

