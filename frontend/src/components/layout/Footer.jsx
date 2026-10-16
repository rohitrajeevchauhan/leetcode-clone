import {
  Mail,
  Heart,
  Code2,
  ExternalLink
} from 'lucide-react'

function Footer() {
  return (
    <footer className="border-t border-gray-800 bg-[#0f1117]">
      <div className="mx-auto flex max-w-7xl flex-col items-center justify-between gap-4 px-6 py-6 md:flex-row">

        <div className="text-sm text-gray-500">
          © {new Date().getFullYear()} LeetClone. All rights reserved.
        </div>

        <div className="flex items-center gap-3">

          <a
            href="https://github.com"
            target="_blank"
            rel="noreferrer"
            className="flex items-center gap-2 rounded-lg border border-gray-800 px-3 py-2 text-sm text-gray-400 transition hover:border-gray-700 hover:bg-gray-800 hover:text-white"
          >
            <Code2 size={17} />
            GitHub
            <ExternalLink size={14} />
          </a>

          <a
            href="mailto:support@leetclone.com"
            className="flex items-center gap-2 rounded-lg border border-gray-800 px-3 py-2 text-sm text-gray-400 transition hover:border-gray-700 hover:bg-gray-800 hover:text-white"
          >
            <Mail size={17} />
            Contact
          </a>

        </div>

        <div className="flex items-center text-xs text-gray-500">
          Built with
          <Heart
            size={14}
            className="mx-1 text-red-500"
            fill="currentColor"
          />
          for developers
        </div>

      </div>
    </footer>
  )
}

export default Footer