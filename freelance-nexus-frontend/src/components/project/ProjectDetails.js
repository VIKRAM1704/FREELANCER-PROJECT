import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import projectService from '../../services/projectService';
import aiService from '../../services/aiService';
import Loader from '../common/Loader';

const ProjectDetails = () => {
  const { id } = useParams();
  const { user, authenticated } = useAuth();
  const navigate = useNavigate();
  const [project, setProject] = useState(null);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProject = async () => {
      try {
        const data = await projectService.getProjectById(id);
        setProject(data);
      } catch (error) {
        console.error('Error fetching project:', error);
      } finally {
        setLoading(false);
      }
    };

    const fetchSummary = async () => {
      try {
        const data = await aiService.generateProjectSummary(id);
        setSummary(data);
      } catch (error) {
        console.error('Error fetching AI summary:', error);
      }
    };

    fetchProject();
    fetchSummary();
  }, [id]);

  if (loading) return <Loader />;

  if (!project) {
    return (
      <div className="container mt-4">
        <div className="alert alert-danger">Project not found</div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <div className="row mb-4">
        <div className="col">
          <button 
            className="btn btn-outline-secondary mb-3"
            onClick={() => navigate(-1)}
          >
            ← Back
          </button>
          <h2>{project.title}</h2>
          <p className="text-muted">
            Posted by {project.clientName} on {new Date(project.createdAt).toLocaleDateString()}
          </p>
        </div>
      </div>

      <div className="row">
        <div className="col-lg-8">
          {/* AI Summary */}
          {summary && (
            <div className="card shadow-sm mb-4 ai-feature">
              <div className="card-body">
                <h5 className="mb-3">
                  <span className="ai-badge">AI</span> Project Summary
                </h5>
                <p>{summary.summary}</p>
                <div className="mt-3">
                  <strong>Key Requirements:</strong>
                  <ul className="mt-2">
                    {summary.keyRequirements?.map((req, idx) => (
                      <li key={idx}>{req}</li>
                    ))}
                  </ul>
                </div>
              </div>
            </div>
          )}

          {/* Project Description */}
          <div className="card shadow-sm mb-4">
            <div className="card-header bg-primary text-white">
              <h5 className="mb-0">Project Description</h5>
            </div>
            <div className="card-body">
              <p style={{ whiteSpace: 'pre-wrap' }}>{project.description}</p>
            </div>
          </div>

          {/* Required Skills */}
          <div className="card shadow-sm mb-4">
            <div className="card-header bg-success text-white">
              <h5 className="mb-0">Required Skills</h5>
            </div>
            <div className="card-body">
              {project.requiredSkills?.map((skill, idx) => (
                <span key={idx} className="badge bg-secondary me-2 mb-2">
                  {skill}
                </span>
              ))}
            </div>
          </div>
        </div>

        <div className="col-lg-4">
          {/* Project Info */}
          <div className="card shadow-sm mb-4">
            <div className="card-header bg-info text-white">
              <h5 className="mb-0">Project Details</h5>
            </div>
            <div className="card-body">
              <div className="mb-3">
                <strong>Budget:</strong>
                <div className="mt-1">₹{project.minBudget} - ₹{project.maxBudget}</div>
              </div>

              <div className="mb-3">
                <strong>Duration:</strong>
                <div className="mt-1">{project.duration} days</div>
              </div>

              <div className="mb-3">
                <strong>Category:</strong>
                <div className="mt-1">
                  <span className="badge bg-primary">{project.category}</span>
                </div>
              </div>

              <div className="mb-3">
                <strong>Status:</strong>
                <div className="mt-1">
                  <span className={`badge bg-${
                    project.status === 'OPEN' ? 'success' :
                    project.status === 'IN_PROGRESS' ? 'warning' :
                    project.status === 'COMPLETED' ? 'info' : 'secondary'
                  }`}>
                    {project.status}
                  </span>
                </div>
              </div>

              <div className="mb-3">
                <strong>Proposals:</strong>
                <div className="mt-1">{project.proposalCount || 0}</div>
              </div>

              {authenticated && user?.roles?.includes('FREELANCER') && project.status === 'OPEN' && (
                <div className="d-grid gap-2 mt-4">
                  <Link 
                    to={`/freelancer/submit-proposal/${project.id}`}
                    className="btn btn-primary"
                  >
                    Submit Proposal
                  </Link>
                </div>
              )}

              {authenticated && user?.roles?.includes('CLIENT') && user?.id === project.clientId && (
                <div className="d-grid gap-2 mt-4">
                  <Link 
                    to={`/client/proposals/${project.id}`}
                    className="btn btn-primary"
                  >
                    View Proposals
                  </Link>
                  <Link 
                    to={`/client/ai-ranking/${project.id}`}
                    className="btn btn-info"
                  >
                    <span className="ai-badge">AI</span> Rank Proposals
                  </Link>
                </div>
              )}

              {!authenticated && (
                <div className="alert alert-info mt-4">
                  <small>
                    <Link to="/login">Login</Link> or <Link to="/register">Register</Link> to submit a proposal
                  </small>
                </div>
              )}
            </div>
          </div>

          {/* Client Info */}
          <div className="card shadow-sm">
            <div className="card-header bg-secondary text-white">
              <h5 className="mb-0">About the Client</h5>
            </div>
            <div className="card-body">
              <h6>{project.clientName}</h6>
              <p className="text-muted mb-2">Member since {new Date(project.clientJoinDate).getFullYear()}</p>
              <div className="mb-2">
                <strong>Projects Posted:</strong> {project.clientProjectCount || 0}
              </div>
              <div className="mb-2">
                <strong>Rating:</strong>  {project.clientRating || 'N/A'}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProjectDetails;