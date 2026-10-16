import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import {
    Lock,
    Mail,
    Loader2,
    AlertCircle,
} from "lucide-react";
import axios from "axios";

const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL ||
    "https://leetcode-clone-1867.onrender.com";

export default function Login() {
    const navigate = useNavigate();
    const location = useLocation();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();

        setError("");
        setLoading(true);

        try {
            const response = await axios.post(
                `${API_BASE_URL}/api/auth/login`,
                {
                    email: email.trim().toLowerCase(),
                    password: password,
                },
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                    timeout: 60000,
                }
            );

            const data = response.data;

            if (!data || !data.token) {
                throw new Error(
                    "Login response did not contain an authentication token."
                );
            }

            // Store authentication details.
            localStorage.setItem("token", data.token);

            if (data.username) {
                localStorage.setItem("username", data.username);
            }

            if (data.email) {
                localStorage.setItem("email", data.email);
            }

            if (data.userId != null) {
                localStorage.setItem("userId", String(data.userId));
            }

            if (data.role) {
                localStorage.setItem("role", data.role);
            }

            // Navigate to the page the user originally requested,
            // or to the Problems page by default.
            const destination = location.state?.from?.pathname || "/problems";
            navigate(destination, { replace: true });
        } catch (err) {
            console.error(
                "Login error:",
                err.response?.status,
                err.response?.data || err.message
            );

            if (err.response?.status === 401) {
                setError("Invalid email or password.");
            } else if (err.response?.status === 403) {
                setError(
                    "Access forbidden. Check your backend security configuration."
                );
            } else if (err.response?.status === 404) {
                setError(
                    "Login endpoint not found. Check your backend API URL."
                );
            } else if (err.response?.status === 409) {
                setError("A conflict occurred. Please check your account details.");
            } else if (err.response?.status >= 500) {
                setError(
                    "Backend server error. Please try again in a moment."
                );
            } else if (err.code === "ECONNABORTED") {
                setError(
                    "The server took too long to respond. Please try again."
                );
            } else if (!err.response) {
                setError(
                    "Cannot connect to the backend. Check the server and CORS settings."
                );
            } else {
                setError(
                    err.response?.data?.message ||
                    err.response?.data?.detail ||
                    "Login failed. Please check your credentials."
                );
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-950 px-4 py-10">
            <div className="w-full max-w-md rounded-2xl border border-gray-800 bg-gray-900 p-8 shadow-2xl">
                <div className="mb-8 text-center">
                    <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-orange-500 text-2xl font-bold text-white">
                        LC
                    </div>

                    <h1 className="text-3xl font-bold text-white">
                        Welcome Back
                    </h1>

                    <p className="mt-2 text-sm text-gray-400">
                        Sign in to continue solving problems.
                    </p>
                </div>

                {error && (
                    <div
                        role="alert"
                        className="mb-5 flex items-start gap-3 rounded-lg border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-300"
                    >
                        <AlertCircle
                            size={20}
                            className="mt-0.5 shrink-0"
                        />
                        <span>{error}</span>
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label
                            htmlFor="email"
                            className="mb-2 block text-sm font-medium text-gray-300"
                        >
                            Email address
                        </label>

                        <div className="relative">
                            <Mail
                                size={18}
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
                            />

                            <input
                                id="email"
                                type="email"
                                name="email"
                                autoComplete="email"
                                placeholder="you@example.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                                className="w-full rounded-lg border border-gray-700 bg-gray-800 py-3 pl-10 pr-4 text-white outline-none transition placeholder:text-gray-500 focus:border-orange-500 focus:ring-1 focus:ring-orange-500"
                            />
                        </div>
                    </div>

                    <div>
                        <label
                            htmlFor="password"
                            className="mb-2 block text-sm font-medium text-gray-300"
                        >
                            Password
                        </label>

                        <div className="relative">
                            <Lock
                                size={18}
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
                            />

                            <input
                                id="password"
                                type="password"
                                name="password"
                                autoComplete="current-password"
                                placeholder="Enter your password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                                className="w-full rounded-lg border border-gray-700 bg-gray-800 py-3 pl-10 pr-4 text-white outline-none transition placeholder:text-gray-500 focus:border-orange-500 focus:ring-1 focus:ring-orange-500"
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
                                <Loader2 size={20} className="animate-spin" />
                                Signing in...
                            </>
                        ) : (
                            "Sign In"
                        )}
                    </button>
                </form>

                <p className="mt-6 text-center text-sm text-gray-400">
                    Don't have an account?{" "}
                    <Link
                        to="/register"
                        className="font-semibold text-orange-400 hover:text-orange-300"
                    >
                        Create an account
                    </Link>
                </p>
            </div>
        </div>
    );
}