import MainLayout from '../../layouts/MainLayout'
import {
  Search,
  SlidersHorizontal,
  CheckCircle2,
  ChevronRight
} from 'lucide-react'
import { Link } from 'react-router-dom'

function Problems() {
  const problems = [
    {
      id: 1,
      title: 'Two Sum',
      difficulty: 'Easy',
      topics: ['Array', 'Hash Map'],
      solved: true
    },
    {
      id: 2,
      title: 'Add Two Numbers',
      difficulty: 'Medium',
      topics: ['Linked List', 'Math'],
      solved: false
    },
    {
      id: 3,
      title: 'Longest Substring Without Repeating Characters',
      difficulty: 'Medium',
      topics: ['Hash Map', 'Sliding Window'],
      solved: false
    },
    {
      id: 4,
      title: 'Median of Two Sorted Arrays',
      difficulty: 'Hard',
      topics: ['Array', 'Binary Search'],
      solved: false
    },
    {
      id: 5,
      title: 'Valid Parentheses',
      difficulty: 'Easy',
      topics: ['Stack'],
      solved: false
    },
    {
      id: 6,
      title: 'Merge Two Sorted Lists',
      difficulty: 'Easy',
      topics: ['Linked List'],
      solved: false
    },
    {
      id: 7,
      title: 'Maximum Subarray',
      difficulty: 'Medium',
      topics: ['Array', 'Dynamic Programming'],
      solved: false
    },
    {
      id: 8,
      title: 'Binary Tree Inorder Traversal',
      difficulty: 'Easy',
      topics: ['Tree', 'DFS'],
      solved: false
    }
  ]

  return (
    <MainLayout>
      <div className="min-h-screen bg-[#0f1117] p-6">
        <div className="mx-auto max-w-7xl">

          {/* Header */}
          <div className="mb-8">
            <p className="mb-2 text-sm font-medium text-orange-500">
              PROBLEM SET
            </p>

            <h1 className="text-3xl font-bold text-white">
              Problems
            </h1>

            <p className="mt-2 text-gray-500">
              Practice coding problems and improve your problem-solving skills.
            </p>
          </div>

          {/* Search + Filters */}
          <div className="mb-6 rounded-xl border border-gray-800 bg-[#181b24] p-4">

            <div className="flex flex-col gap-3 lg:flex-row">

              <div className="relative flex-1">
                <Search
                  size={18}
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
                />

                <input
                  type="text"
                  placeholder="Search problems..."
                  className="w-full rounded-lg border border-gray-800 bg-[#0f1117] py-3 pl-10 pr-4 text-sm text-white outline-none focus:border-orange-500"
                />
              </div>

              <select className="rounded-lg border border-gray-800 bg-[#0f1117] px-4 py-3 text-sm text-gray-300 outline-none focus:border-orange-500">
                <option>All Difficulties</option>
                <option>Easy</option>
                <option>Medium</option>
                <option>Hard</option>
              </select>

              <select className="rounded-lg border border-gray-800 bg-[#0f1117] px-4 py-3 text-sm text-gray-300 outline-none focus:border-orange-500">
                <option>All Topics</option>
                <option>Array</option>
                <option>Linked List</option>
                <option>Tree</option>
                <option>Graph</option>
                <option>Dynamic Programming</option>
              </select>

              <button className="flex items-center justify-center gap-2 rounded-lg border border-gray-700 px-4 py-3 text-sm text-gray-300 hover:bg-gray-800">
                <SlidersHorizontal size={17} />
                Filters
              </button>

            </div>
          </div>

          {/* Stats */}
          <div className="mb-5 flex flex-wrap gap-4 text-sm">
            <span className="text-gray-500">
              Total: <span className="text-white">8</span>
            </span>

            <span className="text-gray-500">
              Easy: <span className="text-green-400">4</span>
            </span>

            <span className="text-gray-500">
              Medium: <span className="text-yellow-400">3</span>
            </span>

            <span className="text-gray-500">
              Hard: <span className="text-red-400">1</span>
            </span>
          </div>

          {/* Problems */}
          <div className="overflow-hidden rounded-xl border border-gray-800 bg-[#181b24]">

            <div className="hidden grid-cols-[60px_1fr_130px_250px_50px] border-b border-gray-800 px-5 py-4 text-xs font-medium uppercase tracking-wide text-gray-500 md:grid">
              <span>#</span>
              <span>Problem</span>
              <span>Difficulty</span>
              <span>Topics</span>
              <span />
            </div>

            {problems.map((problem) => (
              <Link
                key={problem.id}
                to={`/problems/${problem.id}`}
                className="grid gap-3 border-b border-gray-800 px-5 py-5 transition last:border-b-0 hover:bg-gray-800/40 md:grid-cols-[60px_1fr_130px_250px_50px] md:items-center"
              >

                <span className="text-sm text-gray-500">
                  {problem.id}
                </span>

                <div className="flex items-center gap-3">

                  {problem.solved && (
                    <CheckCircle2
                      size={17}
                      className="shrink-0 text-green-500"
                    />
                  )}

                  <span className="text-sm font-medium text-gray-200">
                    {problem.title}
                  </span>

                </div>

                <div>
                  <Difficulty difficulty={problem.difficulty} />
                </div>

                <div className="flex flex-wrap gap-2">
                  {problem.topics.map((topic) => (
                    <span
                      key={topic}
                      className="rounded-md bg-gray-800 px-2 py-1 text-xs text-gray-400"
                    >
                      {topic}
                    </span>
                  ))}
                </div>

                <ChevronRight
                  size={18}
                  className="hidden text-gray-600 md:block"
                />

              </Link>
            ))}

          </div>

        </div>
      </div>
    </MainLayout>
  )
}

function Difficulty({ difficulty }) {
  const styles = {
    Easy: 'text-green-400 bg-green-500/10',
    Medium: 'text-yellow-400 bg-yellow-500/10',
    Hard: 'text-red-400 bg-red-500/10'
  }

  return (
    <span
      className={`inline-flex rounded-full px-3 py-1 text-xs font-medium ${styles[difficulty]}`}
    >
      {difficulty}
    </span>
  )
}

export default Problems