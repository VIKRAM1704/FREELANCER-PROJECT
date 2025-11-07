# Freelance Nexus - Frontend

AI-Powered Freelancing Platform - React Frontend Application

## 🚀 Features

- **AI-Powered Recommendations**: Personalized project recommendations for freelancers
- **AI Proposal Ranking**: Intelligent proposal ranking for clients
- **Keycloak Authentication**: Secure authentication with Keycloak
- **Role-Based Access**: Different dashboards for Freelancers, Clients, and Admins
- **UPI Payment Integration**: Seamless payment processing
- **Responsive Design**: Mobile-friendly UI with Bootstrap 5
- **Real-time Updates**: Live data updates and notifications

## 📋 Prerequisites

- Node.js 18+ and npm
- Running backend services (Gateway, Keycloak)
- Gateway URL: http://localhost:8765
- Keycloak URL: http://localhost:8080

## 🛠️ Installation

### 1. Clone and Install Dependencies

```bash
cd freelance-nexus-frontend
npm install
```

### 2. Configure Environment Variables

Create `.env` file in the root directory:

```env
REACT_APP_API_GATEWAY_URL=http://localhost:8765
REACT_APP_KEYCLOAK_URL=http://localhost:8080
REACT_APP_KEYCLOAK_REALM=freelance-nexus
REACT_APP_KEYCLOAK_CLIENT_ID=freelance-nexus-client
```

### 3. Start Development Server

```bash
npm start
```

The application will open at `http://localhost:3000`

## 📁 Project Structure

```
src/
├── components/
│   ├── auth/           # Authentication components
│   ├── common/         # Shared components (Header, Footer, Loader)
│   ├── freelancer/     # Freelancer-specific components
│   ├── client/         # Client-specific components
│   ├── project/        # Project-related components
│   └── payment/        # Payment components
├── services/           # API service modules
│   ├── api.js          # Axios instance with interceptors
│   ├── authService.js
│   ├── userService.js
│   ├── freelancerService.js
│   ├── projectService.js
│   ├── proposalService.js
│   ├── paymentService.js
│   └── aiService.js    # AI-related API calls
├── context/
│   └── AuthContext.js  # Authentication context with Keycloak
├── utils/
│   └── constants.js    # Application constants
├── styles/
│   └── custom.css      # Custom styles
├── App.js              # Main application component
└── index.js            # Application entry point
```

## 🎨 Key Components

### Authentication
- **Login.js** - Keycloak login integration
- **Register.js** - User registration
- **ProtectedRoute.js** - Route guard for authenticated routes

### Freelancer Features
- **FreelancerDashboard** - Overview with stats and recent activities
- **ProjectRecommendations** - AI-powered project suggestions
- **SubmitProposal** - Proposal submission form
- **MyProposals** - List of submitted proposals
- **FreelancerProfile** - Profile management

### Client Features
- **ClientDashboard** - Client overview and statistics
- **PostProject** - Create new project
- **MyProjects** - Manage client projects
- **ViewProposals** - Review proposals for projects
- **AIProposalRanking** - AI-ranked proposals

### Project Features
- **ProjectList** - Browse all open projects
- **ProjectDetails** - Detailed project view with AI summary
- **ProjectSummary** - AI-generated project summary

### Payment Features
- **PaymentForm** - Payment interface
- **UPIPayment** - UPI-specific payment flow
- **PaymentHistory** - Transaction history

## 🔧 Available Scripts

