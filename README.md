# Ticketing System - Full Stack Application

A comprehensive IT support ticketing system built with Spring Boot and Next.js.

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- PostgreSQL
- Spring Security + JWT
- Maven

### Frontend
- Next.js 14
- React 18
- TypeScript
- Tailwind CSS
- Axios

## Features

### Must-Have Features ✅

1. **Authentication & Authorization**
   - User registration and login
   - JWT-based authentication
   - Role-based access control (User, Support Agent, Admin)
   - Secure password encryption

2. **User Dashboard**
   - Create new tickets with subject, description, and priority
   - View all personal tickets
   - Add comments to tickets
   - Track ticket status (Open, In Progress, Resolved, Closed)
   - View complete ticket history with comments

3. **Ticket Management**
   - Full ticket lifecycle management
   - Ticket reassignment capabilities
   - Comment threads with timestamps and user info
   - Track ticket owner and assignee

4. **Admin Panel**
   - User management (add, edit, delete users)
   - Role assignment (Admin, Support Agent, User)
   - View all tickets across the system
   - Force reassign or resolve/close any ticket
   - Monitor ticket statuses

5. **Access Control**
   - Admins can manage users and override tickets
   - Support agents can manage assigned tickets
   - Users can only manage their own tickets

### Good-to-Have Features ✅

1. **Email Notifications**
   - Ticket creation notifications
   - Assignment notifications
   - Status change notifications

2. **Search & Filter**
   - Search tickets by subject or description
   - Filter by status (Open, In Progress, Resolved, Closed)
   - Filter by priority (Low, Medium, High, Urgent)

3. **Ticket Prioritization**
   - Four priority levels: Low, Medium, High, Urgent
   - Visual priority indicators
   - Sort and filter by priority

4. **File Attachments**
   - Upload files to tickets
   - Secure file storage
   - Display attachment metadata

5. **Rate Ticket Resolution**
   - 5-star rating system
   - Optional feedback for resolved tickets
   - Rating restricted to ticket creators

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- PostgreSQL 12 or higher
- Maven

### Database Setup

1. Install PostgreSQL and create a database:
```sql
CREATE DATABASE ticketing_db;
```

2. Update database credentials in `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ticketing_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Run the development server:
```bash
npm run dev
```

The frontend will start on `http://localhost:3000`

## Default Users

After first run, you can create users through the registration page. To create an admin user:

1. Register a regular user
2. Update the user's role in the database:
```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';
```

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register new user
- POST `/api/auth/login` - Login user

### Tickets
- GET `/api/tickets` - Get user's tickets
- GET `/api/tickets/{id}` - Get ticket details
- POST `/api/tickets` - Create new ticket
- PUT `/api/tickets/{id}/status` - Update ticket status
- PUT `/api/tickets/{id}/assign` - Assign ticket
- POST `/api/tickets/{id}/comments` - Add comment
- POST `/api/tickets/{id}/rate` - Rate ticket
- POST `/api/tickets/{id}/attachments` - Upload attachment
- GET `/api/tickets/search?keyword={keyword}` - Search tickets
- GET `/api/tickets/filter/status?status={status}` - Filter by status
- GET `/api/tickets/filter/priority?priority={priority}` - Filter by priority

### Admin
- GET `/api/admin/users` - Get all users
- GET `/api/admin/users/{id}` - Get user by ID
- POST `/api/admin/users` - Create user
- PUT `/api/admin/users/{id}` - Update user
- DELETE `/api/admin/users/{id}` - Delete user
- GET `/api/admin/tickets` - Get all tickets
- PUT `/api/admin/tickets/{id}/status` - Force update status
- PUT `/api/admin/tickets/{id}/assign` - Force assign ticket

## Usage

### For Users
1. Register/Login to the system
2. Create tickets from the dashboard
3. View and manage your tickets
4. Add comments to track progress
5. Upload attachments as needed
6. Rate tickets after resolution

### For Support Agents
1. Login with support agent credentials
2. View assigned tickets
3. Update ticket status
4. Add comments and solutions
5. Upload relevant files

### For Admins
1. Login with admin credentials
2. Access admin panel from dashboard
3. Manage users (create, edit, delete, assign roles)
4. View all tickets system-wide
5. Override ticket assignments and statuses
6. Monitor system activity

## Project Structure

```
ticketing-system/
├── backend/
│   ├── src/main/java/com/ticketing/
│   │   ├── model/          # Entity classes
│   │   ├── repository/     # JPA repositories
│   │   ├── service/        # Business logic
│   │   ├── controller/     # REST controllers
│   │   ├── security/       # Security configuration
│   │   ├── config/         # Application configuration
│   │   └── dto/            # Data transfer objects
│   └── src/main/resources/
│       └── application.properties
└── frontend/
    ├── app/                # Next.js pages
    │   ├── login/          # Login page
    │   ├── register/       # Registration page
    │   ├── dashboard/      # User dashboard
    │   ├── tickets/        # Ticket pages
    │   └── admin/          # Admin panel
    └── lib/                # Utilities
        └── api.ts          # API client
```

## Security

- Passwords are encrypted using BCrypt
- JWT tokens for stateless authentication
- CORS configured for frontend-backend communication
- Role-based access control on API endpoints
- File upload size limits enforced

## Email Configuration

To enable email notifications, update `application.properties`:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

For Gmail, you'll need to create an App Password.

## License

This project is created for educational purposes.
