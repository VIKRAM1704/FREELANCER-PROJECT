import React, { useState } from 'react';
import { Card, Form, Button, Alert } from 'react-bootstrap';
import paymentService from '../../services/paymentService';
import { toast } from 'react-toastify';

const UPIPayment = ({ amount, projectId, onSuccess }) => {
  const [upiId, setUpiId] = useState('');
  const [loading, setLoading] = useState(false);

  const handlePayment = async () => {
    setLoading(true);
    try {
      await paymentService.processUPIPayment({ amount, projectId, upiId });
      toast.success('Payment successful!');
      if (onSuccess) onSuccess();
    } catch (error) {
      toast.error('Payment failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card>
      <Card.Header>UPI Payment</Card.Header>
      <Card.Body>
        <Alert variant="info">Amount to pay: ₹{amount}</Alert>
        <Form.Group className="mb-3">
          <Form.Label>Enter UPI ID</Form.Label>
          <Form.Control type="text" placeholder="yourname@upi" value={upiId}
            onChange={(e) => setUpiId(e.target.value)} />
        </Form.Group>
        <Button onClick={handlePayment} disabled={loading || !upiId}>
          {loading ? 'Processing...' : 'Pay with UPI'}
        </Button>
      </Card.Body>
    </Card>
  );
};

export default UPIPayment;