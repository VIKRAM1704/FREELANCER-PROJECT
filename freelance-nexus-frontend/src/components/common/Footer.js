import React from 'react';
import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <footer className="bg-dark text-white mt-5">
      <div className="container py-4">
        <div className="row">
          <div className="col-md-4 mb-3">
            <h5 className="fw-bold">Freelance Nexus</h5>
            <p className="text-muted">
              AI-powered freelance marketplace connecting talented professionals with amazing projects.
            </p>
          </div>
          
          <div className="col-md-4 mb-3">
            <h6 className="fw-bold">Quick Links</h6>
            <ul className="list-unstyled">
              <li><Link to="/projects" className="text-muted text-decoration-none">Browse Projects</Link></li>
              <li><Link to="/login" className="text-muted text-decoration-none">Login</Link></li>
              <li><Link to="/register" className="text-muted text-decoration-none">Register</Link></li>
            </ul>
          </div>
          
          <div className="col-md-4 mb-3">
            <h6 className="fw-bold">Contact</h6>
            <p className="text-muted mb-1">
              <small>Email: support@freelancenexus.com</small>
            </p>
            <p className="text-muted">
              <small>Phone: +91 1234567890</small>
            </p>
          </div>
        </div>
        
        <hr className="border-secondary" />
        
        <div className="text-center text-muted">
          <small>&copy; 2024 Freelance Nexus. All rights reserved.</small>
        </div>
      </div>
    </footer>
  );
};

export default Footer;