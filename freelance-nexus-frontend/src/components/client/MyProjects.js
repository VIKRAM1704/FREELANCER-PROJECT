import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import projectService from '../../services/projectService';
import Loader from '../common/Loader';

const MyProjects = () => {
  const { user } = useAuth();
  const [projects, setProjects] = useState([]);
  const [filter, setFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const data = await projectService.getClientProjects(user.id);
        setProjects(data);
      } catch (error) {
        console.error('Error fetching projects:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchProjects();
  }, [user.id]);

  const handleDelete = async (projectId) => {
    if (window.confirm('Are you sure you want to delete this project?')) {
      try {
        await projectService.deleteProject(projectId);
        setProjects(projects.filter(p => p.id !== projectId));
      } catch (error) {
        alert('Failed to delete project');
      }
    }
  };

  const filteredProjects = projects.filter(p => 
    filter === 'ALL' || p.status === filter
  );

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <div className="row mb-4">
        <div className="col">
          <h2>My Projects</h2>
          <p className="text-muted">Manage all your posted projects</p>
        </div>
        <div className="col-auto">
          <Link to="/client/post-project" className="btn btn-primary">
            Post New Project
          </Link>
        </div>
      </div>

      <div className="row mb-4">
        <div className="col">
          <div className="btn-group" role="group">
            <button 
              className={`btn ${filter === 'ALL' ? 'btn-primary' : 'btn-outline-primary'}`}
              onClick={() => setFilter('ALL')}
            >
              All ({projects.length})
            </button>
            <button 
              className={`btn ${filter === 'OPEN' ? 'btn-success' : 'btn-outline-success'}`}
              onClick={() => setFilter('OPEN')}
            >
              Open ({projects.filter(p => p.status === 'OPEN').length})
            </button>
            <button 
              className={`btn ${filter === 'IN_PROGRESS' ? 'btn-warning' : 'btn-outline-warning'}`}
              onClick={() => setFilter('IN_PROGRESS')}
            >
              In Progress ({projects.filter(p => p.status === 'IN_PROGRESS').length})
            </button>
            <button 
              className={`btn ${filter === 'COMPLETED' ? 'btn-info' : 'btn-outline-info'}`}
              onClick={() => setFilter('COMPLETED')}
            >
              Completed ({projects.filter(p => p.status === 'COMPLETED').length})
            </button>
          </div>
        </div>
      </div>

      {filteredProjects.length === 0 ? (
        <div className="alert alert-info">
          <h5>No projects found</h5>
          <p className="mb-0">Start by posting your first project!</p>
        </div>
      ) : (
        <div className="row g-4">
          {filteredProjects.map((project) => (
            <div key={project.id} className="col-12">
              <div className="card shadow-sm project-card">
                <div className="card-body">
                  <div className="row">
                    <div className="col-md-8">
                      <h5 className="card-title">{project.title}</h5>
                      <p className="text-muted mb-2">
                        {project.description?.substring(0, 200)}...
                      </p>
                      
                      <div className="row mb-2">
                        <div className="col-sm-6">
                          <strong>Budget:</strong> ₹{project.minBudget} - ₹{project.maxBudget}
                        </div>
                        <div className="col-sm-6">
                          <strong>Duration:</strong> {project.duration} days
                        </div>
                      </div>

                      <div className="mb-2">
                        <strong>Proposals:</strong> {project.proposalCount || 0}
                      </div>

                      <small className="text-muted">
                        Posted: {new Date(project.createdAt).toLocaleDateString()}
                      </small>
                    </div>

                    <div className="col-md-4 text-end">
                      <span className={`badge bg-${
                        project.status === 'COMPLETED' ? 'success' :
                        project.status === 'IN_PROGRESS' ? 'warning' :
                        project.status === 'CANCELLED' ? 'danger' :
                        'primary'
                      } mb-3`}>
                        {project.status}
                      </span>

                      <div className="d-grid gap-2">
                        <Link 
                          to={`/projects/${project.id}`}
                          className="btn btn-sm btn-outline-primary"
                        >
                          View Details
                        </Link>
                        
                        <Link 
                          to={`/client/proposals/${project.id}`}
                          className="btn btn-sm btn-primary"
                        >
                          View Proposals ({project.proposalCount || 0})
                        </Link>

                        {project.proposalCount > 0 && (
                          <Link 
                            to={`/client/ai-ranking/${project.id}`}
                            className="btn btn-sm btn-info"
                          >
                            <span className="ai-badge">AI</span> Rank Proposals
                          </Link>
                        )}

                        {project.status === 'OPEN' && (
                          <button
                            className="btn btn-sm btn-outline-danger"
                            onClick={() => handleDelete(project.id)}
                          >
                            Delete
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MyProjects;