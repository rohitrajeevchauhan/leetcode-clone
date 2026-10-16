import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import axios from 'axios'
import {
    Code2,
    Lock,
    Mail,
    Loader2,
    AlertCircle,
    Eye,
    EyeOff,
    ArrowRight,
    ShieldCheck,
} from 'lucide-react'

export default function Login() {
    const navigate = useNavigate()
    const location = useLocation()

    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [showPassword, setShowPassword] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    const handleSubmit = async (event) => {
        event.preventDefault()
        setError('')

        if (!email.trim() || !password) {
            setError('Please enter your email and password.')
            return
        }

        setLoading(true)

        try {
            const response = await axios.post('/api/auth/login', {
                email: email.trim(),
                password,
            })

            const data = response.data

            if (!data || !data.token) {
                setError('Login response did not contain an authentication token.')
                return
            }

            localStorage.setItem('token', data.token)
            localStorage.setItem(
                'username',
                data.username || email.trim().split('@')[0]
            )
            localStorage.setItem('email', data.email || email.trim())

            if (data.role) {
                localStorage.setItem('role', data.role)
            }

            localStorage.setItem('user', JSON.stringify(data))

            const destination =
                location.state?.from?.pathname || '/dashboard'

            navigate(destination, { replace: true })
        } catch (err) {
            if (err.response?.status === 401 || err.response?.status === 403) {
                setError('Invalid email or password. Please try again.')
            } else if (err.response?.data?.message) {
                setError(err.response.data.message)
            } else if (err.response) {
                setError('Login failed. Please check your credentials.')
            } else {
                setError(
                    'Unable to connect to the server. Check that your backend is running.'
                )
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <main className="relative flex min-h-[calc(100vh-64px)] items-center justify-center overflow-hidden bg-slate-950 px-4 py-12 text-white">
            {/* Background effects */}
            <div className="pointer-events-none absolute -left-32 top-10 h-80 w-80 rounded-full bg-purple-600/15 blur-[100px]" />
            <div className="pointer-events-none absolute -right-32 bottom-0 h-96 w-96 rounded-full bg-cyan-500/10 blur-[110px]" />

            <div className="relative grid w-full max-w-5xl overflow-hidden rounded-3xl border border-slate-800 bg-slate-900/80 shadow-2xl shadow-black/40 lg:grid-cols-2">
                {/* Left panel */}
                <section className="relative hidden flex-col justify-between overflow-hidden border-r border-slate-800 bg-gradient-to-br from-slate-900 via-slate-900 to-purple-950/50 p-10 lg:flex">
                    <div>
                        <div className="inline-flex items-center gap-3">
                            <div className="rounded-xl bg-gradient-to-br from-purple-600 to-cyan-500 p-3">
                                <Code2 size={28} />
                            </div>

                            <span className="text-2xl font-bold">
                Leet<span className="text-cyan-400">Clone</span>
              </span>
                        </div>

                        <div className="mt-20">
              <span className="rounded-full border border-cyan-400/20 bg-cyan-400/10 px-3 py-1.5 text-xs font-semibold uppercase tracking-[0.2em] text-cyan-300">
                Your coding journey
              </span>

                            <h1 className="mt-6 text-4xl font-bold leading-tight">
                                Think better.
                                <br />
                                Code smarter.
                                <br />
                                <span className="bg-gradient-to-r from-purple-400 to-cyan-300 bg-clip-text text-transparent">
                  Grow every day.
                </span>
                            </h1>

                            <p className="mt-5 max-w-sm leading-7 text-slate-400">
                                Practice coding problems, track your progress, and prepare
                                for your next technical interview.
                            </p>
                        </div>
                    </div>

                    <div className="mt-16 rounded-2xl border border-slate-700/70 bg-slate-950/50 p-5">
                        <div className="flex items-center gap-3">
                            <div className="rounded-xl bg-purple-500/15 p-3 text-purple-300">
                                <ShieldCheck size={24} />
                            </div>

                            <div>
                                <p className="font-semibold text-slate-200">
                                    Your workspace awaits
                                </p>
                                <p className="mt-1 text-sm text-slate-500">
                                    Sign in to continue learning.
                                </p>
                            </div>
                        </div>
                    </div>
                </section>

                {/* Login form */}
                <section className="p-6 sm:p-10 lg:p-12">
                    <div className="mx-auto max-w-md">
                        <div className="mb-8 lg:hidden">
                            <div className="inline-flex items-center gap-3">
                                <div className="rounded-xl bg-gradient-to-br from-purple-600 to-cyan-500 p-2.5">
                                    <Code2 size={24} />
                                </div>

                                <span className="text-2xl font-bold">
                  Leet<span className="text-cyan-400">Clone</span>
                </span>
                            </div>
                        </div>

                        <div className="mb-8">
                            <p className="text-sm font-semibold uppercase tracking-[0.2em] text-cyan-400">
                                Welcome back
                            </p>

                            <h2 className="mt-3 text-3xl font-bold tracking-tight sm:text-4xl">
                                Sign in to your account
                            </h2>

                            <p className="mt-3 text-sm leading-6 text-slate-400">
                                Enter your credentials to access your coding workspace.
                            </p>
                        </div>

                        {error && (
                            <div
                                role="alert"
                                className="mb-6 flex items-start gap-3 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300"
                            >
                                <AlertCircle size={19} className="mt-0.5 shrink-0" />
                                <p>{error}</p>
                            </div>
                        )}

                        <form onSubmit={handleSubmit} className="space-y-5">
                            <div>
                                <label
                                    htmlFor="email"
                                    className="mb-2 block text-sm font-medium text-slate-300"
                                >
                                    Email address
                                </label>

                                <div className="flex items-center gap-3 rounded-xl border border-slate-700 bg-slate-950/80 px-4 transition focus-within:border-cyan-400/60 focus-within:ring-2 focus-within:ring-cyan-400/10">
                                    <Mail size={19} className="shrink-0 text-slate-500" />

                                    <input
                                        id="email"
                                        type="email"
                                        autoComplete="email"
                                        required
                                        value={email}
                                        onChange={(event) => setEmail(event.target.value)}
                                        placeholder="you@example.com"
                                        className="w-full bg-transparent py-3.5 text-sm text-white outline-none placeholder:text-slate-600"
                                    />
                                </div>
                            </div>

                            <div>
                                <div className="mb-2 flex items-center justify-between">
                                    <label
                                        htmlFor="password"
                                        className="text-sm font-medium text-slate-300"
                                    >
                                        Password
                                    </label>
                                </div>

                                <div className="flex items-center gap-3 rounded-xl border border-slate-700 bg-slate-950/80 px-4 transition focus-within:border-cyan-400/60 focus-within:ring-2 focus-within:ring-cyan-400/10">
                                    <Lock size={19} className="shrink-0 text-slate-500" />

                                    <input
                                        id="password"
                                        type={showPassword ? 'text' : 'password'}
                                        autoComplete="current-password"
                                        required
                                        value={password}
                                        onChange={(event) => setPassword(event.target.value)}
                                        placeholder="Enter your password"
                                        className="w-full bg-transparent py-3.5 text-sm text-white outline-none placeholder:text-slate-600"
                                    />

                                    <button
                                        type="button"
                                        onClick={() => setShowPassword((value) => !value)}
                                        aria-label={showPassword ? 'Hide password' : 'Show password'}
                                        className="shrink-0 text-slate-500 transition hover:text-cyan-300"
                                    >
                                        {showPassword ? <EyeOff size={19} /> : <Eye size={19} />}
                                    </button>
                                </div>
                            </div>

                            <button
                                type="submit"
                                disabled={loading}
                                className="flex w-full items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 px-4 py-3.5 font-semibold text-white shadow-lg shadow-purple-950/30 transition hover:from-purple-500 hover:to-indigo-500 disabled:cursor-not-allowed disabled:opacity-60"
                            >
                                {loading ? (
                                    <>
                                        <Loader2 size={19} className="animate-spin" />
                                        Signing in...
                                    </>
                                ) : (
                                    <>
                                        Sign in
                                        <ArrowRight size={19} />
                                    </>
                                )}
                            </button>
                        </form>

                        <div className="my-7 flex items-center gap-4">
                            <div className="h-px flex-1 bg-slate-800" />
                            <span className="text-xs text-slate-600">
                YOUR CODING JOURNEY STARTS HERE
              </span>
                            <div className="h-px flex-1 bg-slate-800" />
                        </div>

                        <p className="text-center text-sm text-slate-400">
                            Don&apos;t have an account?{' '}
                            <Link
                                to="/register"
                                className="font-semibold text-cyan-400 transition hover:text-cyan-300"
                            >
                                Create an account
                            </Link>
                        </p>

                        <p className="mt-8 text-center text-xs text-slate-600">
                            LeetClone · Practice. Improve. Succeed.
                        </p>
                    </div>
                </section>
            </div>
        </main>
    )
}