# Notes-Management-System

This repository contains a Note Management System, where users can manage notes and update and labels. The system supports different user roles: Admin and user.
## Entities

1. **User**: Represents different user roles - Admin and User.
2. **Note**: Represents notes with various attributes such as title, description etc.
3. **Label**: Represents labels associated with notes.


## Relationships

- User (1) - (n) Note
- Note (n) - (n) Label
- User (1) - (n) Label

## ERD Diagram

An Entity-Relationship Diagram (ERD) visualizing the relationships between entities is provided below:

![ERD NoteManagement](https://github.com/hk5ahi/Notes-Management-System/assets/75085428/7bedf838-20d9-4e8d-a061-3855083f42d1)


## Getting Started

Follow these steps to set up and run the Note Management System:

1. Clone this repository to your local machine.
2. Navigate to the project directory.
3. Open a terminal and run the application using appropriate commands.
