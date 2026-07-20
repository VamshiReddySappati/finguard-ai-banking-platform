import type {
  Account,
  AuditEvent,
  AuthSession,
  FraudDecision,
  InvestigationBrief,
  Transaction,
} from './types'

const SESSION_KEY = 'finguard-session'

export const getSession = (): AuthSession | null => {
  const raw = localStorage.getItem(SESSION_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as AuthSession
  } catch {
    localStorage.removeItem(SESSION_KEY)
    return null
  }
}

export const saveSession = (session: AuthSession) =>
  localStorage.setItem(SESSION_KEY, JSON.stringify(session))

export const clearSession = () => localStorage.removeItem(SESSION_KEY)

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const session = getSession()
  const headers = new Headers(options.headers)
  headers.set('Content-Type', 'application/json')
  if (session) headers.set('Authorization', `Bearer ${session.accessToken}`)

  const response = await fetch(path, { ...options, headers })
  if (!response.ok) {
    let message = `Request failed (${response.status})`
    try {
      const body = (await response.json()) as { message?: string }
      message = body.message || message
    } catch {
      // Preserve the status-based message when the response is not JSON.
    }
    throw new Error(message)
  }
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}

export const api = {
  login: (email: string, password: string) =>
    request<AuthSession>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),
  accounts: () => request<Account[]>('/api/accounts'),
  transactions: () => request<Transaction[]>('/api/transactions'),
  allTransactions: () => request<Transaction[]>('/api/transactions/admin/all'),
  transfer: (sourceAccountId: string, destinationAccountId: string, amount: number) =>
    request<Transaction>('/api/transactions', {
      method: 'POST',
      headers: { 'Idempotency-Key': crypto.randomUUID() },
      body: JSON.stringify({ sourceAccountId, destinationAccountId, amount }),
    }),
  fraud: () => request<FraudDecision[]>('/api/fraud'),
  resolveFraud: (
    transactionId: string,
    decision: 'APPROVED' | 'REJECTED',
    note: string,
  ) =>
    request<FraudDecision>(`/api/fraud/${transactionId}/resolve`, {
      method: 'POST',
      body: JSON.stringify({ decision, note }),
    }),
  audit: () => request<AuditEvent[]>('/api/audit'),
  investigate: (payload: {
    transactionId: string
    amount: number
    currency: string
    status: string
    riskScore: number
    reasons: string[]
    eventTypes: string[]
  }) =>
    request<InvestigationBrief>('/api/investigations/brief', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
}
