# P6-WhereAreThey
Udacity Anroid Nanodegree Project 6

### Before building:

Create a project in the google developper console.
Enable the following API:

- Google Cloud Messaging 
- Google Maps Android API

Inte the API Manager, create the following credentials:

- API Key for server (no special parameter)
- API Key for the android application ( package name is ca.nbsoft.whereareyou).
- OAuth 2.0 client IDs for web app
- OAuth 2.0 client IDs for the debug configuration of the android app
- OAuth 2.0 client IDs for the release configuration of the android app

Rename the file gradle.properties.example to gradle.properties.
Open the file and paste the various key generated in the developper. The file conains additionnal infromation.

### Testing with bots

The project contains two flavor: localServer and remoteServer. To test this app without having to send contact request to your friend, you should build the localServer version.

- In android studio, select the backend module and run it.
- Once it is running, go to http://localhost:8080/_ah/api/explorer in your browser.
- Select Services -> whereAreyou API -> whereAreYou.createBots.
- Click on 'Execute without OAuth'

This will create two bots: John Doe and Jane Doe.

Install the application on your phone or emulator. Click on the FAB and select 'Add from email'.
You can add the bots user by entering the following address:
  - john.doe@wherearethey.com
  - jane.doe@wherearethey.com

Bots features:
- The bots will automatically accept your contact request.
- Bots will send a random location when you ask them for their location. They will also echo any message you send.
- Bots will request your location if you send them your location with the following message: "Ask me"



