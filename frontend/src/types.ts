export type Role = 'CUSTOMER' | 'ADMIN'

export interface AuthSession {
  accessToken: string
  tokenType: string
  expiresInSeconds: number
  userId: string
  email: string
  roles: Role[]
}

export interface Account {
  id: string
  ownerId: string
  accountNumber: string
  balance: number
  currency: string
  createdAt: string
}

export type TransactionStatus =
  | 'PENDING_REVIEW'
  | 'FLAGGED'
  | 'REJECTED'
  | 'PROCESSING'
  | 'COMPLETED'
  | 'FAILED'

export interface Transaction {
  id: string
  sourceAccountId: string
  destinationAccountId: string
  amount: number
  currency: string
  status: TransactionStatus
  failureReason?: string
  createdAt: string
  updatedAt: string
}

export interface FraudDecision {
  transactionId: string
  decision: 'APPROVED' | 'REJECTED' | 'FLAGGED'
  riskScore: number
  reasons: string[]
  createdAt: string
  updatedAt: string
}

export interface AuditEvent {
  id: string
  sourceEventId: string
  transactionId: string
  eventType: string
  payload: string
  createdAt: string
}

export interface InvestigationBrief {
  transactionId: string
  summary: string
  recommendedActions: string[]
  provider: string
  generatedAt: string
}
