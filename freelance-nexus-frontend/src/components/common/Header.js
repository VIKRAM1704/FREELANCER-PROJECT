import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Header = () => {
  const { user, authenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
      <div className="container">
        <Link className="navbar-brand fw-bold" to="/">
          🚀 Freelance Nexus
        </Link>
        
        <button 
          className="navbar-toggler" 
          type="button" 
          data-bs-toggle="collapse" 
          data-bs-target="#navbarNav"
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto">
            <li className="nav-item">
              <Link className="nav-link" to="/projects">Browse Projects</Link>
            </li>
            
            {authenticated && user?.roles?.includes('FREELANCER') && (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/freelancer/dashboard">
                    Dashboard
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/freelancer/recommendations">
                    <span className="badge bg-warning text-dark">AI</span> Recommendations
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/freelancer/proposals">
                    My Proposals
                  </Link>
                </li>
              </>
            )}
            
            {authenticated && user?.roles?.includes('CLIENT') && (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/client/dashboard">
                    Dashboard
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/client/post-project">
                    Post Project
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/client/projects">
                    My Projects
                  </Link>
                </li>
              </>
            )}
          </ul>
          
          <ul className="navbar-nav">
            {authenticated ? (
              <>
                <li className="nav-item dropdown">
                  <a 
                    className="nav-link dropdown-toggle" 
                    href="#" 
                    role="button" 
                    data-bs-toggle="dropdown"
                  >
                    👤 {user?.username || 'User'}
                  </a>
                  <ul className="dropdown-menu dropdown-menu-end">
                    {user?.roles?.includes('FREELANCER') && (
                      <li>
                        <Link className="dropdown-item" to="/freelancer/profile">
                          My Profile
                        </Link>
                      </li>
                    )}
                    <li>
                      <Link className="dropdown-item" to="/payment-history">
                        Payment History
                      </Link>
                    </li>
                    <li><hr className="dropdown-divider" /></li>
                    <li>
                      <button className="dropdown-item" onClick={handleLogout}>
                        Logout
                      </button>
                    </li>
                  </ul>
                </li>
              </>
            ) : (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/login">Login</Link>
                </li>
                <li className="nav-item">
                  <Link className="btn btn-light btn-sm ms-2" to="/register">
                    Sign Up
                  </Link>
                </li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Header;