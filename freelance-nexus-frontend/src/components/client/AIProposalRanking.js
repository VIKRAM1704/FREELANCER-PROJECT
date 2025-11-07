import React, { useState, useEffect } from 'react';
import { Container, Card, Badge, Alert } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import aiService from '../../services/aiService';
import Loader from '../common/Loader';

const AIProposalRanking = () => {
  const { projectId } = useParams();
  const [rankings, setRankings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchRankings();
  }, [projectId]);

  const fetchRankings = async () => {
    try {
      const data = await aiService.rankProposals(projectId);
      setRankings(data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Loader message="AI is ranking proposals..." />;

  return (
    <Container className="mt-4">
      <h2><i className="bi bi-robot me-2"></i>AI-Ranked Proposals</h2>
      <Alert variant="info" className="mt-3">
        Our AI has analyzed and ranked proposals based on freelancer experience, skills match, and proposal quality.
      </Alert>
      {rankings.map((r, idx) => (
        <Card key={r.id} className="mt-3">
          <Card.Body>
            <div className="d-flex justify-content-between">
              <div>
                <Badge bg="primary">Rank #{idx + 1}</Badge>
                <h5 className="mt-2">{r.freelancerName}</h5>
                <p>Score: {r.aiScore}/100</p>
              </div>
              <Badge bg="success">{r.matchPercentage}% Match</Badge>
            </div>
          </Card.Body>
        </Card>
      ))}
    </Container>
  );
};

export default AIProposalRanking;