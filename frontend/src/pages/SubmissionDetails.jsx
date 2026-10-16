import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import axios from 'axios'
import Editor from '@monaco-editor/react'
import {
    ArrowLeft,
    CheckCircle2,
    XCircle,
    Clock,
    Code2,
    Copy,
    Check,
    RefreshCw,
    AlertCircle,
    MemoryStick,
    FileCode2,
    ExternalLink,
    Loader2,
} from 'lucide-react'

const API = '/api'

function getToken() {
    return localStorage.getItem('token') || ''
}

function getLanguage(language) {
    const languages = {
        JAVA: 'java',
        CPP: 'cpp',
        PYTHON: 'python',
        JAVASCRIPT: 'javascript',
    }

    return languages[String(language || '').toUpperCase()] || 'plaintext'
}

function formatLanguage(language) {
    const languages = {
        JAVA: 'Java',
        CPP: 'C++',
        PYTHON: 'Python',
        JAVASCRIPT: 'JavaScript',
    }

    return languages[String(language || '').toUpperCase()] || language || 'Unknown'
}

function formatDate(value) {
    if (!value) return '—'

    const date = new Date(value)

    return Number.isNaN(date.getTime())
        ? String(value)
        : date.toLocaleString()
}

function formatDuration(value) {
    if (value == null || value === '') return '—'
    return `${value} ms`
}

function getStatusStyle(status = '') {
    switch (String(status).toUpperCase()) {
        case 'ACCEPTED':
            return 'border-emerald-500/30 bg-emerald-500/10 text-emerald-400'
        case 'PENDING':
        case 'RUNNING':
            return 'border-amber-500/30 bg-amber-500/10 text-amber-300'
        case 'WRONG_ANSWER':
        case 'COMPILATION_ERROR':
        case 'RUNTIME_ERROR':
        case 'TIME_LIMIT_EXCEEDED':
        case 'MEMORY_LIMIT_EXCEEDED':
            return 'border-rose-500/30 bg-rose-500/10 text-rose-400'
        default:
            return 'border-slate-700 bg-slate-800 text-slate-300'
    }
}

function StatusBadge({ status }) {
    const value = String(status || 'UNKNOWN').toUpperCase()
    const accepted = value === 'ACCEPTED'
    const pending = value === 'PENDING' || value === 'RUNNING'
    const Icon = accepted ? CheckCircle2 : pending ? Clock : XCircle

    return (
        <span
            className={`inline-flex items-center gap-2 rounded-full border px-3 py-1.5 text-xs font-bold ${getStatusStyle(value)}`}
        >
            <Icon size={15} />
            {value.replaceAll('_', ' ')}
        </span>
    )
}

function MetricCard({ icon: Icon, label, value, color }) {
    return (
        <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-5">
            <div className="flex items-center justify-between gap-3">
                <p className="text-sm text-slate-400">{label}</p>
                <div className={`rounded-xl bg-slate-800 p-2.5 ${color}`}>
                    <Icon size={19} />
                </div>
            </div>

            <p className="mt-4 break-words text-2xl font-bold text-white">
                {value}
            </p>
        </div>
    )
}

function OutputBlock({ label, value }) {
    return (
        <div className="min-w-0">
            <p className="mb-2 text-xs font-semibold uppercase tracking-wide text-slate-500">
                {label}
            </p>

            <pre className="min-h-12 overflow-auto whitespace-pre-wrap break-words rounded-xl border border-slate-800 bg-[#080b14] p-3 font-mono text-sm leading-6 text-slate-200">
                {value == null || value === '' ? '—' : String(value)}
            </pre>
        </div>
    )
}

