import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import projectService from '../../services/projectService';
import Loader from '../common/Loader';

const ClientDashboard = () => {
  const { user } = useAuth();
  const [projects, setProjects] = useState([]);
  const [stats, setStats] = useState({
    totalProjects: 0,
    activeProjects: 0,
    completedProjects: 0,
    totalSpent: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const projectsData = await projectService.getClientProjects(user.id);
        setProjects(projectsData.slice(0, 5));
        
        setStats({
          totalProjects: projectsData.length,
          activeProjects: projectsData.filter(p => p.status === 'IN_PROGRESS').length,
          completedProjects: projectsData.filter(p => p.status === 'COMPLETED').length,
          totalSpent: projectsData.reduce((sum, p) => sum + (p.budget || 0), 0)
        });
      } catch (error) {
        console.error('Error fetching dashboard:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, [user.id]);

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <h2>Welcome, {user?.firstName || user?.username}! 👋</h2>
      <p className="text-muted">Manage your projects and find talented freelancers</p>

      <div className="row g-4 mb-4">
        <div className="col-md-3">
          <div className="card stat-card bg-primary text-white">
            <div className="card-body text-center">
              <h3>{stats.totalProjects}</h3>
              <p className="mb-0">Total Projects</p>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card stat-card bg-warning text-dark">
            <div className="card-body text-center">
              <h3>{stats.activeProjects}</h3>
              <p className="mb-0">Active</p>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card stat-card bg-success text-white">
            <div className="card-body text-center">
              <h3>{stats.completedProjects}</h3>
              <p className="mb-0">Completed</p>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card stat-card bg-info text-white">
            <div className="card-body text-center">
              <h3>₹{stats.totalSpent}</h3>
              <p className="mb-0">Total Spent</p>
            </div>
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-md-8">
          <div className="card shadow-sm">
            <div className="card-header bg-white">
              <h5 className="mb-0">Recent Projects</h5>
            </div>
            <div className="card-body">
              {projects.length === 0 ? (
                <p className="text-center text-muted py-3">No projects yet</p>
              ) : (
                <div className="list-group list-group-flush">
                  {projects.map(project => (
                    <div key={project.id} className="list-group-item">
                      <div className="d-flex justify-content-between">
                        <div>
                          <h6 className="mb-1">{project.title}</h6>
                          <p className="mb-1 text-muted small">
                            Budget: ₹{project.budget} | Proposals: {project.proposalCount || 0}
                          </p>
                          <small className="text-muted">
                            Posted: {new Date(project.createdAt).toLocaleDateString()}
                          </small>
                        </div>
                        <span className={`badge bg-${
                          project.status === 'COMPLETED' ? 'success' :
                          project.status === 'IN_PROGRESS' ? 'warning' : 'primary'
                        }`}>
                          {project.status}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
              <div className="text-center mt-3">
                <Link to="/client/projects" className="btn btn-outline-primary">
                  View All Projects
                </Link>
              </div>
            </div>
          </div>
        </div>

        <div className="col-md-4">
          <div className="card shadow-sm">
            <div className="card-header bg-white">
              <h5 className="mb-0">Quick Actions</h5>
            </div>
            <div className="card-body">
              <div className="d-grid gap-2">
                <Link to="/client/post-project" className="btn btn-primary">
                  Post New Project
                </Link>
                <Link to="/client/projects" className="btn btn-outline-primary">
                  My Projects
                </Link>
                <Link to="/payment-history" className="btn btn-outline-info">
                  Payment History
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ClientDashboard;