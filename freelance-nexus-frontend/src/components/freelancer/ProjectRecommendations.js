import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import aiService from '../../services/aiService';
import Loader from '../common/Loader';

const ProjectRecommendations = () => {
  const { user } = useAuth();
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchRecommendations = useCallback(async () => {
    try {
      const data = await aiService.getProjectRecommendations(user.id);
      setRecommendations(data);
    } catch (error) {
      console.error('Error fetching recommendations:', error);
    } finally {
      setLoading(false);
    }
  }, [user.id]);

  useEffect(() => {
    fetchRecommendations();
  }, [fetchRecommendations]);

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <div className="row mb-4">
        <div className="col">
          <h2>
            <span className="ai-badge">AI</span> Project Recommendations
          </h2>
          <p className="text-muted">
            Projects matched to your skills and experience using AI
          </p>
        </div>
      </div>

      {recommendations.length === 0 ? (
        <div className="alert alert-info">
          <h5>No recommendations yet</h5>
          <p className="mb-0">
            Complete your profile and add skills to get personalized project recommendations.
          </p>
        </div>
      ) : (
        <div className="row g-4">
          {recommendations.map((rec) => (
            <div key={rec.project.id} className="col-md-6 col-lg-4">
              <div className="card h-100 shadow-sm project-card">
                <div className="card-body">
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <h5 className="card-title">{rec.project.title}</h5>
                    <span className="badge bg-success">{rec.matchScore}% Match</span>
                  </div>

                  <p className="card-text text-muted">
                    {rec.project.description?.substring(0, 150)}...
                  </p>

                  <div className="mb-3">
                    <strong>Budget:</strong> ₹{rec.project.minBudget} - ₹{rec.project.maxBudget}
                  </div>

                  <div className="mb-3">
                    <strong>Required Skills:</strong>
                    <div className="mt-2">
                      {rec.project.requiredSkills?.slice(0, 3).map((skill, idx) => (
                        <span key={idx} className="badge bg-secondary me-1 mb-1">{skill}</span>
                      ))}
                    </div>
                  </div>

                  <div className="ai-feature p-2 mb-3">
                    <small className="d-block mb-1">
                      <strong>Why this matches:</strong>
                    </small>
                    <small>{rec.reason}</small>
                  </div>

                  <div className="d-grid gap-2">
                    <Link to={`/projects/${rec.project.id}`} className="btn btn-outline-primary">
                      View Details
                    </Link>
                    <Link to={`/freelancer/submit-proposal/${rec.project.id}`} className="btn btn-primary">
                      Submit Proposal
                    </Link>
                  </div>
                </div>
                <div className="card-footer bg-transparent">
                  <small className="text-muted">
                    Posted: {new Date(rec.project.postedAt).toLocaleDateString()}
                  </small>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ProjectRecommendations;
