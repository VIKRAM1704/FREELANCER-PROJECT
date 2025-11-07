import React, { useState, useEffect } from 'react';
import { Card, Alert, Button } from 'react-bootstrap';
import aiService from '../../services/aiService';

const ProjectSummary = ({ projectId }) => {
  const [summary, setSummary] = useState('');
  const [loading, setLoading] = useState(false);

  const generateSummary = async () => {
    setLoading(true);
    try {
      const data = await aiService.generateProjectSummary(projectId);
      setSummary(data.summary);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="mt-3">
      <Card.Header>
        <i className="bi bi-robot me-2"></i>AI-Generated Summary
      </Card.Header>
      <Card.Body>
        {summary ? (
          <Alert variant="info">{summary}</Alert>
        ) : (
          <Button onClick={generateSummary} disabled={loading}>
            {loading ? 'Generating...' : 'Generate AI Summary'}
          </Button>
        )}
      </Card.Body>
    </Card>
  );
};

export default ProjectSummary;