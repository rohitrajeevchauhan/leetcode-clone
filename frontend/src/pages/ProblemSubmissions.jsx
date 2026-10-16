
import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import axios from 'axios'

const API = '/api'

function statusClass(status = '') {
    if (status === 'ACCEPTED') return 'text-green-400'
    if (status === 'PENDING' || status === 'RUNNING') return 'text-yellow-400'
    return 'text-red-400'
}

export default function ProblemSubmissions() {
    const { problemId } = useParams()
    const [items, setItems] = useState([])
    const [page, setPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        let active = true

        async function load() {
            setLoading(true)
            setError('')

            try {
                const response = await axios.get(
                    `${API}/submissions/problem/${problemId}`,
                    {
                        params: { page, size: 10, sort: 'createdAt,desc' },
                        headers: {
                            Authorization: `Bearer ${localStorage.getItem('token')}`,
                        },
                    }
                )

                if (!active) return

                const data = response.data
                setItems(Array.isArray(data) ? data : data.content ?? [])
                setTotalPages(Array.isArray(data) ? 1 : data.totalPages ?? 0)
            } catch (err) {
                if (active) {
                    setError(
                        err.response?.data?.message || 'Could not load problem submissions.'
                    )
                }
            } finally {
                if (active) setLoading(false)
            }
        }

        load()
        return () => { active = false }
    }, [problemId, page])

    return (
        <main className="min-h-screen bg-slate-950 px-4 py-8 text-slate-100 md:px-10">
            <div className="mx-auto max-w-5xl">
                <Link to={`/problems/${problemId}`} className="text-indigo-400 hover:text-indigo-300">
                    ← Back to Problem
                </Link>

                <h1 className="mt-5 text-3xl font-bold">Problem Submissions</h1>
                <p className="mt-2 text-slate-400">Problem ID: {problemId}</p>

                {loading && <p className="mt-8 text-slate-400">Loading...</p>}
                {!loading && error && (
                    <p className="mt-6 rounded-lg bg-red-500/10 p-4 text-red-300">{error}</p>
                )}
                {!loading && !error && items.length === 0 && (
                    <p className="mt-8 rounded-lg border border-slate-800 p-6 text-slate-400">
                        You have not submitted code for this problem yet.
                    </p>
                )}

                {!loading && !error && items.length > 0 && (
                    <div className="mt-6 space-y-3">
                        {items.map((item) => (
                            <Link
                                key={item.id}
                                to={`/submissions/${item.id}`}
                                className="block rounded-xl border border-slate-800 bg-slate-900/60 p-5 hover:border-indigo-500/60"
                            >
                                <div className="flex flex-wrap items-center justify-between gap-3">
                  <span className={`font-semibold ${statusClass(item.status)}`}>
                    {(item.status || 'UNKNOWN').replaceAll('_', ' ')}
                  </span>
                                    <span className="text-sm text-slate-400">
                    {item.language || '—'}
                  </span>
                                </div>

                                <div className="mt-3 text-sm text-slate-300">
                                    Test cases passed: {item.passedTestCases ?? 0} / {item.totalTestCases ?? 0}
                                </div>
                                <div className="mt-1 text-sm text-slate-500">
                                    {item.createdAt ? new Date(item.createdAt).toLocaleString() : 'Date unavailable'}
                                </div>
                            </Link>
                        ))}

                        <div className="flex items-center justify-between pt-3">
              <span className="text-sm text-slate-400">
                Page {page + 1}{totalPages > 0 ? ` of ${totalPages}` : ''}
              </span>
                            <div className="flex gap-2">
                                <button
                                    disabled={page === 0}
                                    onClick={() => setPage((p) => p - 1)}
                                    className="rounded-lg border border-slate-700 px-4 py-2 disabled:opacity-40"
                                >
                                    Previous
                                </button>
                                <button
                                    disabled={totalPages === 0 || page + 1 >= totalPages}
                                    onClick={() => setPage((p) => p + 1)}
                                    className="rounded-lg border border-slate-700 px-4 py-2 disabled:opacity-40"
                                >
                                    Next
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </main>
    )
}
