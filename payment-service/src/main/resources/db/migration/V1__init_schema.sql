CREATE TABLE IF NOT EXISTS payments (
  id BIGSERIAL PRIMARY KEY,
  transaction_id VARCHAR(255) UNIQUE NOT NULL,
  project_id BIGINT,
  payer_id BIGINT,
  payee_id BIGINT,
  amount DECIMAL(10,2),
  currency VARCHAR(10),
  payment_method VARCHAR(50),
  upi_id VARCHAR(100),
  status VARCHAR(20),
  payment_date TIMESTAMP,
  description TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transaction_history (
  id BIGSERIAL PRIMARY KEY,
  payment_id BIGINT REFERENCES payments(id),
  status_change VARCHAR(50),
  notes TEXT,
  changed_at TIMESTAMP
);

-- sample data
INSERT INTO payments(transaction_id, project_id, payer_id, payee_id, amount, currency, payment_method, upi_id, status, created_at)
VALUES ('txn-sample-1', 1, 101, 201, 1000.00, 'INR', 'UPI', 'test@upi', 'COMPLETED', NOW());
