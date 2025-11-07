 SAPS Most Wanted App Report
1. Introduction
The SAPS Most Wanted App is an Android mobile application developed using Kotlin in Android Studio. The application allows users to view and search for individuals listed as the most wanted by the South African Police Service (SAPS). It connects to a live public SAPS API to fetch real-time data about wanted suspects and integrates Firebase Realtime Database for secure user registration and authentication. This app was created as an educational project to demonstrate modern Android development practices, API integration, and secure authentication using Firebase.
2. Purpose of the Application
The main purpose of the SAPS Most Wanted App is to improve public safety awareness and make information on wanted suspects easily accessible to the general public. By providing an interactive and real-time platform, the app allows users to search for, view, and stay informed about individuals wanted by SAPS.
In addition to public awareness, the project aims to demonstrate the use of real-time data integration with APIs, implement secure authentication mechanisms using Firebase, and showcase modern Android development principles such as MVVM architecture and SharedPreferences for session management.
3. Design Considerations
•	Simplicity and Accessibility – The user interface was designed to be simple, intuitive, and accessible for all users.
•	Performance Optimization – Efficient data fetching ensures smooth performance on all devices.
•	Security – Firebase Authentication and secure database connections protect user data.
•	Scalability – The app follows the MVVM architecture, allowing for easy updates and maintenance.
•	Offline Awareness – The app alerts users when there is no internet connection.
•	Modern UI/UX Design – Built using Material Design components for a professional user experience.
4. Technologies Used
Component	Technology/Tool
Programming Language	Kotlin
Integrated Development Environment (IDE)	Android Studio
Database	Firebase Realtime Database
API	SAPS Most Wanted Public API
Storage	SharedPreferences
Architecture	MVVM (Model-View-ViewModel)
5. Application Features
•	User Registration and Login: Secure registration and login using Firebase Realtime Database.
•	Live SAPS Most Wanted Data: Retrieves live data directly from the SAPS API and displays suspect details.
•	Search Functionality: Users can search for suspects by name or crime category.
•	User Session Storage: Uses Android SharedPreferences to remember logged-in users.
•	User-Friendly Interface: Provides a simple navigation layout following Material Design principles.
6. Installation Instructions
1.	Step 1: Clone the repository using: git clone https://https://github.com/ZesandeMbekwa101/OPSC6312-Part-2-SAPS-Most-Wanted-App/tree/MyWork
2.	Step 2: Open the project in Android Studio and wait for Gradle to finish syncing.
3.	Step 3: Configure Firebase by adding your Android app’s package name and downloading the google-services.json file.
4.	Step 4: Configure the API by verifying the API URL in the code.
5.	Step 5: Build and run the app on a connected device or emulator.
7. How to Use the Application
•	Registration – Tap Register, enter credentials, and save them securely in Firebase.
•	Login – Enter registered credentials to access the dashboard.
•	View Most Wanted List – The app fetches live data from the SAPS API and displays details.
•	Search for Suspects – Use the search bar to filter suspects by name or crime type.
•	Logout – Tap Logout from the menu to securely end your session.
8. Release Notes
Version 1.0.0 — Initial Release (November 2025)
•	Firebase Authentication System – Provides real-time, secure user registration and login.
•	Live SAPS API Integration – Enables real-time updates and ensures current suspect data.
•	Smart Search System – Allows quick search by name or crime type.
•	Session Management – Remembers logged-in users using SharedPreferences.
•	Modern Material Design – Enhances user experience and accessibility.
9. Notes and Limitations
•	The SAPS Most Wanted App is for educational purposes only and is not an official SAPS application.
•	An active internet connection is required to retrieve data from the API.
•	Incorrect Firebase configuration may prevent successful authentication.
10. Conclusion
The SAPS Most Wanted App demonstrates the integration of real-time APIs, secure authentication systems, and modern Android development tools. By focusing on accessibility, performance, and security, the application provides a strong example of how mobile technology can support public safety awareness and innovation in community engagement.



Youtube Link: https://www.youtube.com/watch?v=knhMAZzL33M



SCREENSHOT OF PUSH NOTIFICATIONS WORKING 
<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/8db40192-c3f1-4963-9f0b-44b82ea70c86" />

