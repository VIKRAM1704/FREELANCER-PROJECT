import React, { useState, useEffect } from 'react';
import { Container, Card, Badge, Button, Alert } from 'react-bootstrap';
import { useParams, Link } from 'react-router-dom';
import projectService from '../../services/projectService';
import aiService from '../../services/aiService';
import Loader from '../common/Loader';

const ProjectDetails = () => {
  const { id } = useParams();
  const [project, setProject] = useState(null);
  const [summary, setSummary] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProject();
    fetchSummary();
  }, [id]);

  const fetchProject = async () => {
    try {
      const data = await projectService.getProjectById(id);
      setProject(data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const fetchSummary = async () => {
    try {
      const data = await aiService.generateProjectSummary(id);
      setSummary(data.summary);
    } catch (error) {
      console.error(error);
    }
  };

  if (loading) return <Loader />;
  if (!project) return <Alert variant="danger">Project not found</Alert>;

  return (
    <Container className="mt-4">
      <Card className="shadow">
        <Card.Body>
          <h2>{project.title}</h2>
          <div className="mb-3">
            <Badge bg="primary" className="me-2">{project.category}</Badge>
            <Badge bg="success">{project.status}</Badge>
          </div>
          
          {summary && (
            <Alert variant="info">
              <i className="bi bi-robot me-2"></i>
              <strong>AI Summary:</strong> {summary}
            </Alert>
          )}

          <h5>Description</h5>
          <p>{project.description}</p>

          <h5>Required Skills</h5>
          <div className="mb-3">
            {project.skills?.map((s, i) => (
              <Badge key={i} bg="secondary" className="me-1 mb-1">{s}</Badge>
            ))}
          </div>

          <div className="row mb-3">
            <div className="col-md-6">
              <strong>Budget:</strong> ₹{project.budget}
            </div>
            <div className="col-md-6">
              <strong>Duration:</strong> {project.duration}
            </div>
          </div>

          <Link to={`/freelancer/submit-proposal/${id}`} className="btn btn-primary">
            Submit Proposal
          </Link>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default ProjectDetails;