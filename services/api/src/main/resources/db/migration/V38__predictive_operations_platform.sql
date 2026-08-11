-- V38__predictive_operations_platform.sql: Enterprise Predictive Operations Platform

CREATE TABLE IF NOT EXISTS predictive_forecasts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    forecast_type VARCHAR(50) NOT NULL,
    probability_score INT NOT NULL,
    confidence_score INT NOT NULL,
    forecast_window VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS prediction_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    prediction_type VARCHAR(50) NOT NULL,
    actual_outcome VARCHAR(50) NOT NULL,
    accuracy_score INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS capacity_forecasts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    metric_name VARCHAR(100) NOT NULL,
    current_usage DOUBLE PRECISION NOT NULL,
    projected_usage DOUBLE PRECISION NOT NULL,
    exhaustion_days INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS risk_forecasts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    risk_factor VARCHAR(150) NOT NULL,
    severity VARCHAR(30) DEFAULT 'MEDIUM',
    mitigation_recommendation TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS prediction_models (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    model_name VARCHAR(150) NOT NULL,
    model_version VARCHAR(50) NOT NULL,
    status VARCHAR(30) DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
