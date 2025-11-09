import React from 'react';

const ProjectSummary = ({ summary }) => {
  if (!summary) return null;

  return (
    <div className="card shadow-sm ai-feature">
      <div className="card-body">
        <h5 className="mb-3">
          <span className="ai-badge">AI</span> Project Summary
        </h5>
        <p>{summary.summary}</p>
        
        {summary.keyRequirements && (
          <div className="mt-3">
            <strong>Key Requirements:</strong>
            <ul className="mt-2">
              {summary.keyRequirements.map((req, idx) => (
                <li key={idx}>{req}</li>
              ))}
            </ul>
          </div>
        )}

        {summary.suggestedSkills && (
          <div className="mt-3">
            <strong>Suggested Skills:</strong>
            <div className="mt-2">
              {summary.suggestedSkills.map((skill, idx) => (
                <span key={idx} className="badge bg-success me-1 mb-1">
                  {skill}
                </span>
              ))}
            </div>
          </div>
        )}

        {summary.estimatedComplexity && (
          <div className="mt-3">
            <strong>Estimated Complexity:</strong>
            <span className={`badge ms-2 bg-${
              summary.estimatedComplexity === 'LOW' ? 'success' :
              summary.estimatedComplexity === 'MEDIUM' ? 'warning' : 'danger'
            }`}>
              {summary.estimatedComplexity}
            </span>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProjectSummary;