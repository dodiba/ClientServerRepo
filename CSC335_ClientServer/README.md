# Client/Server Application

A Java-based client/server application with user authentication, database storage, and email functionality.

## Features

- User registration and authentication
- Password recovery via email
- Multi-threaded server handling multiple clients
- Admin interface for monitoring
- MySQL database integration
- Gmail SMTP email integration

## Prerequisites

- Java 11 or higher
- MySQL Server
- Maven
- Gmail account (for email functionality)

## Setup

1. **Database Setup**
   - Install MySQL Server
   - Create a new database using the schema in `server/database/schema.sql`
   - Update database credentials in `DatabaseManager.java`

2. **Email Configuration**
   - Update Gmail credentials in `EmailUtil.java`
   - For Gmail, you'll need to:
     - Enable 2-factor authentication
     - Generate an App Password
     - Use the App Password in the `EmailUtil.java` file

3. **Build the Project**
   ```bash
   mvn clean package
   ```

## Running the Application

1. **Start the Server**
   ```bash
   java -jar target/client-server-baseline-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

2. **Start the Client**
   ```bash
   java -cp target/client-server-baseline-1.0-SNAPSHOT-jar-with-dependencies.jar client.Client
   ```

## Project Structure

```
client-server-baseline/
├── client/
│   ├── src/
│   │   ├── client/
│   │   │   ├── Client.java
│   │   │   └── gui/
│   │   │       └── ClientGUI.java
│   │   └── common/
│   │       ├── NetworkMessage.java
│   │       └── User.java
├── server/
│   ├── src/
│   │   ├── server/
│   │   │   └── Server.java
│   │   ├── database/
│   │   │   └── DatabaseManager.java
│   │   └── util/
│   │       └── EmailUtil.java
├── pom.xml
└── README.md
```

## Security Notes

- Passwords are stored in plain text for demonstration purposes. In a production environment, use proper password hashing.
- The application uses Gmail's SMTP server for email functionality. Make sure to use App Passwords for security.
- The server listens on port 5000 by default. Ensure this port is available and not blocked by firewalls.

## Troubleshooting

1. **Connection Issues**
   - Verify the server is running
   - Check if the port is not blocked
   - Ensure correct server address is entered

2. **Database Issues**
   - Verify MySQL server is running
   - Check database credentials
   - Ensure the schema is properly created

3. **Email Issues**
   - Verify Gmail credentials
   - Check if 2-factor authentication is enabled
   - Ensure App Password is correctly set

## License

This project is licensed under the MIT License - see the LICENSE file for details. 