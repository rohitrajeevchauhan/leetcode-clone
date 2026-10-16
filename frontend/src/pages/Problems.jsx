
import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import {
    Search,
    Code2,
    LogOut,
    User,
    RefreshCw,
    AlertCircle,
    CheckCircle2,
    Filter,
} from 'lucide-react'
import axios from 'axios'

function Problems() {
    const navigate = useNavigate()

    const [problems, setProblems] = useState([])
    const [search, setSearch] = useState('')
    const [difficulty, setDifficulty] = useState('All')
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        fetchProblems()
    }, [])

    const fetchProblems = async () => {
        setLoading(true)
        setError('')

        try {
            const token = localStorage.getItem('token')

            const response = await axios.get('/api/problems', {
                headers: token
                    ? { Authorization: `Bearer ${token}` }
                    : {},
            })

            if (!Array.isArray(response.data)) {
                throw new Error('The problems API did not return a list.')
            }

            setProblems(response.data)
        } catch (err) {
            console.error('Failed to fetch problems:', err)

            if (err.response?.status === 401 || err.response?.status === 403) {
                setError('Please sign in again to access the problems list.')
            } else {
                setError(
                    err.response?.data?.message ||
                    err.message ||
                    'Unable to load problems. Check your backend connection.'
                )
            }
        } finally {
            setLoading(false)
        }
    }

    const filteredProblems = useMemo(() => {
        return problems.filter((problem) => {
            const title = String(
                problem.title ?? problem.name ?? problem.problemTitle ?? ''
            )

            const level = String(problem.difficulty ?? 'Unknown')

            const matchesSearch = title
                .toLowerCase()
                .includes(search.trim().toLowerCase())

            const matchesDifficulty =
                difficulty === 'All' ||
                level.toLowerCase() === difficulty.toLowerCase()

            return matchesSearch && matchesDifficulty
        })
    }, [problems, search, difficulty])

    const getDifficultyColor = (level) => {
        switch (String(level).toLowerCase()) {
            case 'easy':
                return 'text-green-400'
            case 'medium':
                return 'text-yellow-400'
            case 'hard':
                return 'text-red-400'
            default:
                return 'text-gray-400'
        }
    }

    const handleLogout = () => {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        localStorage.removeItem('email')
        localStorage.removeItem('role')
        localStorage.removeItem('user')
        navigate('/login', { replace: true })
    }

    const username = localStorage.getItem('username') || 'Coder'

    return (
        <div className="min-h-screen bg-[#0f1117] text-white">
            <header className="sticky top-0 z-10 border-b border-gray-800 bg-[#141720]">
                <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-5 py-4">
                    <Link to="/dashboard" className="flex items-center gap-3">
                        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-orange-500">
                            <Code2 size={23} />
                        </div>
                        <div>
                            <h1 className="text-xl font-bold">
                                Leet<span className="text-orange-500">Clone</span>
                            </h1>
                            <p className="text-xs text-gray-500">Coding practice platform</p>
                        </div>
                    </Link>

                    <div className="flex items-center gap-4">
                        <div className="hidden items-center gap-2 text-sm text-gray-300 sm:flex">
                            <User size={17} className="text-orange-400" />
                            {username}
                        </div>

                        <button
                            onClick={handleLogout}
                            className="flex items-center gap-2 rounded-lg border border-gray-700 px-3 py-2 text-sm text-gray-300 transition hover:border-red-500 hover:text-red-400"
                        >
                            <LogOut size={16} />
                            Logout
                        </button>
                    </div>
                </div>
            </header>

            <main className="mx-auto max-w-7xl px-5 py-10">
                <section className="mb-8">
                    <p className="mb-2 text-sm font-semibold uppercase tracking-widest text-orange-400">
                        Practice. Compete. Improve.
                    </p>

                    <h2 className="text-3xl font-bold sm:text-4xl">
                        Coding Problems
                    </h2>

                    <p className="mt-3 max-w-2xl text-gray-400">
                        Sharpen your problem-solving skills with coding challenges.
                        Choose a problem and start practising.
                    </p>
                </section>

                <section className="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
                    <div className="rounded-xl border border-gray-800 bg-[#181b24] p-5">
                        <p className="text-sm text-gray-400">Total Problems</p>
                        <p className="mt-2 text-3xl font-bold">{problems.length}</p>
                    </div>

                    <div className="rounded-xl border border-gray-800 bg-[#181b24] p-5">
                        <p className="text-sm text-gray-400">Easy Problems</p>
                        <p className="mt-2 text-3xl font-bold text-green-400">
                            {problems.filter(
                                (p) => String(p.difficulty).toLowerCase() === 'easy'
                            ).length}
                        </p>
                    </div>

                    <div className="rounded-xl border border-gray-800 bg-[#181b24] p-5">
                        <p className="text-sm text-gray-400">Medium + Hard</p>
                        <p className="mt-2 text-3xl font-bold text-orange-400">
                            {problems.filter((p) =>
                                ['medium', 'hard'].includes(
                                    String(p.difficulty).toLowerCase()
                                )
                            ).length}
                        </p>
                    </div>
                </section>

                <section className="mb-5 flex flex-col gap-3 sm:flex-row">
                    <div className="relative flex-1">
                        <Search
                            size={19}
                            className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500"
                        />
                        <input
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            placeholder="Search problems by title..."
                            className="w-full rounded-xl border border-gray-800 bg-[#181b24] py-3 pl-11 pr-4 text-sm outline-none placeholder:text-gray-500 focus:border-orange-500"
                        />
                    </div>

                    <div className="flex items-center gap-2 rounded-xl border border-gray-800 bg-[#181b24] px-3">
                        <Filter size={17} className="text-gray-400" />
                        <select
                            value={difficulty}
                            onChange={(e) => setDifficulty(e.target.value)}
                            className="bg-transparent py-3 text-sm text-white outline-none"
                        >
                            <option className="bg-[#181b24]" value="All">All difficulties</option>
                            <option className="bg-[#181b24]" value="Easy">Easy</option>
                            <option className="bg-[#181b24]" value="Medium">Medium</option>
                            <option className="bg-[#181b24]" value="Hard">Hard</option>
                        </select>
                    </div>

                    <button
                        onClick={fetchProblems}
                        disabled={loading}
                        title="Refresh problems"
                        className="flex items-center justify-center gap-2 rounded-xl border border-gray-800 px-4 py-3 text-sm text-gray-300 hover:border-orange-500 disabled:opacity-50"
                    >
                        <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
                        Refresh
                    </button>
                </section>

                {error && (
                    <div className="mb-5 flex flex-col gap-3 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-red-300 sm:flex-row sm:items-center">
                        <AlertCircle size={20} />
                        <p className="flex-1 text-sm">{error}</p>
                        <button
                            onClick={fetchProblems}
                            className="rounded-lg border border-red-500/40 px-3 py-2 text-sm hover:bg-red-500/10"
                        >
                            Try again
                        </button>
                    </div>
                )}

                <div className="overflow-hidden rounded-xl border border-gray-800 bg-[#181b24]">
                    <div className="flex items-center justify-between border-b border-gray-800 px-5 py-4">
                        <h3 className="font-semibold">All Problems</h3>
                        <span className="text-sm text-gray-500">
              {filteredProblems.length} results
            </span>
                    </div>

                    {loading ? (
                        <div className="flex items-center justify-center gap-3 py-16 text-gray-400">
                            <RefreshCw size={20} className="animate-spin" />
                            Loading problems...
                        </div>
                    ) : filteredProblems.length === 0 ? (
                        <div className="px-5 py-16 text-center">
                            <Code2 size={35} className="mx-auto mb-3 text-gray-600" />
                            <p className="font-medium">No problems found</p>
                            <p className="mt-2 text-sm text-gray-500">
                                {problems.length === 0
                                    ? 'Your backend has not returned any problems yet.'
                                    : 'Try changing your search or difficulty filter.'}
                            </p>
                        </div>
                    ) : (
                        <div className="divide-y divide-gray-800">
                            {filteredProblems.map((problem, index) => {
                                const id = problem.id ?? problem.problemId
                                const title =
                                    problem.title ??
                                    problem.name ??
                                    problem.problemTitle ??
                                    `Problem ${index + 1}`
                                const level = problem.difficulty ?? 'Unknown'

                                return (
                                    <div
                                        key={id ?? index}
                                        className="flex flex-col gap-3 px-5 py-4 transition hover:bg-white/[0.025] sm:flex-row sm:items-center"
                                    >
                                        <div className="flex min-w-0 flex-1 items-start gap-3">
                      <span className="mt-0.5 flex h-7 min-w-7 items-center justify-center rounded-md bg-gray-800 px-2 text-xs text-gray-400">
                        {index + 1}
                      </span>

                                            <div className="min-w-0">
                                                <h4 className="font-medium text-gray-100">
                                                    {title}
                                                </h4>
                                                <p className="mt-1 line-clamp-2 text-sm text-gray-500">
                                                    {problem.description || 'Open this problem to view its details and examples.'}
                                                </p>
                                            </div>
                                        </div>

                                        <div className="flex items-center justify-between gap-4 sm:justify-end">
                      <span
                          className={`text-sm font-medium ${getDifficultyColor(level)}`}
                      >
                        {level}
                      </span>

                                            {id != null ? (
                                                <Link
                                                    to={`/problems/${id}`}
                                                    className="rounded-lg bg-orange-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-orange-600"
                                                >
                                                    Solve
                                                </Link>
                                            ) : (
                                                <span className="text-xs text-gray-500">
                          Missing problem ID
                        </span>
                                            )}
                                        </div>
                                    </div>
                                )
                            })}
                        </div>
                    )}
                </div>

                <div className="mt-5 flex items-center gap-2 text-xs text-gray-500">
                    <CheckCircle2 size={15} />
                    Problems are loaded from your Spring Boot API.
                </div>
            </main>
        </div>
    )
}

export default Problems
