import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Header from './components/common/Header';
import Footer from './components/common/Footer';
import ProtectedRoute from './components/auth/ProtectedRoute';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import Logout from './components/auth/Logout';
import FreelancerDashboard from './components/freelancer/FreelancerDashboard';
import FreelancerProfile from './components/freelancer/FreelancerProfile';
import ProjectRecommendations from './components/freelancer/ProjectRecommendations';
import SubmitProposal from './components/freelancer/SubmitProposal';
import MyProposals from './components/freelancer/MyProposals';
import ClientDashboard from './components/client/ClientDashboard';
import PostProject from './components/client/PostProject';
import MyProjects from './components/client/MyProjects';
import ViewProposals from './components/client/ViewProposals';
import AIProposalRanking from './components/client/AIProposalRanking';
import ProjectList from './components/project/ProjectList';
import ProjectDetails from './components/project/ProjectDetails';
import PaymentForm from './components/payment/PaymentForm';
import PaymentHistory from './components/payment/PaymentHistory';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="d-flex flex-column min-vh-100">
          <Header />
          <main className="flex-grow-1">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/logout" element={<Logout />} />
              <Route path="/" element={<Navigate to="/projects" replace />} />
              
              {/* Public Routes */}
              <Route path="/projects" element={<ProjectList />} />
              <Route path="/projects/:id" element={<ProjectDetails />} />
              
              {/* Freelancer Routes */}
              <Route path="/freelancer/dashboard" element={
                <ProtectedRoute roles={['FREELANCER']}>
                  <FreelancerDashboard />
                </ProtectedRoute>
              } />
              <Route path="/freelancer/profile" element={
                <ProtectedRoute roles={['FREELANCER']}>
                  <FreelancerProfile />
                </ProtectedRoute>
              } />
              <Route path="/freelancer/recommendations" element={
                <ProtectedRoute roles={['FREELANCER']}>
                  <ProjectRecommendations />
                </ProtectedRoute>
              } />
              <Route path="/freelancer/proposals" element={
                <ProtectedRoute roles={['FREELANCER']}>
                  <MyProposals />
                </ProtectedRoute>
              } />
              <Route path="/freelancer/submit-proposal/:projectId" element={
                <ProtectedRoute roles={['FREELANCER']}>
                  <SubmitProposal />
                </ProtectedRoute>
              } />
              
              {/* Client Routes */}
              <Route path="/client/dashboard" element={
                <ProtectedRoute roles={['CLIENT']}>
                  <ClientDashboard />
                </ProtectedRoute>
              } />
              <Route path="/client/post-project" element={
                <ProtectedRoute roles={['CLIENT']}>
                  <PostProject />
                </ProtectedRoute>
              } />
              <Route path="/client/projects" element={
                <ProtectedRoute roles={['CLIENT']}>
                  <MyProjects />
                </ProtectedRoute>
              } />
              <Route path="/client/proposals/:projectId" element={
                <ProtectedRoute roles={['CLIENT']}>
                  <ViewProposals />
                </ProtectedRoute>
              } />
              <Route path="/client/ai-ranking/:projectId" element={
                <ProtectedRoute roles={['CLIENT']}>
                  <AIProposalRanking />
                </ProtectedRoute>
              } />
              
              {/* Payment Routes */}
              <Route path="/payment/:proposalId" element={
                <ProtectedRoute roles={['CLIENT']}>
                  <PaymentForm />
                </ProtectedRoute>
              } />
              <Route path="/payment-history" element={
                <ProtectedRoute>
                  <PaymentHistory />
                </ProtectedRoute>
              } />
            </Routes>
          </main>
          <Footer />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;