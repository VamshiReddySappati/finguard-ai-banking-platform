import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  Activity,
  ArrowRightLeft,
  Bot,
  CheckCircle2,
  CircleDollarSign,
  Clock3,
  Database,
  LogOut,
  RefreshCw,
  ShieldAlert,
  ShieldCheck,
  WalletCards,
  XCircle,
} from 'lucide-react'
import { api, clearSession, getSession, saveSession } from './api'
import type {
  Account,
  AuditEvent,
  AuthSession,
  FraudDecision,
  InvestigationBrief,
  Transaction,
  TransactionStatus,
} from './types'

const DEMO_DESTINATION = 'cccccccc-cccc-cccc-cccc-cccccccccccc'
const money = (value: number, currency = 'USD') =>
  new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(value)
const short = (id: string) => `${id.slice(0, 8)}…${id.slice(-4)}`
const date = (value: string) =>
  new Intl.DateTimeFormat('en-US', { dateStyle: 'medium', timeStyle: 'short' }).format(
    new Date(value),
  )

function Login({ onLogin }: { onLogin: (session: AuthSession) => void }) {
  const [email, setEmail] = useState('customer@finguard.dev')
  const [password, setPassword] = useState('Password123!')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const submit = async (event: React.FormEvent) => {
    event.preventDefault()
    setLoading(true)
    setError('')
    try {
      const session = await api.login(email, password)
      saveSession(session)
      onLogin(session)
    } catch (problem) {
      setError(problem instanceof Error ? problem.message : 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="login-shell">
      <section className="login-brand">
        <div className="brand-lock"><ShieldCheck size={26} /></div>
        <p className="eyebrow">EVENT-DRIVEN BANKING</p>
        <h1>Financial operations with explainable risk controls.</h1>
        <p>
          FinGuard AI demonstrates secure microservices, Kafka workflows, idempotent
          ledger posting and an evidence-grounded fraud review process.
        </p>
        <div className="feature-grid">
          <span><Activity />Real-time events</span>
          <span><ShieldAlert />Fraud decisions</span>
          <span><Bot />AI case briefs</span>
        </div>
      </section>
      <section className="login-card">
        <div>
          <p className="eyebrow">PORTFOLIO DEMO</p>
          <h2>Sign in to FinGuard</h2>
          <p className="muted">Use either seeded account. This project uses synthetic data only.</p>
        </div>
        <form onSubmit={submit}>
          <label>Email<input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required /></label>
          <label>Password<input value={password} onChange={(e) => setPassword(e.target.value)} type="password" required /></label>
          {error && <div className="error">{error}</div>}
          <button className="primary" disabled={loading}>{loading ? 'Signing in…' : 'Secure sign in'}</button>
        </form>
        <div className="demo-buttons">
          <button onClick={() => { setEmail('customer@finguard.dev'); setPassword('Password123!') }}>Customer demo</button>
          <button onClick={() => { setEmail('admin@finguard.dev'); setPassword('Admin123!') }}>Fraud analyst demo</button>
        </div>
      </section>
    </main>
  )
}

function Status({ status }: { status: TransactionStatus }) {
  const icon = status === 'COMPLETED'
    ? <CheckCircle2 />
    : status === 'REJECTED' || status === 'FAILED'
      ? <XCircle />
      : status === 'FLAGGED'
        ? <ShieldAlert />
        : <Clock3 />
  return <span className={`status ${status.toLowerCase()}`}>{icon}{status.replace('_', ' ')}</span>
}

function CustomerDashboard() {
  const [accounts, setAccounts] = useState<Account[]>([])
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [source, setSource] = useState('')
  const [destination, setDestination] = useState(DEMO_DESTINATION)
  const [amount, setAmount] = useState('250')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  const load = useCallback(async () => {
    try {
      const [loadedAccounts, loadedTransactions] = await Promise.all([
        api.accounts(),
        api.transactions(),
      ])
      setAccounts(loadedAccounts)
      setTransactions(loadedTransactions)
      setSource((current) => current || loadedAccounts[0]?.id || '')
      setError('')
    } catch (problem) {
      setError(problem instanceof Error ? problem.message : 'Could not load dashboard')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void load()
    const timer = setInterval(() => void load(), 3000)
    return () => clearInterval(timer)
  }, [load])

  const total = accounts.reduce((sum, account) => sum + Number(account.balance), 0)
  const pending = transactions.filter((transaction) =>
    ['PENDING_REVIEW', 'PROCESSING', 'FLAGGED'].includes(transaction.status),
  ).length

  const transfer = async (event: React.FormEvent) => {
    event.preventDefault()
    setMessage('')
    setError('')
    try {
      const transaction = await api.transfer(source, destination, Number(amount))
      setMessage(`Transfer ${short(transaction.id)} submitted for fraud review.`)
      setAmount('250')
      await load()
    } catch (problem) {
      setError(problem instanceof Error ? problem.message : 'Transfer failed')
    }
  }

  return (
    <>
      <div className="metric-grid">
        <article><div><span>Total available</span><strong>{money(total)}</strong></div><CircleDollarSign /></article>
        <article><div><span>Open workflows</span><strong>{pending}</strong></div><Activity /></article>
        <article><div><span>Completed transfers</span><strong>{transactions.filter((t) => t.status === 'COMPLETED').length}</strong></div><ShieldCheck /></article>
      </div>
      <div className="content-grid">
        <section className="panel">
          <div className="panel-title"><div><p className="eyebrow">ACCOUNTS</p><h3>Portfolio balances</h3></div><WalletCards /></div>
          <div className="accounts">
            {accounts.map((account) => (
              <article className="account-card" key={account.id}>
                <span>{account.currency} account</span>
                <strong>{money(Number(account.balance), account.currency)}</strong>
                <small>{account.accountNumber}</small>
              </article>
            ))}
          </div>
        </section>
        <section className="panel">
          <div className="panel-title"><div><p className="eyebrow">NEW TRANSFER</p><h3>Send through risk review</h3></div><ArrowRightLeft /></div>
          <form className="transfer-form" onSubmit={transfer}>
            <label>From account<select value={source} onChange={(e) => setSource(e.target.value)}>{accounts.map((account) => <option key={account.id} value={account.id}>{account.accountNumber} · {money(Number(account.balance), account.currency)}</option>)}</select></label>
            <label>Destination account ID<input value={destination} onChange={(e) => setDestination(e.target.value)} required /></label>
            <label>Amount<input value={amount} onChange={(e) => setAmount(e.target.value)} min="0.01" step="0.01" type="number" required /></label>
            <button className="primary">Submit transfer</button>
            {message && <div className="success">{message}</div>}
            {error && <div className="error">{error}</div>}
          </form>
          <p className="hint">Try $250 for approval, $5,000 for manual review, or over $10,000 for rejection.</p>
        </section>
      </div>
      <section className="panel table-panel">
        <div className="panel-title"><div><p className="eyebrow">LIVE WORKFLOW</p><h3>Recent transactions</h3></div><button className="icon-button" onClick={() => void load()} aria-label="Refresh"><RefreshCw /></button></div>
        {loading ? <p className="muted">Loading events…</p> : <TransactionTable transactions={transactions} />}
      </section>
    </>
  )
}

function TransactionTable({ transactions }: { transactions: Transaction[] }) {
  return (
    <div className="table-wrap"><table>
      <thead><tr><th>Transaction</th><th>Amount</th><th>Status</th><th>Destination</th><th>Updated</th></tr></thead>
      <tbody>{transactions.length === 0
        ? <tr><td colSpan={5} className="empty">No transfers yet.</td></tr>
        : transactions.map((transaction) => (
          <tr key={transaction.id}>
            <td><code>{short(transaction.id)}</code>{transaction.failureReason && <small className="reason">{transaction.failureReason}</small>}</td>
            <td>{money(Number(transaction.amount), transaction.currency)}</td>
            <td><Status status={transaction.status} /></td>
            <td><code>{short(transaction.destinationAccountId)}</code></td>
            <td>{date(transaction.updatedAt || transaction.createdAt)}</td>
          </tr>
        ))}</tbody>
    </table></div>
  )
}

function AdminDashboard() {
  const [fraud, setFraud] = useState<FraudDecision[]>([])
  const [audit, setAudit] = useState<AuditEvent[]>([])
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [brief, setBrief] = useState<InvestigationBrief | null>(null)
  const [generating, setGenerating] = useState<string | null>(null)
  const [error, setError] = useState('')

  const load = useCallback(async () => {
    try {
      const [loadedFraud, loadedAudit, loadedTransactions] = await Promise.all([
        api.fraud(),
        api.audit(),
        api.allTransactions(),
      ])
      setFraud(loadedFraud)
      setAudit(loadedAudit)
      setTransactions(loadedTransactions)
      setError('')
    } catch (problem) {
      setError(problem instanceof Error ? problem.message : 'Could not load analyst dashboard')
    }
  }, [])

  useEffect(() => {
    void load()
    const timer = setInterval(() => void load(), 4000)
    return () => clearInterval(timer)
  }, [load])

  const resolve = async (id: string, decision: 'APPROVED' | 'REJECTED') => {
    try {
      await api.resolveFraud(id, decision, `${decision} during portfolio demonstration`)
      await load()
    } catch (problem) {
      setError(problem instanceof Error ? problem.message : 'Resolution failed')
    }
  }

  const investigate = async (decision: FraudDecision) => {
    const transaction = transactions.find((item) => item.id === decision.transactionId)
    setGenerating(decision.transactionId)
    setError('')
    try {
      const result = await api.investigate({
        transactionId: decision.transactionId,
        amount: Number(transaction?.amount || 0),
        currency: transaction?.currency || 'USD',
        status: transaction?.status || decision.decision,
        riskScore: decision.riskScore,
        reasons: decision.reasons,
        eventTypes: audit
          .filter((event) => event.transactionId === decision.transactionId)
          .map((event) => event.eventType),
      })
      setBrief(result)
    } catch (problem) {
      setError(problem instanceof Error ? problem.message : 'Could not generate case brief')
    } finally {
      setGenerating(null)
    }
  }

  return (
    <>
      <div className="metric-grid">
        <article><div><span>Flagged reviews</span><strong>{fraud.filter((item) => item.decision === 'FLAGGED').length}</strong></div><ShieldAlert /></article>
        <article><div><span>Risk decisions</span><strong>{fraud.length}</strong></div><ShieldCheck /></article>
        <article><div><span>Audit events</span><strong>{audit.length}</strong></div><Database /></article>
      </div>
      {error && <div className="error page-error">{error}</div>}
      {brief && (
        <section className="panel ai-brief">
          <div className="panel-title"><div><p className="eyebrow">AI INVESTIGATION BRIEF</p><h3>Evidence-grounded analyst support</h3></div><Bot /></div>
          <p>{brief.summary}</p>
          <ol>{brief.recommendedActions.map((action) => <li key={action}>{action}</li>)}</ol>
          <small>Provider: {brief.provider} · Generated {date(brief.generatedAt)} · Human approval remains required.</small>
        </section>
      )}
      <section className="panel table-panel">
        <div className="panel-title"><div><p className="eyebrow">FRAUD OPERATIONS</p><h3>Decision queue</h3></div><button className="icon-button" onClick={() => void load()}><RefreshCw /></button></div>
        <div className="table-wrap"><table>
          <thead><tr><th>Transaction</th><th>Decision</th><th>Risk</th><th>Reason</th><th>Action</th></tr></thead>
          <tbody>{fraud.map((item) => (
            <tr key={item.transactionId}>
              <td><code>{short(item.transactionId)}</code></td>
              <td><span className={`decision ${item.decision.toLowerCase()}`}>{item.decision}</span></td>
              <td><div className="risk"><span style={{ width: `${item.riskScore}%` }} /></div><small>{item.riskScore}/100</small></td>
              <td>{item.reasons.join(', ')}</td>
              <td><div className="row-actions">
                <button onClick={() => void investigate(item)} disabled={generating === item.transactionId}>{generating === item.transactionId ? 'Generating…' : 'AI brief'}</button>
                {item.decision === 'FLAGGED' && <>
                  <button onClick={() => void resolve(item.transactionId, 'APPROVED')}>Approve</button>
                  <button className="danger" onClick={() => void resolve(item.transactionId, 'REJECTED')}>Reject</button>
                </>}
              </div></td>
            </tr>
          ))}</tbody>
        </table></div>
      </section>
      <section className="panel table-panel">
        <div className="panel-title"><div><p className="eyebrow">AUDIT STREAM</p><h3>Latest domain events</h3></div><Database /></div>
        <div className="audit-list">{audit.slice(0, 30).map((event) => (
          <article key={event.id}><span className="event-dot" /><div><strong>{event.eventType.replaceAll('_', ' ')}</strong><p><code>{short(event.transactionId)}</code> · {date(event.createdAt)}</p></div></article>
        ))}</div>
      </section>
    </>
  )
}

export default function App() {
  const [session, setSession] = useState<AuthSession | null>(() => getSession())
  const isAdmin = useMemo(() => session?.roles.includes('ADMIN') ?? false, [session])

  if (!session) return <Login onLogin={setSession} />

  const logout = () => {
    clearSession()
    setSession(null)
  }

  return (
    <div className="app">
      <header>
        <div className="logo"><div className="brand-lock"><ShieldCheck /></div><div><strong>FinGuard AI</strong><span>Banking control plane</span></div></div>
        <div className="user"><div><strong>{session.email}</strong><span>{isAdmin ? 'Fraud analyst' : 'Digital banking customer'}</span></div><button className="icon-button" onClick={logout} aria-label="Log out"><LogOut /></button></div>
      </header>
      <main className="dashboard">
        <div className="welcome"><div><p className="eyebrow">{isAdmin ? 'RISK COMMAND CENTER' : 'CUSTOMER OVERVIEW'}</p><h1>{isAdmin ? 'Review risk with a complete event trail.' : 'Move money with visible controls.'}</h1><p>{isAdmin ? 'Resolve flagged transfers, generate evidence-grounded briefs and inspect every event.' : 'Transfers are evaluated asynchronously and balances update only after approval.'}</p></div><div className="live"><span />Services polling live</div></div>
        {isAdmin ? <AdminDashboard /> : <CustomerDashboard />}
      </main>
    </div>
  )
}
