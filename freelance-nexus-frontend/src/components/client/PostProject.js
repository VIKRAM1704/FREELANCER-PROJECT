import React, { useState } from 'react';
import { Container, Card, Form, Button, Row, Col } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import projectService from '../../services/projectService';
import { toast } from 'react-toastify';

const PostProject = () => {
  const navigate = useNavigate();
  const [project, setProject] = useState({
    title: '', description: '', budget: '', duration: '', skills: []
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await projectService.createProject(project);
      toast.success('Project posted successfully!');
      navigate('/client/my-projects');
    } catch (error) {
      toast.error('Failed to post project');
    }
  };

  return (
    <Container className="mt-4">
      <h2>Post New Project</h2>
      <Card className="mt-4">
        <Card.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Project Title</Form.Label>
              <Form.Control type="text" required value={project.title}
                onChange={(e) => setProject({...project, title: e.target.value})} />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control as="textarea" rows={4} required value={project.description}
                onChange={(e) => setProject({...project, description: e.target.value})} />
            </Form.Group>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Budget (₹)</Form.Label>
                  <Form.Control type="number" required value={project.budget}
                    onChange={(e) => setProject({...project, budget: e.target.value})} />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Duration</Form.Label>
                  <Form.Control type="text" placeholder="e.g., 2 weeks" required
                    value={project.duration}
                    onChange={(e) => setProject({...project, duration: e.target.value})} />
                </Form.Group>
              </Col>
            </Row>
            <Button type="submit" variant="primary">Post Project</Button>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default PostProject;