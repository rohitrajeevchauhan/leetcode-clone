import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { Lock, Mail, Loader2, AlertCircle } from 'lucide-react'
import axios from 'axios'

function Login() {
  const navigate = useNavigate()
  const location = useLocation()

  const [form, setForm] = useState({
    email: '',
    password: ''
  })

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    setError('')
    setLoading(true)

    try {
      const response = await axios.post('/api/auth/login', {
        email: form.email,
        password: form.password
      })

      const data = response.data

      console.log('Login response:', data)

      const token =
        data.token ||
        data.accessToken ||
        data.jwt

      if (!token) {
        throw new Error('No JWT token received from server')
      }

      localStorage.setItem('token', token)

      const user = data.user

      if (user) {
        localStorage.setItem('user', JSON.stringify(user))

        if (user.role) {
          localStorage.setItem(
            'role',
            typeof user.role === 'string'
              ? user.role
              : user.role.name
          )
        }
      }

      const from = location.state?.from?.pathname || '/dashboard'

      navigate(from, { replace: true })

    } catch (err) {
      console.error('Login error:', err)

      if (err.response?.status === 401) {
        setError('Invalid email or password.')
      } else if (err.response?.data?.message) {
        setError(err.response.data.message)
      } else if (err.message) {
        setError(err.message)
      } else {
        setError('Login failed. Please try again.')
      }

    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-[#0f1117] px-4">

      <div className="w-full max-w-md">

        <div className="mb-8 text-center">
          <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-orange-500">
            <span className="text-2xl font-black text-white">
              LC
            </span>
          </div>

          <h1 className="text-3xl font-bold text-white">
            Welcome back
          </h1>

          <p className="mt-2 text-gray-500">
            Sign in to continue to LeetClone
          </p>
        </div>

        <div className="rounded-2xl border border-gray-800 bg-[#181b24] p-6 shadow-xl">

          {error && (
            <div className="mb-5 flex items-start gap-3 rounded-lg border border-red-500/20 bg-red-500/10 p-3 text-sm text-red-400">
              <AlertCircle
                size={18}
                className="mt-0.5 shrink-0"
              />
              <span>{error}</span>
            </div>
          )}

          <form
            onSubmit={handleSubmit}
            className="space-y-5"
          >

            <div>
              <label className="mb-2 block text-sm font-medium text-gray-300">
                Email
              </label>

              <div className="relative">
                <Mail
                  size={18}
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
                />

                <input
                  type="email"
                  name="email"
                  value={form.email}
                  onChange={handleChange}
                  placeholder="you@example.com"
                  required
                  className="w-full rounded-lg border border-gray-700 bg-[#0f1117] py-3 pl-10 pr-4 text-sm text-white outline-none placeholder:text-gray-600 focus:border-orange-500"
                />
              </div>
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-gray-300">
                Password
              </label>

              <div className="relative">
                <Lock
                  size={18}
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
                />

                <input
                  type="password"
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  placeholder="••••••••"
                  required
                  className="w-full rounded-lg border border-gray-700 bg-[#0f1117] py-3 pl-10 pr-4 text-sm text-white outline-none placeholder:text-gray-600 focus:border-orange-500"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="flex w-full items-center justify-center gap-2 rounded-lg bg-orange-500 py-3 font-semibold text-white transition hover:bg-orange-600 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading ? (
                <>
                  <Loader2
                    size={18}
                    className="animate-spin"
                  />
                  Signing in...
                </>
              ) : (
                'Sign In'
              )}
            </button>

          </form>

          <div className="mt-6 text-center text-sm text-gray-500">
            Don't have an account?{' '}
            <Link
              to="/register"
              className="font-medium text-orange-500 hover:text-orange-400"
            >
              Create account
            </Link>
          </div>

        </div>

        <p className="mt-6 text-center text-xs text-gray-600">
          LeetClone • Practice. Compete. Improve.
        </p>

      </div>

    </div>
  )
}

export default Login