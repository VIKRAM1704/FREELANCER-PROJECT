import React, { useState } from 'react';

const UPIPayment = ({ amount, onPaymentSuccess, onPaymentFailure }) => {
  const [upiId, setUpiId] = useState('');
  const [processing, setProcessing] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setProcessing(true);

    try {
      // Simulate UPI payment processing
      // In production, this would integrate with actual UPI gateway
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      const transactionId = 'TXN' + Date.now();
      onPaymentSuccess({ transactionId, upiId, amount });
    } catch (error) {
      onPaymentFailure(error);
    } finally {
      setProcessing(false);
    }
  };

  return (
    <div className="card shadow-sm">
      <div className="card-header bg-success text-white">
        <h5 className="mb-0">💳 Pay with UPI</h5>
      </div>
      <div className="card-body">
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Enter UPI ID</label>
            <input
              type="text"
              className="form-control"
              value={upiId}
              onChange={(e) => setUpiId(e.target.value)}
              placeholder="username@upi"
              required
            />
            <small className="text-muted">
              Supported: GPay, PhonePe, Paytm, BHIM
            </small>
          </div>

          <div className="mb-3">
            <label className="form-label">Amount</label>
            <input
              type="text"
              className="form-control"
              value={`₹${amount}`}
              readOnly
            />
          </div>

          <div className="alert alert-info">
            <small>
              <strong>Note:</strong> You will receive a payment request on your UPI app. 
              Please approve it to complete the transaction.
            </small>
          </div>

          <div className="d-grid">
            <button 
              type="submit" 
              className="btn btn-success"
              disabled={processing || !upiId}
            >
              {processing ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2"></span>
                  Processing...
                </>
              ) : (
                `Pay ₹${amount}`
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default UPIPayment;