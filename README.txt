Scheduling Application
-
C195 - Software II Performance Assessment
Author: Damon Heard, dheard12@wgu.edu

Purpose:
-
Application to be used as an appointment and customer management system for a global consulting
organization. Uses the company's MySQL database for data retrieval, modification, and storage.

Application Version: QAM2

IDE: IntelliJ IDEA Community Edition version 2021.1.4.
Java SE Runtime Environment 18.9 (build 11.0.12+8-LTS-237)
JavaFX SDK 11.0.12, JDBC: MySQL Connector J 8.0/mysql-connector-java-8.0.26.jar

Application directions:
-
Username: test
// password: test

Must use valid username/password to login. The database used must have auto-increment set on all ID columns.

After logging in, an alert will notify the user if there are any appointments scheduled to begin in the next
15 minutes. On the main screen, the user can view appointments happening in the next week, month, or all appointments.
Appointments can also be added, deleted, modified from this screen by clicking the respective buttons. Adding and
modified appointments will be checked to ensure no schedule conflicts occur and that appointments take place within
regular business hours. When modifying an appointment, the fields will be pre-populated with the appointment information.
When adding or modifying an appointment, be sure not to leave any fields blank as this will trigger an error message.

Clicking 'View Customers' will bring up the customer table screen where customers can be added, deleted, and modified
by clicking the respective buttons. The Modify Customer fields will be pre-populated with the selected customer's date.
When adding and modifying a customer, be sure to fill in all information or an error message will be triggered.

On the main screen, click 'Reports' to be taken to the Reports screen. Here, the user can view reports on appointments by
type and month, by contact ID, and get an appointment count by appointment location.


