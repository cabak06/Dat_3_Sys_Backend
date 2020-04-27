[![Build Status](https://travis-ci.com/PetersenAndreas/CA3-Gruppe3.svg?branch=master)](https://travis-ci.com/PetersenAndreas/CA3-Gruppe3)

# Group 3 - CA3
*This project is the startcode written by Group 3 for the CA3-assignment*

### Preconditions
*In order to use this code, you should have a local developer setup + a "matching" droplet on Digital Ocean as described in the 3. semester guidelines* 

### Initial Setup
 - Clone the project `git clone git@github.com:PetersenAndreas/CA3-Gruppe3.git`
 - Delete the .git folder in the project
 - Make your own Repository for the project (git init, make repository on github, and follow github instructions to push this project up)
 - Create two local databases, that will be used for development (replace following startcode with the name for your database)
   - `startcode`
   - `startcode_test` (for exercise projects, you can keep this name for your test database)
   *As a default, the name for the db reference in the code is **startcode***
 - Create a remote database with a name matching your above database name (The one you replaced startcode with)
 - **If you use another name for *startcode*_test:** Locate the file **.travis.yml** in the root, and change the script `CREATE DATABASE startcode_test;` to use the SAME name as your local test-database.
 - Inside the resource folder, open the config.properties file, and change the names of the databases to match the names of your databases
 - Inside the resource folder, open the SharedSecret.properties file, and change the value of the sharedSecret variable ( *Must be 32 characters* )
 - Using your terminal in the project folder, use the command `mvn test` to see if everything has been set up correctly
 
 ### Customise your project
 - Delete/Refactor the following, as you need for your project:
   - RenameMe.java in the entity folder *(if you refactor, rememeber to change the query and tests)*
   - Facade.java in the facades folder
   - RenameMeResource.java *(Remeber the change the name for the database to match the name of your own database)*
   - FacadeExampleTest.java in the test packages, in the facades folder. *(Remeber to change the name for the database to match the name of your own database)*
   - The DTO classes and the corresponding method in the DemoResources.java class in the rest package.
 - After all your changes, clean and build the project, to make sure it has been done correctly

### Deploy the project (manually) via Maven
*This step is needed to make sure the droplet will accept travis deploying the project on your droplet*
- Open the pom-file, and locate the properties-section in the start of the file. Change the value for `remote.server` to the URL for your OWN droplet
- ssh into your droplet and open this file with the command: `nano /opt/tomcat/bin/setenv.sh`
- add this to the file, using your own values (those you selected when you did the setup of your droplet,  [here](https://docs.google.com/document/d/1POXowHvFNSTL6C-QOlivkSnL_iF1ogsLGFRTckbBdt8/edit#heading=h.11opjunivufy))
( *Some of this might have been added already, if you have completed the "getting stated guide"* ):

`export DEPLOYED="DEV_ON_DIGITAL_OCEAN"`
`export USER="YOUR_DB_USER"`
`export PW="YOUR_DB_PASSWORD"`
`export CONNECTION_STR="jdbc:mysql://localhost:3306/NAME_OF_YOUR_DB"`

- Save the file, and restart Tomcat `sudo systemctl restart tomcat`
- Back in a LOCAL terminal, in the root of the project, type the following ***replacing the password with your own for the script_user on your droplet***:
  `mvn clean test -Dremote.user=script_user -Dremote.password=PW_FOR_script_user tomcat7:deploy`
- If everything was fine the project should be deployed to your droplet, ready to use with the remote database. Verify via some of the GET-endpoints you have added to your code

### ADD CI-control to your project and let Maven deploy, when the project BUILDS and all tests are GREEN
- Login to Travis using Github, and select your project on the dashboard
- Click "More options" and select "settings"
- Create two Environment Variables with names and values as sketched below (must be done in two steps);
   - **REMOTE_PW :** *Your value for the script_user password, from droplet setup*
   - **REMOTE_USER : *script_user***
 - Now make a small change to index.html (one that is easy to see after deploy)
 - In a terminal, in the root of the project, type: `mvn clean test` (**always build and test before you commit**)
 - If everything was fine, commit and push your changes
 - On *travis-ci.org* verify that your commit has been detected and a "build cycle" has started
 - If everything was fine (might take a few minutes) verify that Travis has deployed your new war file to your droplet
 - Remember to change the travis "build passing" emblem/sticker to match you own build log.
   - In your travis build click the emblem select "markdown", copy the result, insert in the top of your README.md file

### IMPORTENT NOTE ON DUMMY DATA
 *Make sure you read the comment area and follow the instruct, in the SetupTestUsers.java in the utils packages*

# Original startup code from the teachers

As the project is build on the original startcode provided by the teachers, their readme file can be found here: 
 - [Guide for the teachers original startcode](README_teacher_startcode_guide.md)

