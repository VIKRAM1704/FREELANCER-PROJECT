import React, { useState } from 'react';
import { Container, Card, Form, Button } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import paymentService from '../../services/paymentService';
import { toast } from 'react-toastify';

const PaymentForm = () => {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [payment, setPayment] = useState({
    amount: '', paymentMethod: 'UPI'
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await paymentService.initiatePayment({ ...payment, projectId });
      toast.success('Payment initiated!');
      navigate('/payments/history');
    } catch (error) {
      toast.error('Payment failed');
    }
  };

  return (
    <Container className="mt-4">
      <Card>
        <Card.Header><h4>Make Payment</h4></Card.Header>
        <Card.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Amount (₹)</Form.Label>
              <Form.Control type="number" required value={payment.amount}
                onChange={(e) => setPayment({...payment, amount: e.target.value})} />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Payment Method</Form.Label>
              <Form.Select value={payment.paymentMethod}
                onChange={(e) => setPayment({...payment, paymentMethod: e.target.value})}>
                <option value="UPI">UPI</option>
                <option value="Card">Credit/Debit Card</option>
              </Form.Select>
            </Form.Group>
            <Button type="submit" variant="primary">Pay Now</Button>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default PaymentForm;