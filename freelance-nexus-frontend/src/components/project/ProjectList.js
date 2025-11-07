import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Badge, Form, InputGroup } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import projectService from '../../services/projectService';
import Loader from '../common/Loader';

const ProjectList = () => {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    try {
      const data = await projectService.getOpenProjects();
      setProjects(data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const filteredProjects = projects.filter(p => 
    p.title.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <Loader />;

  return (
    <Container className="mt-4">
      <h2>Browse Projects</h2>
      <InputGroup className="my-4">
        <InputGroup.Text><i className="bi bi-search"></i></InputGroup.Text>
        <Form.Control placeholder="Search projects..." value={search}
          onChange={(e) => setSearch(e.target.value)} />
      </InputGroup>
      <Row>
        {filteredProjects.map(p => (
          <Col lg={6} key={p.id} className="mb-4">
            <Card className="shadow-sm h-100">
              <Card.Body>
                <h5>{p.title}</h5>
                <p>{p.description?.substring(0, 150)}...</p>
                <div className="mb-2">
                  {p.skills?.map((s, i) => (
                    <Badge key={i} bg="secondary" className="me-1">{s}</Badge>
                  ))}
                </div>
                <div className="d-flex justify-content-between">
                  <strong className="text-primary">₹{p.budget}</strong>
                  <Link to={`/projects/${p.id}`} className="btn btn-sm btn-primary">
                    View Details
                  </Link>
                </div>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>
    </Container>
  );
};

export default ProjectList;