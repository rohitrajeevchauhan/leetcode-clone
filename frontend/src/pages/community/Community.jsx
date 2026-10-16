import MainLayout from '../../layouts/MainLayout'
import {
  Users,
  MessageSquare,
  Search,
  UserPlus
} from 'lucide-react'

function Community() {
  const members = [
    {
      name: 'Coding Community',
      description: 'Connect with developers and share your coding journey.',
      members: '1.2K members'
    },
    {
      name: 'DSA Masters',
      description: 'Discuss algorithms, data structures and interview preparation.',
      members: '856 members'
    },
    {
      name: 'Competitive Programmers',
      description: 'Improve problem solving and competitive programming skills.',
      members: '643 members'
    }
  ]

  return (
    <MainLayout>
      <div className="min-h-screen bg-[#0f1117] p-6">
        <div className="mx-auto max-w-7xl">

          <div className="mb-8">
            <p className="mb-2 text-sm font-medium text-orange-500">
              COMMUNITY
            </p>

            <h1 className="text-3xl font-bold text-white">
              Developer Community
            </h1>

            <p className="mt-2 text-gray-500">
              Connect, learn and grow with other developers.
            </p>
          </div>

          <div className="mb-6 flex flex-col gap-4 md:flex-row">
            <div className="relative flex-1">
              <Search
                size={18}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
              />

              <input
                type="text"
                placeholder="Search communities..."
                className="w-full rounded-xl border border-gray-800 bg-[#181b24] py-3 pl-10 pr-4 text-sm text-white outline-none focus:border-orange-500"
              />
            </div>

            <button className="flex items-center justify-center gap-2 rounded-xl bg-orange-500 px-5 py-3 font-semibold text-white hover:bg-orange-600">
              <UserPlus size={18} />
              Create Community
            </button>
          </div>

          <div className="grid gap-5 md:grid-cols-2 lg:grid-cols-3">
            {members.map((community) => (
              <div
                key={community.name}
                className="rounded-xl border border-gray-800 bg-[#181b24] p-6 transition hover:border-gray-700"
              >
                <div className="flex items-center justify-between">
                  <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-orange-500/10">
                    <Users
                      size={24}
                      className="text-orange-500"
                    />
                  </div>

                  <span className="text-xs text-gray-500">
                    {community.members}
                  </span>
                </div>

                <h2 className="mt-5 text-lg font-semibold text-white">
                  {community.name}
                </h2>

                <p className="mt-2 min-h-12 text-sm leading-6 text-gray-500">
                  {community.description}
                </p>

                <button className="mt-5 flex w-full items-center justify-center gap-2 rounded-lg border border-gray-700 py-2.5 text-sm font-medium text-gray-300 transition hover:bg-gray-800 hover:text-white">
                  <MessageSquare size={17} />
                  Join Community
                </button>
              </div>
            ))}
          </div>

        </div>
      </div>
    </MainLayout>
  )
}

export default Community