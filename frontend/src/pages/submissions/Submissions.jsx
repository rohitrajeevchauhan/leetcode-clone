import MainLayout from '../../layouts/MainLayout'
import {
  Send,
  CheckCircle2,
  XCircle,
  Clock,
  Search
} from 'lucide-react'

function Submissions() {
  const submissions = [
    {
      problem: 'Two Sum',
      language: 'Java',
      status: 'Accepted',
      runtime: '2 ms',
      memory: '42 MB',
      submitted: 'Today'
    },
    {
      problem: 'Valid Parentheses',
      language: 'Java',
      status: 'Wrong Answer',
      runtime: '—',
      memory: '—',
      submitted: 'Yesterday'
    },
    {
      problem: 'Merge Two Sorted Lists',
      language: 'Java',
      status: 'Accepted',
      runtime: '1 ms',
      memory: '41 MB',
      submitted: '2 days ago'
    }
  ]

  return (
    <MainLayout>
      <div className="min-h-screen bg-[#0f1117] p-6">
        <div className="mx-auto max-w-7xl">

          <div className="mb-8">
            <p className="mb-2 text-sm font-medium text-orange-500">
              SUBMISSIONS
            </p>

            <h1 className="text-3xl font-bold text-white">
              My Submissions
            </h1>

            <p className="mt-2 text-gray-500">
              View your coding submissions and results.
            </p>
          </div>

          <div className="mb-6 flex flex-col gap-3 md:flex-row">

            <div className="relative flex-1">
              <Search
                size={18}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
              />

              <input
                type="text"
                placeholder="Search submissions..."
                className="w-full rounded-xl border border-gray-800 bg-[#181b24] py-3 pl-10 pr-4 text-sm text-white outline-none focus:border-orange-500"
              />
            </div>

            <select className="rounded-xl border border-gray-800 bg-[#181b24] px-4 py-3 text-sm text-gray-300 outline-none">
              <option>All Statuses</option>
              <option>Accepted</option>
              <option>Wrong Answer</option>
              <option>Runtime Error</option>
            </select>

            <select className="rounded-xl border border-gray-800 bg-[#181b24] px-4 py-3 text-sm text-gray-300 outline-none">
              <option>All Languages</option>
              <option>Java</option>
              <option>Python</option>
              <option>C++</option>
              <option>JavaScript</option>
            </select>

          </div>

          <div className="overflow-hidden rounded-xl border border-gray-800 bg-[#181b24]">

            <div className="hidden grid-cols-[1fr_120px_150px_120px_120px_130px] border-b border-gray-800 px-5 py-4 text-xs font-medium uppercase tracking-wide text-gray-500 md:grid">
              <span>Problem</span>
              <span>Language</span>
              <span>Status</span>
              <span>Runtime</span>
              <span>Memory</span>
              <span>Submitted</span>
            </div>

            {submissions.map((submission, index) => (
              <div
                key={index}
                className="grid gap-4 border-b border-gray-800 px-5 py-5 last:border-b-0 md:grid-cols-[1fr_120px_150px_120px_120px_130px] md:items-center"
              >

                <div className="flex items-center gap-3">
                  <div className="rounded-lg bg-orange-500/10 p-2">
                    <Send
                      size={16}
                      className="text-orange-500"
                    />
                  </div>

                  <span className="text-sm font-medium text-gray-200">
                    {submission.problem}
                  </span>
                </div>

                <span className="text-sm text-gray-400">
                  {submission.language}
                </span>

                <Status status={submission.status} />

                <span className="flex items-center gap-2 text-sm text-gray-500">
                  <Clock size={15} />
                  {submission.runtime}
                </span>

                <span className="text-sm text-gray-500">
                  {submission.memory}
                </span>

                <span className="text-sm text-gray-500">
                  {submission.submitted}
                </span>

              </div>
            ))}

          </div>

        </div>
      </div>
    </MainLayout>
  )
}

function Status({ status }) {
  if (status === 'Accepted') {
    return (
      <span className="flex items-center gap-2 text-sm text-green-400">
        <CheckCircle2 size={17} />
        Accepted
      </span>
    )
  }

  return (
    <span className="flex items-center gap-2 text-sm text-red-400">
      <XCircle size={17} />
      {status}
    </span>
  )
}

export default Submissions