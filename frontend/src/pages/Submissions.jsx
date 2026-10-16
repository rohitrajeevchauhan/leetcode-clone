
import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'
import {
    Search,
    RefreshCw,
    Code2,
    CheckCircle2,
    XCircle,
    Clock,
    ChevronLeft,
    ChevronRight,
    ExternalLink,
    Filter,
    AlertCircle,
    Loader2,
} from 'lucide-react'

const API = '/api'
const PAGE_SIZE = 10

const STATUSES = [
    'ALL',
    'ACCEPTED',
    'WRONG_ANSWER',
    'COMPILATION_ERROR',
    'RUNTIME_ERROR',
    'TIME_LIMIT_EXCEEDED',
    'MEMORY_LIMIT_EXCEEDED',
    'PENDING',
    'RUNNING',
]

const LANGUAGES = [
    'ALL',
    'JAVA',
    'CPP',
    'PYTHON',
    'JAVASCRIPT',
]

function getToken() {
    return localStorage.getItem('token') || ''
}

function getStatusClass(status = '') {
    switch (status.toUpperCase()) {
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

function getStatusIcon(status = '') {
    if (status === 'ACCEPTED') return CheckCircle2
    if (['PENDING', 'RUNNING'].includes(status)) return Clock
    if (
        [
            'WRONG_ANSWER',
            'COMPILATION_ERROR',
            'RUNTIME_ERROR',
            'TIME_LIMIT_EXCEEDED',
            'MEMORY_LIMIT_EXCEEDED',
        ].includes(status)
    ) {
        return XCircle
    }
    return Code2
}

function formatDate(value) {
    if (!value) return '—'

    const date = new Date(value)

    return Number.isNaN(date.getTime())
        ? String(value)
        : date.toLocaleString()
}

function formatLanguage(language) {
    const names = {
        JAVA: 'Java',
        CPP: 'C++',
        PYTHON: 'Python',
        JAVASCRIPT: 'JavaScript',
    }

    return names[String(language || '').toUpperCase()] || language || '—'
}

function getProblemId(submission) {
    return (
        submission.problemId ??
        submission.problem?.id ??
        submission.problem?.problemId ??
        null
    )
}

function getProblemTitle(submission) {
    return (
        submission.problemTitle ||
        submission.problem?.title ||
        submission.problem?.name ||
        (getProblemId(submission)
            ? `Problem #${getProblemId(submission)}`
            : 'Problem')
    )
}

function getErrorMessage(error) {
    if (error.response?.status === 401 || error.response?.status === 403) {
        return 'Your session may have expired. Please log in again.'
    }

    return (
        error.response?.data?.message ||
        error.response?.data?.error ||
        error.message ||
        'Could not load submissions.'
    )
}

function StatCard({ icon: Icon, label, value, color }) {
    return (
        <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-5">
            <div className="flex items-center justify-between gap-3">
                <div>
                    <p className="text-sm text-slate-400">{label}</p>
                    <p className="mt-2 text-2xl font-bold text-white">
                        {value}
                    </p>
                </div>

                <div className={`rounded-xl bg-slate-800 p-3 ${color}`}>
                    <Icon size={22} />
                </div>
            </div>
        </div>
    )
}

function Submissions() {
    const [submissions, setSubmissions] = useState([])
    const [page, setPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [totalElements, setTotalElements] = useState(0)

    const [search, setSearch] = useState('')
    const [statusFilter, setStatusFilter] = useState('ALL')
    const [languageFilter, setLanguageFilter] = useState('ALL')

    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [refreshKey, setRefreshKey] = useState(0)

    useEffect(() => {
        let active = true

        async function loadSubmissions() {
            setLoading(true)
            setError('')

            try {
                const response = await axios.get(`${API}/submissions`, {
                    params: {
                        page,
                        size: PAGE_SIZE,
                        sort: 'createdAt,desc',
                    },
                    headers: {
                        Authorization: `Bearer ${getToken()}`,
                    },
                })

                if (!active) return

                const data = response.data
                const items = Array.isArray(data)
                    ? data
                    : data?.content || data?.submissions || []

                setSubmissions(items)
                setTotalPages(
                    Array.isArray(data) ? 1 : data?.totalPages ?? 0,
                )
                setTotalElements(
                    Array.isArray(data)
                        ? items.length
                        : data?.totalElements ?? items.length,
                )
            } catch (err) {
                if (active) {
                    setError(getErrorMessage(err))
                    setSubmissions([])
                }
            } finally {
                if (active) setLoading(false)
            }
        }

        loadSubmissions()

        return () => {
            active = false
        }
    }, [page, refreshKey])

    const filteredSubmissions = useMemo(() => {
        const query = search.trim().toLowerCase()

        return submissions.filter((submission) => {
            const status = String(submission.status || '').toUpperCase()
            const language = String(
                submission.language || '',
            ).toUpperCase()

            const title = getProblemTitle(submission).toLowerCase()
            const id = String(submission.id ?? '')

            const matchesSearch =
                !query ||
                title.includes(query) ||
                id.includes(query)

            const matchesStatus =
                statusFilter === 'ALL' || status === statusFilter

            const matchesLanguage =
                languageFilter === 'ALL' || language === languageFilter

            return (
                matchesSearch &&
                matchesStatus &&
                matchesLanguage
            )
        })
    }, [submissions, search, statusFilter, languageFilter])

    const acceptedCount = submissions.filter(
        (item) => item.status === 'ACCEPTED',
    ).length

    const acceptanceRate = submissions.length
        ? Math.round((acceptedCount / submissions.length) * 100)
        : 0

    function clearFilters() {
        setSearch('')
        setStatusFilter('ALL')
        setLanguageFilter('ALL')
        setPage(0)
    }

    function refreshSubmissions() {
        setRefreshKey((value) => value + 1)
    }

    return (
        <main className="min-h-screen bg-[#080b14] px-4 py-8 text-slate-100 md:px-8">
            <div className="mx-auto max-w-7xl">
                {/* Header */}
                <div className="mb-8 flex flex-wrap items-center justify-between gap-4">
                    <div>
                        <div className="mb-2 flex items-center gap-2 text-sm text-violet-400">
                            <Code2 size={17} />
                            Coding activity
                        </div>

                        <h1 className="text-3xl font-bold tracking-tight sm:text-4xl">
                            My Submissions
                        </h1>

                        <p className="mt-2 text-sm text-slate-400 sm:text-base">
                            Review your code submissions, verdicts, and progress.
                        </p>
                    </div>

                    <div className="flex gap-3">
                        <button
                            onClick={refreshSubmissions}
                            disabled={loading}
                            className="inline-flex items-center gap-2 rounded-xl border border-slate-700 bg-slate-900 px-4 py-2.5 text-sm font-medium transition hover:bg-slate-800 disabled:opacity-50"
                        >
                            <RefreshCw
                                size={16}
                                className={loading ? 'animate-spin' : ''}
                            />
                            Refresh
                        </button>

                        <Link
                            to="/problems"
                            className="inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-violet-600 to-indigo-600 px-4 py-2.5 text-sm font-semibold transition hover:from-violet-500 hover:to-indigo-500"
                        >
                            <Code2 size={16} />
                            Browse Problems
                        </Link>
                    </div>
                </div>

                {/* Summary cards */}
                <div className="mb-8 grid grid-cols-1 gap-4 sm:grid-cols-3">
                    <StatCard
                        icon={Code2}
                        label="Submissions on this page"
                        value={submissions.length}
                        color="text-violet-400"
                    />

                    <StatCard
                        icon={CheckCircle2}
                        label="Accepted on this page"
                        value={acceptedCount}
                        color="text-emerald-400"
                    />

                    <StatCard
                        icon={CheckCircle2}
                        label="Acceptance rate on this page"
                        value={`${acceptanceRate}%`}
                        color="text-cyan-400"
                    />
                </div>

                {/* Filters */}
                <section className="mb-6 rounded-2xl border border-slate-800 bg-[#0d1220] p-4 sm:p-5">
                    <div className="mb-4 flex items-center gap-2">
                        <Filter size={17} className="text-violet-400" />
                        <h2 className="font-semibold">Filter submissions</h2>
                    </div>

                    <div className="grid grid-cols-1 gap-3 md:grid-cols-4">
                        <div className="relative md:col-span-2">
                            <Search
                                size={17}
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500"
                            />

                            <input
                                type="text"
                                value={search}
                                onChange={(event) => setSearch(event.target.value)}
                                placeholder="Search problem or submission ID..."
                                className="w-full rounded-xl border border-slate-700 bg-[#090d17] py-3 pl-10 pr-4 text-sm text-slate-200 outline-none placeholder:text-slate-500 focus:border-violet-500"
                            />
                        </div>

                        <select
                            value={statusFilter}
                            onChange={(event) => setStatusFilter(event.target.value)}
                            className="rounded-xl border border-slate-700 bg-[#090d17] px-3 py-3 text-sm text-slate-200 outline-none focus:border-violet-500"
                        >
                            {STATUSES.map((status) => (
                                <option key={status} value={status}>
                                    {status === 'ALL'
                                        ? 'All verdicts'
                                        : status.replaceAll('_', ' ')}
                                </option>
                            ))}
                        </select>

                        <select
                            value={languageFilter}
                            onChange={(event) => setLanguageFilter(event.target.value)}
                            className="rounded-xl border border-slate-700 bg-[#090d17] px-3 py-3 text-sm text-slate-200 outline-none focus:border-violet-500"
                        >
                            {LANGUAGES.map((language) => (
                                <option key={language} value={language}>
                                    {language === 'ALL'
                                        ? 'All languages'
                                        : formatLanguage(language)}
                                </option>
                            ))}
                        </select>
                    </div>

                    {(search || statusFilter !== 'ALL' || languageFilter !== 'ALL') && (
                        <button
                            onClick={clearFilters}
                            className="mt-3 text-sm text-violet-400 transition hover:text-violet-300"
                        >
                            Clear all filters
                        </button>
                    )}
                </section>

                {/* Error */}
                {error && (
                    <div className="mb-6 flex items-start gap-3 rounded-xl border border-rose-500/30 bg-rose-500/10 p-4 text-rose-300">
                        <AlertCircle size={20} className="mt-0.5 shrink-0" />

                        <div className="flex-1">
                            <p className="font-semibold">
                                Unable to load submissions
                            </p>
                            <p className="mt-1 text-sm">{error}</p>

                            {(error.includes('session') ||
                                error.includes('log in')) && (
                                <Link
                                    to="/login"
                                    className="mt-3 inline-block text-sm font-semibold underline"
                                >
                                    Go to login
                                </Link>
                            )}

                            <button
                                onClick={refreshSubmissions}
                                className="ml-4 text-sm font-semibold underline"
                            >
                                Try again
                            </button>
                        </div>
                    </div>
                )}

                {/* Table */}
                <section className="overflow-hidden rounded-2xl border border-slate-800 bg-[#0d1220]">
                    <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-800 px-5 py-4">
                        <h2 className="font-semibold">Submission history</h2>

                        <span className="text-xs text-slate-500">
                            {totalElements} total submissions
                        </span>
                    </div>

                    {loading ? (
                        <div className="flex min-h-64 items-center justify-center gap-3 text-slate-400">
                            <Loader2
                                size={22}
                                className="animate-spin text-violet-400"
                            />
                            Loading submissions...
                        </div>
                    ) : !error && filteredSubmissions.length === 0 ? (
                        <div className="flex min-h-72 flex-col items-center justify-center px-5 text-center">
                            <div className="mb-4 rounded-2xl border border-slate-800 bg-slate-900 p-4">
                                <Code2 size={30} className="text-slate-500" />
                            </div>

                            <h3 className="text-lg font-semibold">
                                {submissions.length === 0
                                    ? 'No submissions yet'
                                    : 'No matching submissions'}
                            </h3>

                            <p className="mt-2 max-w-md text-sm leading-6 text-slate-400">
                                {submissions.length === 0
                                    ? 'Solve a problem and submit your code to see your history here.'
                                    : 'Try changing your search or filters to find a submission.'}
                            </p>

                            {submissions.length === 0 ? (
                                <Link
                                    to="/problems"
                                    className="mt-5 rounded-xl bg-violet-600 px-4 py-2.5 text-sm font-semibold hover:bg-violet-500"
                                >
                                    Start solving
                                </Link>
                            ) : (
                                <button
                                    onClick={clearFilters}
                                    className="mt-5 rounded-xl border border-slate-700 px-4 py-2.5 text-sm hover:bg-slate-800"
                                >
                                    Clear filters
                                </button>
                            )}
                        </div>
                    ) : !error ? (
                        <>
                            <div className="overflow-x-auto">
                                <table className="w-full min-w-[850px] text-left text-sm">
                                    <thead className="bg-slate-900/70 text-xs uppercase tracking-wider text-slate-500">
                                    <tr>
                                        <th className="px-5 py-4">Problem</th>
                                        <th className="px-5 py-4">Verdict</th>
                                        <th className="px-5 py-4">Language</th>
                                        <th className="px-5 py-4">Test cases</th>
                                        <th className="px-5 py-4">Runtime</th>
                                        <th className="px-5 py-4">Submitted</th>
                                        <th className="px-5 py-4">Details</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    {filteredSubmissions.map((submission) => {
                                        const status = String(
                                            submission.status || 'UNKNOWN',
                                        ).toUpperCase()

                                        const StatusIcon = getStatusIcon(status)
                                        const problemId = getProblemId(submission)

                                        return (
                                            <tr
                                                key={submission.id}
                                                className="border-t border-slate-800/80 transition hover:bg-slate-900/60"
                                            >
                                                <td className="px-5 py-4">
                                                    {problemId ? (
                                                        <Link
                                                            to={`/problems/${problemId}`}
                                                            className="font-medium text-slate-200 transition hover:text-violet-400"
                                                        >
                                                            {getProblemTitle(submission)}
                                                        </Link>
                                                    ) : (
                                                        <span className="font-medium text-slate-200">
                                                                {getProblemTitle(submission)}
                                                            </span>
                                                    )}

                                                    <p className="mt-1 text-xs text-slate-500">
                                                        Submission #{submission.id}
                                                    </p>
                                                </td>

                                                <td className="px-5 py-4">
                                                        <span
                                                            className={`inline-flex items-center gap-1.5 whitespace-nowrap rounded-full border px-2.5 py-1.5 text-xs font-semibold ${getStatusClass(status)}`}
                                                        >
                                                            <StatusIcon size={13} />
                                                            {status.replaceAll('_', ' ')}
                                                        </span>
                                                </td>

                                                <td className="px-5 py-4 text-slate-300">
                                                    {formatLanguage(submission.language)}
                                                </td>

                                                <td className="px-5 py-4 text-slate-300">
                                                    {submission.passedTestCases ?? 0}
                                                    {' / '}
                                                    {submission.totalTestCases ?? 0}
                                                </td>

                                                <td className="px-5 py-4 text-slate-400">
                                                    {submission.executionTime != null
                                                        ? `${submission.executionTime} ms`
                                                        : '—'}
                                                </td>

                                                <td className="whitespace-nowrap px-5 py-4 text-xs text-slate-400">
                                                    {formatDate(submission.createdAt)}
                                                </td>

                                                <td className="px-5 py-4">
                                                    <Link
                                                        to={`/submissions/${submission.id}`}
                                                        className="inline-flex items-center gap-1.5 rounded-lg border border-slate-700 px-3 py-2 text-xs font-semibold text-violet-300 transition hover:border-violet-500/50 hover:bg-violet-500/10"
                                                    >
                                                        View
                                                        <ExternalLink size={13} />
                                                    </Link>
                                                </td>
                                            </tr>
                                        )
                                    })}
                                    </tbody>
                                </table>
                            </div>

                            {/* Pagination */}
                            <div className="flex flex-wrap items-center justify-between gap-4 border-t border-slate-800 px-5 py-4">
                                <p className="text-sm text-slate-400">
                                    Page {page + 1}
                                    {totalPages > 0 ? ` of ${totalPages}` : ''}
                                    <span className="ml-2 text-slate-600">
                                        ({filteredSubmissions.length} shown on this page)
                                    </span>
                                </p>

                                <div className="flex items-center gap-2">
                                    <button
                                        disabled={page === 0 || loading}
                                        onClick={() =>
                                            setPage((current) => Math.max(0, current - 1))
                                        }
                                        className="inline-flex items-center gap-1 rounded-lg border border-slate-700 px-3 py-2 text-sm transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-40"
                                    >
                                        <ChevronLeft size={16} />
                                        Previous
                                    </button>

                                    <button
                                        disabled={
                                            loading ||
                                            totalPages === 0 ||
                                            page + 1 >= totalPages
                                        }
                                        onClick={() =>
                                            setPage((current) =>
                                                Math.min(totalPages - 1, current + 1),
                                            )
                                        }
                                        className="inline-flex items-center gap-1 rounded-lg border border-slate-700 px-3 py-2 text-sm transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-40"
                                    >
                                        Next
                                        <ChevronRight size={16} />
                                    </button>
                                </div>
                            </div>
                        </>
                    ) : null}
                </section>
            </div>
        </main>
    )
}

export default Submissions