export default function SubmissionDetails() {
    const { id } = useParams()

    const [submission, setSubmission] = useState(null)
    const [loading, setLoading] = useState(true)
    const [refreshing, setRefreshing] = useState(false)
    const [error, setError] = useState('')
    const [copied, setCopied] = useState(false)

    async function loadSubmission(isRefresh = false) {
        if (isRefresh) {
            setRefreshing(true)
        } else {
            setLoading(true)
        }

        setError('')

        try {
            const response = await axios.get(`${API}/submissions/${id}`, {
                headers: {
                    Authorization: `Bearer ${getToken()}`,
                },
            })

            setSubmission(response.data)
        } catch (err) {
            setError(
                err.response?.status === 401 ||
                err.response?.status === 403
                    ? 'You are not authorized to view this submission. Please log in again if your session expired.'
                    : err.response?.data?.message ||
                    err.response?.data?.error ||
                    'Could not load submission details.',
            )
        } finally {
            setLoading(false)
            setRefreshing(false)
        }
    }

    useEffect(() => {
        let active = true

        async function fetchSubmission() {
            setLoading(true)
            setError('')

            try {
                const response = await axios.get(`${API}/submissions/${id}`, {
                    headers: {
                        Authorization: `Bearer ${getToken()}`,
                    },
                })

                if (active) setSubmission(response.data)
            } catch (err) {
                if (active) {
                    setError(
                        err.response?.status === 401 ||
                        err.response?.status === 403
                            ? 'You are not authorized to view this submission. Please log in again if your session expired.'
                            : err.response?.data?.message ||
                            err.response?.data?.error ||
                            'Could not load submission details.',
                    )
                }
            } finally {
                if (active) setLoading(false)
            }
        }

        fetchSubmission()

        return () => {
            active = false
        }
    }, [id])

    async function copyCode() {
        if (!submission?.code) return

        try {
            await navigator.clipboard.writeText(submission.code)
            setCopied(true)
            setTimeout(() => setCopied(false), 1800)
        } catch {
            setError('Could not copy the code. Please select and copy it manually.')
        }
    }

    if (loading) {
        return (
            <main className="flex min-h-screen items-center justify-center gap-3 bg-[#080b14] text-slate-300">
                <Loader2 className="animate-spin text-violet-400" size={23} />
                Loading submission details...
            </main>
        )
    }

    if (error || !submission) {
        return (
            <main className="min-h-screen bg-[#080b14] px-4 py-10 text-slate-100">
                <div className="mx-auto max-w-2xl">
                    <div className="rounded-2xl border border-rose-500/30 bg-rose-500/5 p-6">
                        <AlertCircle className="mb-3 text-rose-400" size={28} />
                        <h1 className="text-xl font-bold">
                            Unable to load submission
                        </h1>
                        <p className="mt-3 text-sm leading-6 text-rose-200">
                            {error || 'Submission not found.'}
                        </p>

                        <div className="mt-6 flex flex-wrap gap-3">
                            <button
                                onClick={() => loadSubmission()}
                                className="rounded-xl bg-violet-600 px-4 py-2.5 text-sm font-semibold hover:bg-violet-500"
                            >
                                Try again
                            </button>

                            <Link
                                to="/submissions"
                                className="rounded-xl border border-slate-700 px-4 py-2.5 text-sm hover:bg-slate-900"
                            >
                                Back to submissions
                            </Link>
                        </div>
                    </div>
                </div>
            </main>
        )
    }

    const testCases = Array.isArray(submission.testCases)
        ? submission.testCases
        : []

    const status = String(submission.status || 'UNKNOWN').toUpperCase()
    const accepted = status === 'ACCEPTED'

    const passedCount =
        submission.passedTestCases ??
        testCases.filter((test) => test.passed).length

    const totalCount = submission.totalTestCases ?? testCases.length

    const problemId =
        submission.problemId ??
        submission.problem?.id ??
        submission.problem?.problemId

    const problemTitle =
        submission.problemTitle ||
        submission.problem?.title ||
        (problemId ? `Problem #${problemId}` : 'Submission details')

    return (
        <main className="min-h-screen bg-[#080b14] px-4 py-8 text-slate-100 sm:px-6 lg:px-8">
            <div className="mx-auto max-w-7xl">
                {/* Navigation */}
                <div className="mb-7 flex flex-wrap items-center justify-between gap-3">
                    <Link
                        to="/submissions"
                        className="inline-flex items-center gap-2 text-sm text-slate-400 transition hover:text-white"
                    >
                        <ArrowLeft size={17} />
                        All Submissions
                    </Link>

                    <button
                        onClick={() => loadSubmission(true)}
                        disabled={refreshing}
                        className="inline-flex items-center gap-2 rounded-xl border border-slate-700 bg-slate-900 px-4 py-2.5 text-sm transition hover:bg-slate-800 disabled:opacity-50"
                    >
                        <RefreshCw
                            size={15}
                            className={refreshing ? 'animate-spin' : ''}
                        />
                        Refresh
                    </button>
                </div>

                {/* Submission heading */}
                <section className="rounded-2xl border border-slate-800 bg-gradient-to-br from-[#11172a] to-[#0b1020] p-5 sm:p-7">
                    <div className="flex flex-wrap items-start justify-between gap-5">
                        <div>
                            <div className="mb-3 flex items-center gap-2 text-sm text-violet-400">
                                <Code2 size={17} />
                                Submission #{submission.id}
                            </div>

                            <h1 className="text-2xl font-bold sm:text-3xl">
                                {problemTitle}
                            </h1>

                            <p className="mt-3 text-sm text-slate-400">
                                Submitted on {formatDate(submission.createdAt)}
                            </p>
                        </div>

                        <StatusBadge status={status} />
                    </div>

                    <div className="mt-6 flex flex-wrap gap-3">
                        {problemId && (
                            <Link
                                to={`/problems/${problemId}`}
                                className="inline-flex items-center gap-2 rounded-xl bg-violet-600 px-4 py-2.5 text-sm font-semibold transition hover:bg-violet-500"
                            >
                                Try Problem Again
                                <ExternalLink size={15} />
                            </Link>
                        )}

                        {problemId && (
                            <Link
                                to={`/problems/${problemId}/submissions`}
                                className="rounded-xl border border-slate-700 px-4 py-2.5 text-sm font-medium transition hover:bg-slate-800"
                            >
                                Problem History
                            </Link>
                        )}
                    </div>
                </section>

                {/* Metrics */}
                <section className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
                    <MetricCard
                        icon={CheckCircle2}
                        label="Test Cases Passed"
                        value={`${passedCount} / ${totalCount}`}
                        color="text-emerald-400"
                    />

                    <MetricCard
                        icon={FileCode2}
                        label="Language"
                        value={formatLanguage(submission.language)}
                        color="text-violet-400"
                    />

                    <MetricCard
                        icon={Clock}
                        label="Execution Time"
                        value={submission.executionTime == null
                            ? '—'
                            : `${submission.executionTime} ms`}
                        color="text-cyan-400"
                    />

                    <MetricCard
                        icon={MemoryStick}
                        label="Memory Used"
                        value={submission.memoryUsed == null
                            ? '—'
                            : `${submission.memoryUsed} KB`}
                        color="text-amber-400"
                    />
                </section>

                {/* Error details */}
                {submission.errorMessage && (
                    <section className="mt-6 overflow-hidden rounded-2xl border border-rose-500/30 bg-rose-500/5">
                        <div className="flex items-center gap-2 border-b border-rose-500/20 px-5 py-4">
                            <AlertCircle size={18} className="text-rose-400" />
                            <h2 className="font-semibold text-rose-300">
                                Error / Judge Message
                            </h2>
                        </div>

                        <pre className="overflow-auto whitespace-pre-wrap break-words p-5 font-mono text-sm leading-6 text-rose-200">
                            {submission.errorMessage}
                        </pre>
                    </section>
                )}

                {/* Submitted source code */}
                <section className="mt-7 overflow-hidden rounded-2xl border border-slate-800 bg-[#0d1220]">
                    <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-800 px-5 py-4">
                        <div className="flex items-center gap-3">
                            <div className="rounded-lg bg-violet-500/10 p-2 text-violet-400">
                                <Code2 size={18} />
                            </div>

                            <div>
                                <h2 className="font-semibold">Submitted Code</h2>
                                <p className="mt-1 text-xs text-slate-500">
                                    {formatLanguage(submission.language)}
                                </p>
                            </div>
                        </div>

                        <button
                            onClick={copyCode}
                            disabled={!submission.code}
                            className="inline-flex items-center gap-2 rounded-lg border border-slate-700 px-3 py-2 text-sm transition hover:bg-slate-800 disabled:opacity-40"
                        >
                            {copied ? <Check size={15} /> : <Copy size={15} />}
                            {copied ? 'Copied' : 'Copy Code'}
                        </button>
                    </div>

                    <Editor
                        height="420px"
                        language={getLanguage(submission.language)}
                        theme="vs-dark"
                        value={submission.code || '// Source code is not available.'}
                        options={{
                            readOnly: true,
                            minimap: { enabled: false },
                            automaticLayout: true,
                            scrollBeyondLastLine: false,
                            wordWrap: 'on',
                            fontSize: 14,
                            lineNumbers: 'on',
                            renderLineHighlight: 'none',
                            padding: { top: 16, bottom: 16 },
                            domReadOnly: true,
                        }}
                    />
                </section>

                {/* Program output */}
                {submission.output != null && submission.output !== '' && (
                    <section className="mt-6 overflow-hidden rounded-2xl border border-slate-800 bg-[#0d1220]">
                        <div className="border-b border-slate-800 px-5 py-4">
                            <h2 className="font-semibold">Program Output</h2>
                        </div>

                        <pre className="overflow-auto whitespace-pre-wrap break-words p-5 font-mono text-sm leading-6 text-emerald-300">
                            {submission.output}
                        </pre>
                    </section>
                )}

                {/* Test case results */}
                <section className="mt-8">
                    <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
                        <div>
                            <h2 className="text-xl font-bold">Test Case Results</h2>
                            <p className="mt-1 text-sm text-slate-500">
                                Details returned by the judging service.
                            </p>
                        </div>

                        <span className="rounded-full border border-slate-700 bg-slate-900 px-3 py-1.5 text-xs font-semibold text-slate-300">
                            {testCases.length} test cases
                        </span>
                    </div>

                    {testCases.length === 0 ? (
                        <div className="rounded-2xl border border-slate-800 bg-slate-900/50 p-7 text-center">
                            <FileCode2 className="mx-auto mb-3 text-slate-500" size={27} />
                            <h3 className="font-semibold">
                                No individual test-case results
                            </h3>
                            <p className="mt-2 text-sm text-slate-400">
                                The backend did not return test-case details for this submission.
                            </p>
                        </div>
                    ) : (
                        <div className="space-y-4">
                            {testCases.map((test, index) => (
                                <article
                                    key={test.testCaseId ?? index}
                                    className="overflow-hidden rounded-2xl border border-slate-800 bg-[#0d1220]"
                                >
                                    <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-800 bg-slate-900/60 px-5 py-4">
                                        <div className="flex items-center gap-3">
                                            <div className={`rounded-lg p-2 ${
                                                test.passed
                                                    ? 'bg-emerald-500/10 text-emerald-400'
                                                    : 'bg-rose-500/10 text-rose-400'
                                            }`}>
                                                {test.passed
                                                    ? <CheckCircle2 size={18} />
                                                    : <XCircle size={18} />}
                                            </div>

                                            <div>
                                                <h3 className="font-semibold">
                                                    Test Case {index + 1}
                                                </h3>
                                                <p className="mt-1 text-xs text-slate-500">
                                                    {test.testCaseId != null
                                                        ? `ID: ${test.testCaseId}`
                                                        : 'Test result'}
                                                </p>
                                            </div>
                                        </div>

                                        <span className={`text-sm font-semibold ${
                                            test.passed
                                                ? 'text-emerald-400'
                                                : 'text-rose-400'
                                        }`}>
                                            {test.passed ? 'Passed' : 'Failed'}
                                        </span>
                                    </div>

                                    <div className="grid gap-5 p-5 md:grid-cols-2">
                                        <OutputBlock
                                            label="Input"
                                            value={test.input}
                                        />

                                        <OutputBlock
                                            label="Expected Output"
                                            value={test.expectedOutput}
                                        />

                                        <OutputBlock
                                            label="Actual Output"
                                            value={test.actualOutput}
                                        />

                                        {test.error && (
                                            <OutputBlock
                                                label="Error"
                                                value={test.error}
                                            />
                                        )}
                                    </div>

                                    {test.executionTime != null && (
                                        <div className="border-t border-slate-800 px-5 py-3 text-xs text-slate-400">
                                            Execution time: {test.executionTime} ms
                                        </div>
                                    )}
                                </article>
                            ))}
                        </div>
                    )}
                </section>

                {/* Footer navigation */}
                <div className="mt-8 flex flex-wrap gap-3 border-t border-slate-800 pt-6">
                    <Link
                        to="/submissions"
                        className="inline-flex items-center gap-2 rounded-xl border border-slate-700 px-4 py-3 text-sm font-medium transition hover:bg-slate-900"
                    >
                        <ArrowLeft size={16} />
                        All Submissions
                    </Link>

                    {problemId && (
                        <Link
                            to={`/problems/${problemId}`}
                            className="inline-flex items-center gap-2 rounded-xl bg-violet-600 px-4 py-3 text-sm font-semibold transition hover:bg-violet-500"
                        >
                            <Code2 size={16} />
                            Solve Again
                        </Link>
                    )}
                </div>
            </div>
        </main>
    )
}