import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Loader from '../common/Loader';

const ProtectedRoute = ({ children, roles = [] }) => {
  const { user, loading, authenticated } = useAuth();

  if (loading) {
    return <Loader />;
  }

  if (!authenticated) {
    return <Navigate to="/api/users/login" replace />;
  }

  if (roles.length > 0 && !roles.includes(user?.role)) {
    return (
      <div className="container mt-5">
        <div className="alert alert-danger">
          <h4>Access Denied</h4>
          <p>You don't have permission to access this page.</p>
        </div>
      </div>
    );
  }

  return children;
};

export default ProtectedRoute;