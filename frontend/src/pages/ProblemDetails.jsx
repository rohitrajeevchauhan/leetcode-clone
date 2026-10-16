import React, { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import axios from 'axios'
import Editor from '@monaco-editor/react'
import {
    ArrowLeft,
    Play,
    Send,
    RotateCcw,
    Loader2,
    AlertCircle,
    CheckCircle2,
    XCircle,
    Clock,
    Code2,
    FileText,
    Terminal,
    ChevronDown,
    Maximize2,
    RefreshCw,
} from 'lucide-react'

const API = '/api'

const LANGUAGE_OPTIONS = [
    { value: 'JAVA', label: 'Java' },
    { value: 'CPP', label: 'C++' },
    { value: 'PYTHON', label: 'Python' },
    { value: 'JAVASCRIPT', label: 'JavaScript' },
]

const STARTER_CODE = {
    JAVA: `import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Read input here
        // Write your solution here

        sc.close();
    }
}`,
    CPP: `#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    // Read input here
    // Write your solution here

    return 0;
}`,
    PYTHON: `def solve():
    # Read input here
    # Write your solution here
    pass

if __name__ == "__main__":
    solve()`,
    JAVASCRIPT: `const fs = require('fs');

function solve() {
    const input = fs.readFileSync(0, 'utf8').trim();

    // Write your solution here
}

solve();`,
}

const MONACO_LANGUAGE = {
    JAVA: 'java',
    CPP: 'cpp',
    PYTHON: 'python',
    JAVASCRIPT: 'javascript',
}

function getToken() {
    return (
        localStorage.getItem('token') ||
        localStorage.getItem('accessToken') ||
        ''
    )
}

function getProblemTitle(problem) {
    return (
        problem?.title ||
        problem?.problemTitle ||
        problem?.name ||
        'Problem Details'
    )
}

function getErrorMessage(error) {
    const data = error?.response?.data

    if (typeof data === 'string' && data.trim()) {
        return data
    }

    if (data?.message) return data.message
    if (data?.error && data?.status) {
        return `${data.status}: ${data.error}`
    }
    if (data?.error) return data.error
    if (error?.message === 'Network Error') {
        return 'Cannot connect to the backend. Check that your Spring Boot server is running and your Vite proxy is configured.'
    }

    return error?.message || 'Something went wrong. Please try again.'
}

function formatValue(value) {
    if (value === null || value === undefined || value === '') {
        return '—'
    }

    if (typeof value === 'object') {
        return JSON.stringify(value, null, 2)
    }

    return String(value)
}

function getDifficultyClass(difficulty) {
    const value = String(difficulty || '').toLowerCase()

    if (value === 'easy') {
        return 'border-emerald-500/30 bg-emerald-500/10 text-emerald-400'
    }

    if (value === 'medium') {
        return 'border-amber-500/30 bg-amber-500/10 text-amber-400'
    }

    if (value === 'hard') {
        return 'border-rose-500/30 bg-rose-500/10 text-rose-400'
    }

    return 'border-slate-700 bg-slate-800 text-slate-300'
}

function StatusBadge({ status }) {
    const value = String(status || 'PENDING').toUpperCase()

    const success = value === 'ACCEPTED'
    const failure = [
        'WRONG_ANSWER',
        'COMPILATION_ERROR',
        'RUNTIME_ERROR',
        'TIME_LIMIT_EXCEEDED',
        'MEMORY_LIMIT_EXCEEDED',
    ].includes(value)

    const className = success
        ? 'border-emerald-500/30 bg-emerald-500/10 text-emerald-400'
        : failure
            ? 'border-rose-500/30 bg-rose-500/10 text-rose-400'
            : 'border-amber-500/30 bg-amber-500/10 text-amber-300'

    const Icon = success ? CheckCircle2 : failure ? XCircle : Clock

    return (
        <span
            className={`inline-flex items-center gap-2 rounded-full border px-3 py-1 text-xs font-semibold ${className}`}
        >
      <Icon size={14} />
            {value.replaceAll('_', ' ')}
    </span>
    )
}

function MetricCard({ icon: Icon, label, value }) {
    return (
        <div className="rounded-xl border border-slate-800 bg-slate-900/70 p-3">
            <div className="mb-2 flex items-center gap-2 text-xs text-slate-400">
                <Icon size={14} />
                {label}
            </div>
            <div className="text-lg font-semibold text-slate-100">{value}</div>
        </div>
    )
}

function getTestCases(result) {
    if (Array.isArray(result?.testCases)) return result.testCases
    if (Array.isArray(result?.testCaseResults)) return result.testCaseResults
    return []
}

function ProblemDetails() {
    const { id } = useParams()
    const navigate = useNavigate()

    const [problem, setProblem] = useState(null)
    const [loadingProblem, setLoadingProblem] = useState(true)
    const [problemError, setProblemError] = useState('')

    const [language, setLanguage] = useState('JAVA')
    const [code, setCode] = useState(STARTER_CODE.JAVA)
    const [fontSize, setFontSize] = useState(14)

    const [activeTab, setActiveTab] = useState('description')
    const [showResetConfirm, setShowResetConfirm] = useState(false)

    const [running, setRunning] = useState(false)
    const [submitting, setSubmitting] = useState(false)

    const [runResult, setRunResult] = useState(null)
    const [submission, setSubmission] = useState(null)

    const [submitError, setSubmitError] = useState('')
    const [notice, setNotice] = useState('')

    useEffect(() => {
        let cancelled = false

        async function loadProblem() {
            setLoadingProblem(true)
            setProblemError('')

            try {
                const response = await axios.get(`${API}/problems`)
                const problems = Array.isArray(response.data)
                    ? response.data
                    : response.data?.content || response.data?.problems || []

                const found = problems.find(
                    (item) =>
                        String(item.id ?? item.problemId) === String(id),
                )

                if (!found) {
                    if (!cancelled) {
                        setProblem(null)
                        setProblemError('Problem not found.')
                    }
                    return
                }

                if (!cancelled) {
                    setProblem(found)
                }
            } catch (error) {
                if (!cancelled) {
                    setProblemError(getErrorMessage(error))
                }
            } finally {
                if (!cancelled) {
                    setLoadingProblem(false)
                }
            }
        }

        loadProblem()

        return () => {
            cancelled = true
        }
    }, [id])

    function changeLanguage(nextLanguage) {
        setLanguage(nextLanguage)
        setCode(STARTER_CODE[nextLanguage] || '')
        setRunResult(null)
        setSubmission(null)
        setSubmitError('')
        setNotice('')
        setActiveTab('description')
    }

    function resetCode() {
        setCode(STARTER_CODE[language] || '')
        setRunResult(null)
        setSubmission(null)
        setSubmitError('')
        setNotice('')
        setShowResetConfirm(false)
    }

    async function handleRun() {
        if (!getToken()) {
            navigate('/login')
            return
        }

        if (!problem) {
            setSubmitError('Problem details have not loaded yet.')
            return
        }

        if (!code.trim()) {
            setSubmitError('Please enter your code before running it.')
            return
        }

        setRunning(true)
        setSubmitError('')
        setNotice('')
        setSubmission(null)
        setRunResult(null)
        setActiveTab('results')

        try {
            const response = await axios.post(
                `${API}/submissions/run`,
                {
                    problemId: Number(problem.id ?? problem.problemId ?? id),
                    sourceCode: code,
                    language,
                },
                {
                    headers: {
                        Authorization: `Bearer ${getToken()}`,
                        'Content-Type': 'application/json',
                    },
                },
            )

            setRunResult(response.data)
        } catch (error) {
            setSubmitError(getErrorMessage(error))
        } finally {
            setRunning(false)
        }
    }

    async function handleSubmit() {
        if (!getToken()) {
            navigate('/login')
            return
        }

        if (!problem) {
            setSubmitError('Problem details have not loaded yet.')
            return
        }

        if (!code.trim()) {
            setSubmitError('Please enter your code before submitting.')
            return
        }

        setSubmitting(true)
        setSubmitError('')
        setNotice('')
        setRunResult(null)
        setSubmission(null)
        setActiveTab('results')

        try {
            const response = await axios.post(
                `${API}/submissions`,
                {
                    problemId: Number(problem.id ?? problem.problemId ?? id),
                    sourceCode: code,
                    language,
                },
                {
                    headers: {
                        Authorization: `Bearer ${getToken()}`,
                        'Content-Type': 'application/json',
                    },
                },
            )

            setSubmission(response.data)
        } catch (error) {
            setSubmitError(getErrorMessage(error))
        } finally {
            setSubmitting(false)
        }
    }

    const testCases = getTestCases(runResult || submission)
    const currentResult = runResult || submission

    const executionTime =
        currentResult?.executionTime ??
        currentResult?.executionTimeMs ??
        currentResult?.time

    const passedCount =
        currentResult?.passedTestCases ??
        testCases.filter((testCase) => testCase.passed).length

    const totalCount =
        currentResult?.totalTestCases ??
        testCases.length

    const resultStatus =
        currentResult?.status ??
        currentResult?.verdict ??
        currentResult?.submissionStatus

    return (
        <div className="min-h-screen bg-[#080b14] text-slate-100">
            {/* Top navigation */}
            <header className="sticky top-0 z-30 border-b border-slate-800 bg-[#0b1020]/95 backdrop-blur">
                <div className="mx-auto flex h-16 max-w-[1800px] items-center justify-between gap-4 px-4 sm:px-6">
                    <div className="flex min-w-0 items-center gap-3">
                        <button
                            onClick={() => navigate(-1)}
                            className="rounded-lg border border-slate-700 p-2 text-slate-300 transition hover:bg-slate-800 hover:text-white"
                            title="Go back"
                        >
                            <ArrowLeft size={18} />
                        </button>

                        <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-violet-600 to-cyan-500">
                            <Code2 size={20} className="text-white" />
                        </div>

                        <div className="min-w-0">
                            <div className="truncate text-sm font-bold tracking-wide">
                                LeetClone
                            </div>
                            <div className="text-xs text-slate-500">
                                Coding workspace
                            </div>
                        </div>
                    </div>

                    <div className="hidden min-w-0 flex-1 justify-center sm:flex">
            <span className="max-w-lg truncate text-sm font-medium text-slate-300">
              {getProblemTitle(problem)}
            </span>
                    </div>

                    <button
                        onClick={() => navigate('/problems')}
                        className="rounded-lg border border-slate-700 px-3 py-2 text-sm text-slate-300 transition hover:bg-slate-800 hover:text-white"
                    >
                        All Problems
                    </button>
                </div>
            </header>

            <main className="mx-auto max-w-[1800px] p-3 sm:p-5">
                {loadingProblem ? (
                    <div className="flex min-h-[60vh] items-center justify-center gap-3 text-slate-400">
                        <Loader2 className="animate-spin" size={22} />
                        Loading problem...
                    </div>
                ) : problemError ? (
                    <div className="mx-auto mt-16 max-w-xl rounded-2xl border border-rose-500/20 bg-rose-500/5 p-6">
                        <AlertCircle className="mb-3 text-rose-400" size={28} />
                        <h2 className="mb-2 text-lg font-semibold">
                            Unable to load problem
                        </h2>
                        <p className="mb-5 text-sm text-slate-400">
                            {problemError}
                        </p>
                        <div className="flex gap-3">
                            <button
                                onClick={() => window.location.reload()}
                                className="rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold hover:bg-violet-500"
                            >
                                Try again
                            </button>
                            <button
                                onClick={() => navigate('/problems')}
                                className="rounded-lg border border-slate-700 px-4 py-2 text-sm hover:bg-slate-800"
                            >
                                Back to problems
                            </button>
                        </div>
                    </div>
                ) : (
                    <div className="grid min-h-[calc(100vh-110px)] grid-cols-1 gap-4 xl:grid-cols-2">
                        {/* Left panel: problem description and results */}
                        <section className="flex min-h-[600px] flex-col overflow-hidden rounded-2xl border border-slate-800 bg-[#0d1220]">
                            <div className="flex items-center gap-1 border-b border-slate-800 px-3 pt-3">
                                <button
                                    onClick={() => setActiveTab('description')}
                                    className={`flex items-center gap-2 rounded-t-lg px-4 py-3 text-sm font-medium transition ${
                                        activeTab === 'description'
                                            ? 'border-b-2 border-violet-500 bg-violet-500/5 text-white'
                                            : 'text-slate-400 hover:text-white'
                                    }`}
                                >
                                    <FileText size={16} />
                                    Description
                                </button>

                                <button
                                    onClick={() => setActiveTab('results')}
                                    className={`flex items-center gap-2 rounded-t-lg px-4 py-3 text-sm font-medium transition ${
                                        activeTab === 'results'
                                            ? 'border-b-2 border-violet-500 bg-violet-500/5 text-white'
                                            : 'text-slate-400 hover:text-white'
                                    }`}
                                >
                                    <Terminal size={16} />
                                    Results
                                    {currentResult && (
                                        <span className="h-2 w-2 rounded-full bg-cyan-400" />
                                    )}
                                </button>
                            </div>

                            <div className="flex-1 overflow-y-auto p-5">
                                {activeTab === 'description' ? (
                                    <div>
                                        <div className="mb-4 flex flex-wrap items-center gap-3">
                                            <h1 className="text-2xl font-bold tracking-tight">
                                                {getProblemTitle(problem)}
                                            </h1>

                                            <span
                                                className={`rounded-full border px-3 py-1 text-xs font-semibold capitalize ${getDifficultyClass(
                                                    problem?.difficulty,
                                                )}`}
                                            >
                        {problem?.difficulty || 'Unspecified'}
                      </span>
                                        </div>

                                        <div className="mb-6 flex flex-wrap gap-2 text-xs text-slate-400">
                                            {problem?.topic && (
                                                <span className="rounded-lg border border-slate-800 bg-slate-900 px-3 py-1.5">
                          {problem.topic}
                        </span>
                                            )}

                                            {problem?.tags?.map?.((tag, index) => (
                                                <span
                                                    key={`${tag}-${index}`}
                                                    className="rounded-lg border border-slate-800 bg-slate-900 px-3 py-1.5"
                                                >
                          {typeof tag === 'string' ? tag : tag?.name}
                        </span>
                                            ))}
                                        </div>

                                        <div className="space-y-5 text-sm leading-7 text-slate-300">
                                            <div>
                                                <h2 className="mb-2 text-base font-semibold text-white">
                                                    Problem Statement
                                                </h2>
                                                <div className="whitespace-pre-wrap">
                                                    {problem?.description ||
                                                        problem?.statement ||
                                                        problem?.problemDescription ||
                                                        'No problem description is available.'}
                                                </div>
                                            </div>

                                            {(problem?.inputFormat || problem?.inputDescription) && (
                                                <div>
                                                    <h2 className="mb-2 text-base font-semibold text-white">
                                                        Input Format
                                                    </h2>
                                                    <div className="whitespace-pre-wrap">
                                                        {problem.inputFormat || problem.inputDescription}
                                                    </div>
                                                </div>
                                            )}

                                            {(problem?.outputFormat || problem?.outputDescription) && (
                                                <div>
                                                    <h2 className="mb-2 text-base font-semibold text-white">
                                                        Output Format
                                                    </h2>
                                                    <div className="whitespace-pre-wrap">
                                                        {problem.outputFormat || problem.outputDescription}
                                                    </div>
                                                </div>
                                            )}

                                            {problem?.constraints && (
                                                <div>
                                                    <h2 className="mb-2 text-base font-semibold text-white">
                                                        Constraints
                                                    </h2>
                                                    <pre className="whitespace-pre-wrap rounded-xl border border-slate-800 bg-[#090d17] p-4 font-mono text-xs leading-6 text-cyan-200">
                            {Array.isArray(problem.constraints)
                                ? problem.constraints.join('\n')
                                : problem.constraints}
                          </pre>
                                                </div>
                                            )}

                                            {Array.isArray(problem?.examples) &&
                                                problem.examples.length > 0 && (
                                                    <div>
                                                        <h2 className="mb-3 text-base font-semibold text-white">
                                                            Examples
                                                        </h2>

                                                        <div className="space-y-4">
                                                            {problem.examples.map((example, index) => (
                                                                <div
                                                                    key={example.id ?? index}
                                                                    className="rounded-xl border border-slate-800 bg-[#090d17] p-4"
                                                                >
                                                                    <div className="mb-3 font-semibold text-slate-200">
                                                                        Example {index + 1}
                                                                    </div>

                                                                    <div className="space-y-3">
                                                                        <div>
                                                                            <div className="mb-1 text-xs text-slate-500">
                                                                                Input
                                                                            </div>
                                                                            <pre className="whitespace-pre-wrap font-mono text-xs text-cyan-200">
                                        {formatValue(example.input)}
                                      </pre>
                                                                        </div>

                                                                        <div>
                                                                            <div className="mb-1 text-xs text-slate-500">
                                                                                Output
                                                                            </div>
                                                                            <pre className="whitespace-pre-wrap font-mono text-xs text-emerald-300">
                                        {formatValue(example.output ?? example.expectedOutput)}
                                      </pre>
                                                                        </div>

                                                                        {example.explanation && (
                                                                            <div>
                                                                                <div className="mb-1 text-xs text-slate-500">
                                                                                    Explanation
                                                                                </div>
                                                                                <p className="text-xs leading-6 text-slate-400">
                                                                                    {example.explanation}
                                                                                </p>
                                                                            </div>
                                                                        )}
                                                                    </div>
                                                                </div>
                                                            ))}
                                                        </div>
                                                    </div>
                                                )}
                                        </div>
                                    </div>
                                ) : (
                                    <div>
                                        <div className="mb-5 flex flex-wrap items-start justify-between gap-3">
                                            <div>
                                                <h2 className="text-lg font-bold">
                                                    {runResult ? 'Run Results' : 'Submission Results'}
                                                </h2>
                                                <p className="mt-1 text-xs text-slate-500">
                                                    {runResult
                                                        ? 'Results from the public test cases.'
                                                        : submission
                                                            ? 'Results from your code submission.'
                                                            : 'Run your code or submit it to see the results here.'}
                                                </p>
                                            </div>

                                            {currentResult && (
                                                <StatusBadge status={resultStatus} />
                                            )}
                                        </div>

                                        {submitError && (
                                            <div className="mb-4 flex gap-3 rounded-xl border border-rose-500/20 bg-rose-500/5 p-4">
                                                <AlertCircle
                                                    className="mt-0.5 shrink-0 text-rose-400"
                                                    size={18}
                                                />
                                                <div>
                                                    <div className="mb-1 text-sm font-semibold text-rose-300">
                                                        Request failed
                                                    </div>
                                                    <p className="whitespace-pre-wrap break-words text-sm text-slate-300">
                                                        {submitError}
                                                    </p>
                                                </div>
                                            </div>
                                        )}

                                        {notice && (
                                            <div className="mb-4 rounded-xl border border-cyan-500/20 bg-cyan-500/5 p-4 text-sm text-cyan-200">
                                                {notice}
                                            </div>
                                        )}

                                        {(running || submitting) && (
                                            <div className="mb-4 flex items-center gap-3 rounded-xl border border-violet-500/20 bg-violet-500/5 p-4">
                                                <Loader2
                                                    className="animate-spin text-violet-400"
                                                    size={20}
                                                />
                                                <div>
                                                    <div className="text-sm font-semibold">
                                                        {running ? 'Running your code...' : 'Submitting your solution...'}
                                                    </div>
                                                    <div className="mt-1 text-xs text-slate-400">
                                                        Please wait while the backend processes your request.
                                                    </div>
                                                </div>
                                            </div>
                                        )}

                                        {currentResult && (
                                            <>
                                                <div className="mb-5 grid grid-cols-2 gap-3">
                                                    <MetricCard
                                                        icon={CheckCircle2}
                                                        label="Test cases passed"
                                                        value={
                                                            totalCount > 0
                                                                ? `${passedCount}/${totalCount}`
                                                                : 'Not available'
                                                        }
                                                    />

                                                    <MetricCard
                                                        icon={Clock}
                                                        label="Execution time"
                                                        value={
                                                            executionTime !== null &&
                                                            executionTime !== undefined
                                                                ? `${executionTime} ms`
                                                                : 'Not reported'
                                                        }
                                                    />
                                                </div>

                                                {currentResult.output && (
                                                    <div className="mb-4">
                                                        <h3 className="mb-2 text-sm font-semibold">
                                                            Output
                                                        </h3>
                                                        <pre className="overflow-x-auto whitespace-pre-wrap rounded-xl border border-slate-800 bg-[#080b14] p-4 font-mono text-xs leading-6 text-emerald-300">
                              {currentResult.output}
                            </pre>
                                                    </div>
                                                )}

                                                {(currentResult.errorMessage ||
                                                    currentResult.error) && (
                                                    <div className="mb-4">
                                                        <h3 className="mb-2 text-sm font-semibold text-rose-300">
                                                            Error
                                                        </h3>
                                                        <pre className="overflow-x-auto whitespace-pre-wrap rounded-xl border border-rose-500/20 bg-rose-500/5 p-4 font-mono text-xs leading-6 text-rose-200">
                              {currentResult.errorMessage ||
                                  currentResult.error}
                            </pre>
                                                    </div>
                                                )}

                                                {testCases.length > 0 && (
                                                    <div>
                                                        <h3 className="mb-3 text-sm font-semibold">
                                                            Test Case Details
                                                        </h3>

                                                        <div className="space-y-3">
                                                            {testCases.map((testCase, index) => (
                                                                <div
                                                                    key={testCase.testCaseId ?? testCase.id ?? index}
                                                                    className="overflow-hidden rounded-xl border border-slate-800 bg-[#090d17]"
                                                                >
                                                                    <div className="flex items-center justify-between gap-3 border-b border-slate-800 px-4 py-3">
                                                                        <div className="flex items-center gap-2 text-sm font-medium">
                                                                            {testCase.passed ? (
                                                                                <CheckCircle2
                                                                                    size={16}
                                                                                    className="text-emerald-400"
                                                                                />
                                                                            ) : (
                                                                                <XCircle
                                                                                    size={16}
                                                                                    className="text-rose-400"
                                                                                />
                                                                            )}
                                                                            Test Case {index + 1}
                                                                        </div>

                                                                        <span
                                                                            className={`text-xs font-semibold ${
                                                                                testCase.passed
                                                                                    ? 'text-emerald-400'
                                                                                    : 'text-rose-400'
                                                                            }`}
                                                                        >
                                      {testCase.passed ? 'Passed' : 'Failed'}
                                    </span>
                                                                    </div>

                                                                    <div className="space-y-3 p-4">
                                                                        {testCase.input !== null &&
                                                                            testCase.input !== undefined && (
                                                                                <div>
                                                                                    <div className="mb-1 text-xs text-slate-500">
                                                                                        Input
                                                                                    </div>
                                                                                    <pre className="overflow-x-auto whitespace-pre-wrap font-mono text-xs text-cyan-200">
                                            {formatValue(testCase.input)}
                                          </pre>
                                                                                </div>
                                                                            )}

                                                                        {testCase.expectedOutput !== null &&
                                                                            testCase.expectedOutput !== undefined && (
                                                                                <div>
                                                                                    <div className="mb-1 text-xs text-slate-500">
                                                                                        Expected Output
                                                                                    </div>
                                                                                    <pre className="overflow-x-auto whitespace-pre-wrap font-mono text-xs text-slate-300">
                                            {formatValue(testCase.expectedOutput)}
                                          </pre>
                                                                                </div>
                                                                            )}

                                                                        {testCase.actualOutput !== null &&
                                                                            testCase.actualOutput !== undefined && (
                                                                                <div>
                                                                                    <div className="mb-1 text-xs text-slate-500">
                                                                                        Your Output
                                                                                    </div>
                                                                                    <pre className="overflow-x-auto whitespace-pre-wrap font-mono text-xs text-emerald-300">
                                            {formatValue(testCase.actualOutput)}
                                          </pre>
                                                                                </div>
                                                                            )}

                                                                        {testCase.error && (
                                                                            <div>
                                                                                <div className="mb-1 text-xs text-rose-400">
                                                                                    Error
                                                                                </div>
                                                                                <pre className="overflow-x-auto whitespace-pre-wrap font-mono text-xs text-rose-300">
                                          {testCase.error}
                                        </pre>
                                                                            </div>
                                                                        )}
                                                                    </div>
                                                                </div>
                                                            ))}
                                                        </div>
                                                    </div>
                                                )}

                                                {currentResult.id && (
                                                    <p className="mt-4 text-xs text-slate-500">
                                                        Submission ID: {currentResult.id}
                                                    </p>
                                                )}
                                            </>
                                        )}

                                        {!currentResult &&
                                            !running &&
                                            !submitting &&
                                            !submitError &&
                                            !notice && (
                                                <div className="flex min-h-64 flex-col items-center justify-center text-center">
                                                    <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl border border-slate-800 bg-slate-900">
                                                        <Terminal
                                                            size={24}
                                                            className="text-slate-500"
                                                        />
                                                    </div>
                                                    <h3 className="mb-2 font-semibold text-slate-200">
                                                        No results yet
                                                    </h3>
                                                    <p className="max-w-xs text-sm leading-6 text-slate-500">
                                                        Click Run Code to test your solution against public
                                                        test cases, or Submit Code to submit it for judging.
                                                    </p>
                                                </div>
                                            )}
                                    </div>
                                )}
                            </div>
                        </section>

                        {/* Right panel: code editor */}
                        <section className="flex min-h-[600px] flex-col overflow-hidden rounded-2xl border border-slate-800 bg-[#0d1220]">
                            <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-800 px-4 py-3">
                                <div className="flex items-center gap-2">
                                    <Code2 size={17} className="text-violet-400" />
                                    <h2 className="text-sm font-semibold">Code Editor</h2>
                                </div>

                                <div className="flex items-center gap-2">
                                    <div className="relative">
                                        <select
                                            value={language}
                                            onChange={(event) =>
                                                changeLanguage(event.target.value)
                                            }
                                            disabled={running || submitting}
                                            className="appearance-none rounded-lg border border-slate-700 bg-[#090d17] py-2 pl-3 pr-9 text-sm text-slate-200 outline-none transition focus:border-violet-500 disabled:opacity-50"
                                            aria-label="Programming language"
                                        >
                                            {LANGUAGE_OPTIONS.map((option) => (
                                                <option
                                                    key={option.value}
                                                    value={option.value}
                                                >
                                                    {option.label}
                                                </option>
                                            ))}
                                        </select>
                                        <ChevronDown
                                            size={14}
                                            className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-slate-400"
                                        />
                                    </div>

                                    <button
                                        onClick={() => setShowResetConfirm(true)}
                                        disabled={running || submitting}
                                        className="rounded-lg border border-slate-700 p-2 text-slate-400 transition hover:bg-slate-800 hover:text-white disabled:opacity-50"
                                        title="Reset code"
                                    >
                                        <RotateCcw size={16} />
                                    </button>
                                </div>
                            </div>

                            <div className="flex flex-wrap items-center justify-between gap-2 border-b border-slate-800 bg-[#0a0e19] px-4 py-2">
                                <div className="flex items-center gap-2 text-xs text-slate-500">
                                    <span className="h-2 w-2 rounded-full bg-cyan-400" />
                                    {LANGUAGE_OPTIONS.find(
                                        (option) => option.value === language,
                                    )?.label || language}
                                    <span className="text-slate-700">/</span>
                                    <span>UTF-8</span>
                                </div>

                                <div className="flex items-center gap-3">
                                    <label className="flex items-center gap-2 text-xs text-slate-500">
                                        Font size
                                        <select
                                            value={fontSize}
                                            onChange={(event) =>
                                                setFontSize(Number(event.target.value))
                                            }
                                            className="rounded border border-slate-700 bg-[#090d17] px-2 py-1 text-xs text-slate-300 outline-none"
                                        >
                                            {[12, 13, 14, 15, 16, 18, 20].map((size) => (
                                                <option key={size} value={size}>
                                                    {size}
                                                </option>
                                            ))}
                                        </select>
                                    </label>
                                </div>
                            </div>

                            <div className="min-h-[450px] flex-1">
                                <Editor
                                    height="100%"
                                    language={MONACO_LANGUAGE[language] || 'java'}
                                    theme="vs-dark"
                                    value={code}
                                    onChange={(value) => setCode(value ?? '')}
                                    options={{
                                        fontSize,
                                        minimap: { enabled: false },
                                        automaticLayout: true,
                                        scrollBeyondLastLine: false,
                                        wordWrap: 'on',
                                        tabSize: language === 'PYTHON' ? 4 : 4,
                                        insertSpaces: true,
                                        formatOnPaste: true,
                                        padding: { top: 16, bottom: 16 },
                                        lineNumbers: 'on',
                                        renderLineHighlight: 'all',
                                        smoothScrolling: true,
                                        cursorBlinking: 'smooth',
                                        readOnly: running || submitting,
                                    }}
                                />
                            </div>

                            <div className="border-t border-slate-800 bg-[#0a0e19] p-4">
                                <div className="mb-3 flex flex-wrap items-center justify-between gap-2">
                                    <p className="text-xs leading-5 text-slate-500">
                                        Run tests first, then submit your solution.
                                    </p>

                                    <button
                                        onClick={() => {
                                            setRunResult(null)
                                            setSubmission(null)
                                            setSubmitError('')
                                            setNotice('')
                                            setActiveTab('description')
                                        }}
                                        className="flex items-center gap-1.5 text-xs text-slate-500 transition hover:text-slate-300"
                                    >
                                        <RefreshCw size={12} />
                                        Clear results
                                    </button>
                                </div>

                                <div className="flex flex-col gap-3 sm:flex-row">
                                    <button
                                        onClick={handleRun}
                                        disabled={running || submitting || !problem}
                                        className="inline-flex flex-1 items-center justify-center gap-2 rounded-xl border border-slate-700 bg-slate-800 px-4 py-3 text-sm font-semibold text-slate-100 transition hover:border-slate-600 hover:bg-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
                                    >
                                        {running ? (
                                            <Loader2 size={17} className="animate-spin" />
                                        ) : (
                                            <Play size={17} />
                                        )}
                                        {running ? 'Running...' : 'Run Code'}
                                    </button>

                                    <button
                                        onClick={handleSubmit}
                                        disabled={submitting || running || !problem}
                                        className="inline-flex flex-1 items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-violet-600 to-indigo-600 px-4 py-3 text-sm font-semibold text-white shadow-lg shadow-violet-950/20 transition hover:from-violet-500 hover:to-indigo-500 disabled:cursor-not-allowed disabled:opacity-50"
                                    >
                                        {submitting ? (
                                            <Loader2 size={17} className="animate-spin" />
                                        ) : (
                                            <Send size={16} />
                                        )}
                                        {submitting ? 'Submitting...' : 'Submit Code'}
                                    </button>
                                </div>
                            </div>
                        </section>
                    </div>
                )}
            </main>

            {/* Reset confirmation modal */}
            {showResetConfirm && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4 backdrop-blur-sm">
                    <div className="w-full max-w-sm rounded-2xl border border-slate-700 bg-[#111827] p-6 shadow-2xl">
                        <div className="mb-4 flex h-11 w-11 items-center justify-center rounded-xl bg-amber-500/10 text-amber-400">
                            <RotateCcw size={21} />
                        </div>

                        <h2 className="mb-2 text-lg font-bold">Reset code?</h2>
                        <p className="mb-6 text-sm leading-6 text-slate-400">
                            This will replace your current editor contents with the default
                            starter code for {language}. Your current code will be lost.
                        </p>

                        <div className="flex gap-3">
                            <button
                                onClick={() => setShowResetConfirm(false)}
                                className="flex-1 rounded-lg border border-slate-700 px-4 py-2.5 text-sm font-medium text-slate-300 transition hover:bg-slate-800"
                            >
                                Cancel
                            </button>

                            <button
                                onClick={resetCode}
                                className="flex-1 rounded-lg bg-violet-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-violet-500"
                            >
                                Reset code
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

export default ProblemDetails