import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'
import {
    Search,
    Code2,
    CheckCircle2,
    ArrowRight,
    SlidersHorizontal,
    RefreshCw,
    AlertCircle,
    Terminal,
    BookOpen,
} from 'lucide-react'

const API_URL = '/api/problems'

const getList = (data) => {
    if (Array.isArray(data)) return data
    if (Array.isArray(data?.content)) return data.content
    if (Array.isArray(data?.data)) return data.data
    return []
}

const difficultyClass = (difficulty) => {
    const value = String(difficulty || '').toUpperCase()

    if (value === 'EASY') {
        return 'border-emerald-400/20 bg-emerald-400/10 text-emerald-300'
    }

    if (value === 'MEDIUM') {
        return 'border-amber-400/20 bg-amber-400/10 text-amber-300'
    }

    if (value === 'HARD') {
        return 'border-rose-400/20 bg-rose-400/10 text-rose-300'
    }

    return 'border-slate-700 bg-slate-800 text-slate-400'
}

export default function Problems() {
    const [problems, setProblems] = useState([])
    const [search, setSearch] = useState('')
    const [difficulty, setDifficulty] = useState('ALL')
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    const loadProblems = async () => {
        setLoading(true)
        setError('')

        const token = localStorage.getItem('token')
        const config = token
            ? {
                headers: {
                    Authorization: 'Bearer ' + token,
                },
            }
            : {}

        try {
            const response = await axios.get(API_URL, config)
            setProblems(getList(response.data))
        } catch (err) {
            console.error('Failed to load problems:', err)

            setError(
                err.response?.data?.message ||
                'Unable to load problems. Check your backend connection and try again.'
            )
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        loadProblems()
    }, [])

    const filteredProblems = useMemo(() => {
        const query = search.trim().toLowerCase()

        return problems.filter((problem) => {
            const matchesSearch =
                !query ||
                String(problem.title || '').toLowerCase().includes(query) ||
                String(problem.description || '').toLowerCase().includes(query) ||
                String(problem.topic || '').toLowerCase().includes(query) ||
                String(problem.tags || '').toLowerCase().includes(query)

            const problemDifficulty = String(
                problem.difficulty || ''
            ).toUpperCase()

            const matchesDifficulty =
                difficulty === 'ALL' || problemDifficulty === difficulty

            return matchesSearch && matchesDifficulty
        })
    }, [problems, search, difficulty])

    const easyCount = problems.filter(
        (problem) => String(problem.difficulty).toUpperCase() === 'EASY'
    ).length

    const mediumCount = problems.filter(
        (problem) => String(problem.difficulty).toUpperCase() === 'MEDIUM'
    ).length

    const hardCount = problems.filter(
        (problem) => String(problem.difficulty).toUpperCase() === 'HARD'
    ).length

    return (
        <main className="min-h-screen bg-slate-950 px-4 py-8 text-white sm:px-6 lg:px-8">
            <div className="mx-auto max-w-7xl">
                {/* Header */}
                <section className="relative overflow-hidden rounded-3xl border border-slate-800 bg-gradient-to-br from-slate-900 via-slate-900 to-purple-950/40 p-6 sm:p-8">
                    <div className="pointer-events-none absolute -right-10 -top-16 h-64 w-64 rounded-full bg-purple-600/15 blur-[90px]" />

                    <div className="relative flex flex-col justify-between gap-6 md:flex-row md:items-center">
                        <div>
                            <div className="inline-flex items-center gap-2 rounded-full border border-cyan-400/20 bg-cyan-400/10 px-3 py-1.5 text-xs font-semibold uppercase tracking-wider text-cyan-300">
                                <Terminal size={14} />
                                Coding workspace
                            </div>

                            <h1 className="mt-5 text-3xl font-bold tracking-tight sm:text-4xl">
                                Problem <span className="text-cyan-300">Library</span>
                            </h1>

                            <p className="mt-3 max-w-2xl leading-7 text-slate-400">
                                Practice data structures, algorithms, and problem-solving.
                                Choose a challenge and keep improving your coding skills.
                            </p>
                        </div>

                        <div className="flex shrink-0 items-center gap-3 rounded-2xl border border-slate-700/70 bg-slate-950/50 p-4">
                            <div className="rounded-xl bg-purple-500/15 p-3 text-purple-300">
                                <BookOpen size={25} />
                            </div>

                            <div>
                                <p className="text-2xl font-bold">
                                    {loading ? '—' : problems.length}
                                </p>
                                <p className="text-xs text-slate-500">
                                    Available problems
                                </p>
                            </div>
                        </div>
                    </div>
                </section>

                {/* Difficulty statistics */}
                <section className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
                    {[
                        {
                            label: 'Easy',
                            count: easyCount,
                            color: 'text-emerald-300',
                            dot: 'bg-emerald-400',
                        },
                        {
                            label: 'Medium',
                            count: mediumCount,
                            color: 'text-amber-300',
                            dot: 'bg-amber-400',
                        },
                        {
                            label: 'Hard',
                            count: hardCount,
                            color: 'text-rose-300',
                            dot: 'bg-rose-400',
                        },
                    ].map((item) => (
                        <div
                            key={item.label}
                            className="flex items-center justify-between rounded-2xl border border-slate-800 bg-slate-900/70 p-5"
                        >
                            <div className="flex items-center gap-3">
                                <span className={'h-2.5 w-2.5 rounded-full ' + item.dot} />
                                <span className="text-sm text-slate-400">
                  {item.label}
                </span>
                            </div>

                            <span className={'text-2xl font-bold ' + item.color}>
                {loading ? '—' : item.count}
              </span>
                        </div>
                    ))}
                </section>

                {/* Search and filters */}
                <section className="mt-8 rounded-2xl border border-slate-800 bg-slate-900/70 p-4 sm:p-5">
                    <div className="flex flex-col gap-4 lg:flex-row lg:items-center">
                        <div className="relative flex-1">
                            <Search
                                size={19}
                                className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500"
                            />

                            <input
                                type="search"
                                value={search}
                                onChange={(event) => setSearch(event.target.value)}
                                placeholder="Search problems by title or description..."
                                aria-label="Search problems"
                                className="w-full rounded-xl border border-slate-700 bg-slate-950 py-3.5 pl-11 pr-4 text-sm text-white outline-none transition placeholder:text-slate-600 focus:border-cyan-400/50 focus:ring-2 focus:ring-cyan-400/10"
                            />
                        </div>

                        <div className="flex items-center gap-3">
                            <SlidersHorizontal
                                size={18}
                                className="shrink-0 text-slate-500"
                            />

                            <select
                                value={difficulty}
                                onChange={(event) => setDifficulty(event.target.value)}
                                aria-label="Filter by difficulty"
                                className="min-w-0 flex-1 rounded-xl border border-slate-700 bg-slate-950 px-4 py-3.5 text-sm text-slate-300 outline-none focus:border-purple-400/50 sm:min-w-44"
                            >
                                <option value="ALL">All difficulties</option>
                                <option value="EASY">Easy</option>
                                <option value="MEDIUM">Medium</option>
                                <option value="HARD">Hard</option>
                            </select>

                            <button
                                type="button"
                                onClick={loadProblems}
                                disabled={loading}
                                aria-label="Refresh problems"
                                className="rounded-xl border border-slate-700 bg-slate-950 p-3.5 text-slate-400 transition hover:border-cyan-400/30 hover:text-cyan-300 disabled:opacity-50"
                            >
                                <RefreshCw
                                    size={17}
                                    className={loading ? 'animate-spin' : ''}
                                />
                            </button>
                        </div>
                    </div>

                    <div className="mt-4 flex items-center justify-between gap-3 text-xs text-slate-500">
            <span>
              {loading
                  ? 'Loading problems...'
                  : 'Showing ' +
                  filteredProblems.length +
                  ' of ' +
                  problems.length +
                  ' problems'}
            </span>

                        {(search || difficulty !== 'ALL') && (
                            <button
                                type="button"
                                onClick={() => {
                                    setSearch('')
                                    setDifficulty('ALL')
                                }}
                                className="font-medium text-cyan-300 hover:text-cyan-200"
                            >
                                Clear filters
                            </button>
                        )}
                    </div>
                </section>

                {/* Error state */}
                {error && (
                    <div
                        role="alert"
                        className="mt-6 flex flex-col gap-4 rounded-2xl border border-red-500/30 bg-red-500/10 p-4 sm:flex-row sm:items-center sm:justify-between"
                    >
                        <div className="flex items-start gap-3 text-sm text-red-300">
                            <AlertCircle size={20} className="mt-0.5 shrink-0" />
                            <p>{error}</p>
                        </div>

                        <button
                            type="button"
                            onClick={loadProblems}
                            className="shrink-0 rounded-lg border border-red-500/30 px-4 py-2 text-sm text-red-200 hover:bg-red-500/10"
                        >
                            Try again
                        </button>
                    </div>
                )}

                {/* Problems list */}
                <section className="mt-6 pb-10">
                    {loading ? (
                        <div className="flex min-h-64 items-center justify-center rounded-2xl border border-slate-800 bg-slate-900/50 text-slate-400">
                            <RefreshCw size={20} className="mr-3 animate-spin" />
                            Loading your problem library...
                        </div>
                    ) : !error && filteredProblems.length === 0 ? (
                        <div className="flex min-h-64 flex-col items-center justify-center rounded-2xl border border-slate-800 bg-slate-900/50 px-5 text-center">
                            <div className="rounded-2xl bg-slate-800 p-4 text-slate-400">
                                <Search size={28} />
                            </div>

                            <h2 className="mt-4 text-lg font-semibold">
                                No problems found
                            </h2>

                            <p className="mt-2 max-w-md text-sm leading-6 text-slate-500">
                                Try another search term or select a different difficulty.
                            </p>

                            <button
                                type="button"
                                onClick={() => {
                                    setSearch('')
                                    setDifficulty('ALL')
                                }}
                                className="mt-5 rounded-xl border border-slate-700 px-4 py-2.5 text-sm text-cyan-300 transition hover:bg-slate-800"
                            >
                                Reset filters
                            </button>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
                            {filteredProblems.map((problem, index) => (
                                <Link
                                    key={problem.id ?? index}
                                    to={'/problems/' + problem.id}
                                    className="group rounded-2xl border border-slate-800 bg-slate-900/60 p-5 transition hover:-translate-y-0.5 hover:border-purple-400/30 hover:bg-slate-900 sm:p-6"
                                >
                                    <div className="flex items-start justify-between gap-4">
                                        <div className="flex min-w-0 items-start gap-3">
                                            <div className="rounded-xl border border-slate-700 bg-slate-950 p-3 text-cyan-300 transition group-hover:border-cyan-400/30">
                                                <Code2 size={21} />
                                            </div>

                                            <div className="min-w-0">
                                                <p className="text-xs text-slate-500">
                                                    Problem #{problem.id ?? index + 1}
                                                </p>

                                                <h2 className="mt-1 break-words text-lg font-bold text-slate-200 transition group-hover:text-cyan-300">
                                                    {problem.title || 'Untitled Problem'}
                                                </h2>
                                            </div>
                                        </div>

                                        <ArrowRight
                                            size={19}
                                            className="mt-1 shrink-0 text-slate-600 transition group-hover:translate-x-1 group-hover:text-cyan-300"
                                        />
                                    </div>

                                    <p className="mt-4 line-clamp-3 min-h-12 text-sm leading-6 text-slate-400">
                                        {problem.description || 'Open this problem to view its details and start solving.'}
                                    </p>

                                    <div className="mt-5 flex flex-wrap items-center justify-between gap-3 border-t border-slate-800 pt-4">
                    <span
                        className={
                            'rounded-full border px-3 py-1 text-xs font-semibold ' +
                            difficultyClass(problem.difficulty)
                        }
                    >
                      {problem.difficulty || 'Unspecified'}
                    </span>

                                        <span className="inline-flex items-center gap-1.5 text-xs font-medium text-slate-500 transition group-hover:text-cyan-300">
                      Open problem
                      <ArrowRight size={14} />
                    </span>
                                    </div>
                                </Link>
                            ))}
                        </div>
                    )}
                </section>
            </div>
        </main>
    )
}