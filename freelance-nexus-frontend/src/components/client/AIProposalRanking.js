import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import aiService from '../../services/aiService';
import projectService from '../../services/projectService';
import Loader from '../common/Loader';

const AIProposalRanking = () => {
  const { projectId } = useParams();
  const [project, setProject] = useState(null);
  const [rankings, setRankings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRankings = async () => {
      try {
        const [projectData, rankingsData] = await Promise.all([
          projectService.getProjectById(projectId),
          aiService.rankProposals(projectId)
        ]);
        setProject(projectData);
        setRankings(rankingsData);
      } catch (error) {
        console.error('Error fetching rankings:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchRankings();
  }, [projectId]);

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <div className="row mb-4">
        <div className="col">
          <h2>
            <span className="ai-badge">AI</span> Ranked Proposals
          </h2>
          <p className="text-muted">
            Proposals ranked by AI based on experience, skills, and bid competitiveness
          </p>
        </div>
      </div>

      <div className="card shadow-sm mb-4 ai-feature">
        <div className="card-body">
          <h5 className="mb-3">Project: {project?.title}</h5>
          <p className="mb-0">
            Our AI has analyzed all proposals considering freelancer experience, skill match, 
            bid amount, and past success rates to provide you with smart recommendations.
          </p>
        </div>
      </div>

      {rankings.length === 0 ? (
        <div className="alert alert-info">
          <h5>No proposals to rank</h5>
          <p className="mb-0">There are no proposals submitted yet for this project.</p>
        </div>
      ) : (
        <div className="row g-4">
          {rankings.map((ranking, index) => (
            <div key={ranking.proposalId} className="col-12">
              <div className="card shadow-sm" style={{ 
                borderLeft: `4px solid ${
                  index === 0 ? '#198754' : 
                  index === 1 ? '#0dcaf0' : 
                  index === 2 ? '#ffc107' : '#6c757d'
                }`
              }}>
                <div className="card-body">
                  <div className="row">
                    <div className="col-md-1 text-center">
                      <div style={{ 
                        fontSize: '2rem', 
                        fontWeight: 'bold',
                        color: index === 0 ? '#198754' : 
                               index === 1 ? '#0dcaf0' : 
                               index === 2 ? '#ffc107' : '#6c757d'
                      }}>
                        #{index + 1}
                      </div>
                      <div className="badge bg-success">
                        {ranking.score}% Match
                      </div>
                    </div>

                    <div className="col-md-7">
                      <h5 className="mb-2">{ranking.freelancerName}</h5>
                      
                      <div className="row mb-2">
                        <div className="col-sm-6">
                          <strong>Bid:</strong> ₹{ranking.bidAmount}
                        </div>
                        <div className="col-sm-6">
                          <strong>Duration:</strong> {ranking.duration} days
                        </div>
                      </div>

                      <div className="mb-3">
                        <strong>Skills Match:</strong>
                        <div className="mt-2">
                          {ranking.matchingSkills?.map((skill, idx) => (
                            <span key={idx} className="badge bg-success me-1 mb-1">
                              {skill}
                            </span>
                          ))}
                        </div>
                      </div>

                      <div className="ai-feature p-2">
                        <small>
                          <strong>AI Reasoning:</strong> {ranking.aiReasoning}
                        </small>
                      </div>
                    </div>

                    <div className="col-md-4 text-end">
                      <div className="mb-3">
                        <div className="mb-2">
                          <small className="text-muted">Experience Score</small>
                          <div className="progress" style={{ height: '10px' }}>
                            <div 
                              className="progress-bar bg-info" 
                              style={{ width: `${ranking.experienceScore}%` }}
                            ></div>
                          </div>
                        </div>
                        <div className="mb-2">
                          <small className="text-muted">Success Rate</small>
                          <div className="progress" style={{ height: '10px' }}>
                            <div 
                              className="progress-bar bg-success" 
                              style={{ width: `${ranking.successRate}%` }}
                            ></div>
                          </div>
                        </div>
                      </div>

                      <div className="d-grid gap-2">
                        <Link
                          to={`/client/proposals/${projectId}`}
                          className="btn btn-primary"
                        >
                          View Full Proposal
                        </Link>
                        <Link
                          to={`/freelancer/profile/${ranking.freelancerId}`}
                          className="btn btn-outline-primary"
                        >
                          View Profile
                        </Link>
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

export default AIProposalRanking;