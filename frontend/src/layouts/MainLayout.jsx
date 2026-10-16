import { useState } from 'react'
import { Menu } from 'lucide-react'

import Navbar from '../components/layout/Navbar'
import Sidebar from '../components/layout/Sidebar'
import Footer from '../components/layout/Footer'

function MainLayout({ children }) {

  const [mobileOpen, setMobileOpen] = useState(false)

  return (
    <div className="min-h-screen bg-[#0f1117] text-white">

      <Navbar />

      <Sidebar
        mobileOpen={mobileOpen}
        setMobileOpen={setMobileOpen}
      />

      <div className="pt-16 lg:pl-64">

        <button
          onClick={() => setMobileOpen(true)}
          className="fixed bottom-5 left-5 z-30 flex h-12 w-12 items-center justify-center rounded-full bg-orange-500 text-white shadow-lg lg:hidden"
        >
          <Menu size={22} />
        </button>

        <main className="min-h-[calc(100vh-4rem)]">
          {children}
        </main>

        <Footer />

      </div>

    </div>
  )
}

export default MainLayout