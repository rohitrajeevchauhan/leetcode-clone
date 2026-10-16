import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import {
  Code2,
  Eye,
  EyeOff,
  Lock,
  Mail,
  User
} from 'lucide-react'

function Register() {
  const [showPassword, setShowPassword] = useState(false)

  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  const navigate = useNavigate()

  const handleSubmit = (e) => {
    e.preventDefault()

    navigate('/login')
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-[#0f1117] px-4">

      <div className="w-full max-w-md">

        <div className="mb-8 text-center">

          <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-xl bg-orange-500">
            <Code2 size={30} />
          </div>

          <h1 className="text-3xl font-bold text-white">
            Join Leet<span className="text-orange-500">Clone</span>
          </h1>

          <p className="mt-2 text-gray-500">
            Create your developer account
          </p>

        </div>

        <div className="rounded-2xl border border-gray-800 bg-[#181b24] p-6 shadow-2xl">

          <form onSubmit={handleSubmit} className="space-y-5">

            <div>
              <label className="mb-2 block text-sm text-gray-300">
                Username
              </label>

              <div className="relative">
                <User
                  size={18}
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
                />

                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="rohit"
                  required
                  className="w-full rounded-lg border border-gray-700 bg-[#0f1117] py-3 pl-10 pr-4 text-white outline-none focus:border-orange-500"
                />
              </div>
            </div>

            <div>
              <label className="mb-2 block text-sm text-gray-300">
                Email
              </label>

              <div className="relative">
                <Mail
                  size={18}
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
                />

                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="you@example.com"
                  required
                  className="w-full rounded-lg border border-gray-700 bg-[#0f1117] py-3 pl-10 pr-4 text-white outline-none focus:border-orange-500"
                />
              </div>
            </div>

            <div>
              <label className="mb-2 block text-sm text-gray-300">
                Password
              </label>

              <div className="relative">
                <Lock
                  size={18}
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
                />

                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Create a password"
                  required
                  className="w-full rounded-lg border border-gray-700 bg-[#0f1117] py-3 pl-10 pr-12 text-white outline-none focus:border-orange-500"
                />

                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-white"
                >
                  {showPassword ? (
                    <EyeOff size={18} />
                  ) : (
                    <Eye size={18} />
                  )}
                </button>
              </div>
            </div>

            <button
              type="submit"
              className="w-full rounded-lg bg-orange-500 py-3 font-semibold text-white transition hover:bg-orange-600"
            >
              Create Account
            </button>

          </form>

          <div className="mt-6 text-center text-sm text-gray-500">
            Already have an account?{' '}
            <Link
              to="/login"
              className="font-medium text-orange-500 hover:text-orange-400"
            >
              Sign in
            </Link>
          </div>

        </div>

      </div>

    </div>
  )
}

export default Register