### `npm start`
Runs the app in development mode at [http://localhost:3000](http://localhost:3000)

### `npm run build`
Builds the app for production to the `build` folder

### `npm test`
Launches the test runner in interactive watch mode

### `npm run eject`
**Note: this is a one-way operation. Once you eject, you can't go back!**

## 🐳 Docker Deployment

### Build Docker Image

```bash
docker build -t freelance-nexus-frontend .
```

### Run Docker Container

```bash
docker run -p 80:80 \
  -e REACT_APP_API_GATEWAY_URL=http://gateway:8765 \
  -e REACT_APP_KEYCLOAK_URL=http://keycloak:8080 \
  freelance-nexus-frontend
```

### Docker Compose (Optional)

```yaml
version: '3.8'
services:
  frontend:
    build: .
    ports:
      - "80:80"
    environment:
      - REACT_APP_API_GATEWAY_URL=http://gateway:8765
      - REACT_APP_KEYCLOAK_URL=http://keycloak:8080
    depends_on:
      - gateway
```

## 🔐 Keycloak Configuration

### Required Keycloak Setup:

1. **Create Realm**: `freelance-nexus`
2. **Create Client**: `freelance-nexus-client`
   - Client Protocol: `openid-connect`
   - Access Type: `public`
   - Standard Flow Enabled: `ON`
   - Valid Redirect URIs: `http://localhost:3000/*`
   - Web Origins: `http://localhost:3000`

3. **Create Roles**:
   - `ADMIN`
   - `CLIENT`
   - `FREELANCER`

4. **Create Test Users** and assign roles

## 🎯 User Roles

### Freelancer
- View AI-recommended projects
- Submit proposals
- Manage profile and portfolio
- Track proposal status
- Receive payments

### Client
- Post new projects
- View AI-ranked proposals
- Accept/reject proposals
- Make payments
- Track project progress

### Admin
- Manage users
- Monitor platform activity
- View analytics
- Moderate content

## 🚦 API Integration

All API calls go through the API Gateway at `http://localhost:8765`

### Service Endpoints:
- **User Service**: `/api/users/*`
- **Freelancer Service**: `/api/freelancers/*`
- **Project Service**: `/api/projects/*`
- **Proposal Service**: `/api/proposals/*`
- **Payment Service**: `/api/payments/*`
- **AI Service**: `/api/ai/*`

### Authentication
JWT tokens from Keycloak are automatically attached to API requests via Axios interceptors.

## 🎨 Styling

- **Bootstrap 5** for responsive UI components
- **Bootstrap Icons** for iconography
- **Custom CSS** for additional styling
- **React-Bootstrap** for React-specific Bootstrap components

## 📱 Responsive Design

The application is fully responsive and optimized for:
- Desktop (1200px+)
- Tablet (768px - 1199px)
- Mobile (< 768px)

## 🔔 Notifications

Toast notifications using `react-toastify`:
- Success messages
- Error handling
- Info alerts
- Warning notifications

## 🚀 Performance Optimizations

- Code splitting with React.lazy()
- Image optimization
- Gzip compression (in production)
- Caching strategies
- Lazy loading components

## 🧪 Testing

```bash
# Run tests
npm test

# Run tests with coverage
npm test -- --coverage
```

## 📝 Environment Configuration

### Development
```env
REACT_APP_API_GATEWAY_URL=http://localhost:8765
REACT_APP_KEYCLOAK_URL=http://localhost:8080
```

### Production
```env
REACT_APP_API_GATEWAY_URL=https://api.freelancenexus.com
REACT_APP_KEYCLOAK_URL=https://auth.freelancenexus.com
```

## 🐛 Troubleshooting

### Keycloak Connection Issues
- Verify Keycloak is running on port 8080
- Check realm and client configuration
- Ensure CORS is properly configured

### API Gateway Issues
- Confirm Gateway is running on port 8765
- Check network connectivity
- Verify JWT token is being sent

### Build Errors
- Clear node_modules: `rm -rf node_modules && npm install`
- Clear cache: `npm cache clean --force`
- Check Node version: `node --version` (should be 18+)

## 📚 Additional Resources

- [React Documentation](https://reactjs.org/)
- [Bootstrap 5 Documentation](https://getbootstrap.com/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Axios Documentation](https://axios-http.com/)

## 👥 Support

For issues and questions:
- Create an issue in the repository
- Contact the development team
- Check the FAQ section

## 📄 License

This project is part of the Freelance Nexus platform.

---

Built with ❤️ using React, Bootstrap, and AI
