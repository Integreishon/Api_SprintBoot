-- ============================================================================
-- TABLA 13: payments (Pagos Realizados) - SIN CAMPO NOTES
-- ============================================================================
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT UNIQUE NOT NULL REFERENCES appointments(id) ON DELETE CASCADE,
    payment_method_id BIGINT NOT NULL REFERENCES payment_methods(id),
    amount DECIMAL(10,2) NOT NULL,
    processing_fee DECIMAL(10,2),
    total_amount DECIMAL(10,2) NOT NULL,
    transaction_reference VARCHAR(100),
    payment_date TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    receipt_number VARCHAR(50),
    payer_name VARCHAR(150),
    payer_email VARCHAR(150),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar consultas
CREATE INDEX idx_payments_appointment_id ON payments(appointment_id);
CREATE INDEX idx_payments_payment_method_id ON payments(payment_method_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);
CREATE INDEX idx_payments_created_at ON payments(created_at);
CREATE INDEX idx_payments_receipt_number ON payments(receipt_number);
CREATE INDEX idx_payments_transaction_reference ON payments(transaction_reference);

-- Índice compuesto para reportes
CREATE INDEX idx_payments_status_payment_date ON payments(status, payment_date);

-- Constraint para números de recibo únicos
CREATE UNIQUE INDEX idx_payments_receipt_number_unique ON payments(receipt_number) WHERE receipt_number IS NOT NULL;

-- Constraint para referencias de transacción únicas
CREATE UNIQUE INDEX idx_payments_transaction_ref_unique ON payments(transaction_reference) WHERE transaction_reference IS NOT NULL;
