import MainLayout from '../../layouts/MainLayout'
import {
  MessageSquare,
  Search,
  Plus,
  ThumbsUp,
  MessageCircle
} from 'lucide-react'

function Discussions() {
  const discussions = [
    {
      title: 'Best approach for solving Two Sum?',
      author: 'Rohit',
      category: 'Algorithms',
      replies: 12,
      likes: 24
    },
    {
      title: 'How should I start Dynamic Programming?',
      author: 'Alex',
      category: 'DSA',
      replies: 18,
      likes: 37
    },
    {
      title: 'Java vs C++ for competitive programming',
      author: 'Rahul',
      category: 'Programming',
      replies: 9,
      likes: 16
    }
  ]

  return (
    <MainLayout>
      <div className="min-h-screen bg-[#0f1117] p-6">
        <div className="mx-auto max-w-7xl">

          <div className="mb-8 flex flex-col justify-between gap-4 md:flex-row md:items-end">
            <div>
              <p className="mb-2 text-sm font-medium text-orange-500">
                DISCUSSIONS
              </p>

              <h1 className="text-3xl font-bold text-white">
                Community Discussions
              </h1>

              <p className="mt-2 text-gray-500">
                Ask questions, share solutions and learn from other developers.
              </p>
            </div>

            <button className="flex items-center justify-center gap-2 rounded-lg bg-orange-500 px-5 py-3 font-semibold text-white hover:bg-orange-600">
              <Plus size={18} />
              New Discussion
            </button>
          </div>

          <div className="mb-6 relative">
            <Search
              size={18}
              className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500"
            />

            <input
              type="text"
              placeholder="Search discussions..."
              className="w-full rounded-xl border border-gray-800 bg-[#181b24] py-3 pl-11 pr-4 text-sm text-white outline-none focus:border-orange-500"
            />
          </div>

          <div className="space-y-4">
            {discussions.map((discussion) => (
              <div
                key={discussion.title}
                className="rounded-xl border border-gray-800 bg-[#181b24] p-5 transition hover:border-gray-700"
              >
                <div className="flex gap-4">

                  <div className="hidden h-11 w-11 shrink-0 items-center justify-center rounded-lg bg-orange-500/10 sm:flex">
                    <MessageSquare
                      size={21}
                      className="text-orange-500"
                    />
                  </div>

                  <div className="min-w-0 flex-1">

                    <h2 className="text-lg font-semibold text-white hover:text-orange-500">
                      {discussion.title}
                    </h2>

                    <div className="mt-2 flex flex-wrap items-center gap-3 text-xs text-gray-500">
                      <span>by {discussion.author}</span>

                      <span className="rounded-full bg-gray-800 px-2.5 py-1 text-gray-400">
                        {discussion.category}
                      </span>
                    </div>

                    <div className="mt-4 flex items-center gap-5 text-sm text-gray-500">

                      <span className="flex items-center gap-1.5">
                        <ThumbsUp size={16} />
                        {discussion.likes}
                      </span>

                      <span className="flex items-center gap-1.5">
                        <MessageCircle size={16} />
                        {discussion.replies} replies
                      </span>

                    </div>

                  </div>

                </div>
              </div>
            ))}
          </div>

        </div>
      </div>
    </MainLayout>
  )
}
export default Discussions