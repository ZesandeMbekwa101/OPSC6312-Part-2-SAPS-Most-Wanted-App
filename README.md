 SAPS Most Wanted App

App Overview

The SAPS Most Wanted App is an Android application built using Kotlin that allows users to view and search for the South African Police Service’s most wanted individuals. The app integrates with a free live API to fetch real-time data about wanted suspects and provides a simple authentication system for users to register and log in securely.

Our App Features

User Registration and Login
  Users can create an account or log in using their credentials. Authentication details are stored securely using Firebase Realtime Database.

Live SAPS Most Wanted Data
  The app retrieves and displays information about the most wanted people directly from a public SAPS API.

Search Functionality
  Users can search for suspects by name or crime type.

User Session Storage
  The app remembers the logged-in user using Android’s SharedPreferences for convenience.

User-Friendly Interface
  Simple and intuitive navigation, ensuring ease of use for all users.

---

## Technologies Used

* **Programming Language:** Kotlin
* **IDE:** Android Studio
* **Database:** Firebase Realtime Database (for login & registration)
* **API:** Free SAPS Most Wanted Public API
* **Storage:** SharedPreferences

---

## Installation Instructions

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/SAPSMostWantedApp.git
   ```

2. **Open in Android Studio**

   * Open the cloned project in Android Studio.
   * Wait for Gradle to finish syncing.

3. **Set Up Firebase**

   * Create a Firebase project in the Firebase Console.
   * Add your Android app’s package name.
   * Download and place the `google-services.json` file in the `app/` directory.
   * Enable the Realtime Database and Authentication (if needed).

4. **Configure the API**

   * In the code, locate the section that calls the SAPS Most Wanted API.
   * Replace the API endpoint with your chosen **live API URL** (if not already included).

5. **Build and Run**

   * Connect your Android device or start an emulator.
   * Click **Run** ▶️ to install and launch the app.

---

## How to Use the App

### 1. Registration

* Open the app and tap **Register**.
* Enter your username, password, and any other required details.
* Your details will be securely stored in Firebase.

### 2. Login

* On the login screen, enter your registered username and password.
* Tap **Login** to access the main dashboard.
* If your credentials are correct, you’ll be redirected to the **Home Screen**.

### 3. View Most Wanted List

* After logging in, the app will fetch data from the SAPS API.
* The list of wanted individuals will display:

  * Full name
  * Crime type or case details
  * Photo (if available)

### 4. Search for Suspects

* Use the search bar to find specific individuals by name or type of crime.

### 5. Logout

* To log out, use the logout button found in the menu or top-right corner.

---

## Notes

* Ensure that your device is connected to the internet to fetch live data from the API.
* Firebase access requires proper configuration; incorrect credentials or database paths will prevent login.
* This app is **for educational purposes** and not an official SAPS application.

---

## Future Enhancements

* Add support for push notifications when new wanted persons are added.
* Integrate Google Maps to show the last known locations.
* Include an anonymous tip-off form for reporting sightings.

---

## License

This project is open-source and available under the **MIT License**.

---

**Developer:** [Your Name]
**Language:** Kotlin
**Version:** 1.0
**Last Updated:** October 2025
