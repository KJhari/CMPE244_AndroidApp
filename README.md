## IoT Curtain Control Android App
This README provides a comprehensive guide to setting up and running the IoT Curtain Control Android App. This application allows users to control a motorized curtain through an Android device. The app communicates with a backend server running on a Jetson Nano, which is in turn connected to the motor controlling the curtain.

# Prerequisites
Before you begin, ensure you have the following installed:

# Android Studio
An Android device or emulator running Android 5.0 (Lollipop) or higher
A Jetson Nano setup with Uvicorn and FastAPI, connected to a motor for controlling the curtain

# Libraries Used
The app uses several libraries and frameworks:

Android Jetpack Compose: Used for building the UI in a declarative way.
Android Media3: For video playback functionality in the app.
Volley: For making network requests to the backend server.
AndroidX: Provides backward-compatible versions of Android framework APIs and additional libraries.
GSON: For handling JSON serialization and deserialization.

# Setting Up
Backend Server
Set up a Uvicorn and FastAPI server on a Jetson Nano.
Ensure the server is capable of receiving HTTP requests and controlling the motor based on these requests.

# Android App
Clone the Repository: Clone or download the project repository to your local machine.

Open the Project: Open Android Studio and import the project.

# Configure IP Address:

In MainActivity.kt, replace JETSON_URL with the IP address and port of your Jetson Nano server.
In ChatGptSupportActivity.kt, replace GPT_URL with the appropriate URL for GPT support.
Build the Project: Use Android Studio’s build system to compile the project.

# Run the App: Deploy the app to an Android device or emulator.

# Usage
Once the app is running, you will see a user interface with buttons to control the curtain (Open, Close, Half Open) and a Support button that connects to a GPT-based support system. Here's how to use these features:

Open/Close/Half Open: These buttons send commands to the Jetson Nano to control the motor's position, simulating the opening, closing, or half-opening of a curtain.

# Support: This button leads to a chat interface where users can send prompts and receive responses from a GPT-based support system.

Video Feedback: The app shows a video representing the curtain’s current state, which changes based on the motor's position.

# Troubleshooting
If you encounter issues:

Ensure the Jetson Nano server is running and accessible from your Android device.
Check the IP address and port configurations.
Ensure your Android device and Jetson Nano are on the same network if necessary.
Contributing
Contributions to the project are welcome. Please follow standard git practices for contributing, such as fork the repo, create a feature branch, and submit pull requests.
