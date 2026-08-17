import { useEffect, useMemo, useState } from "react"
import { Home, BriefcaseBusiness, CheckSquare, Timer, CheckCircle2, Bookmark, BarChart3, Sparkles, Settings } from "lucide-react"
import { useLocation, NavLink } from "react-router-dom"
import TopBar from "./TopBar"
import BottomNav from "./BottomNav"
import MoreSheet from "./MoreSheet"
import BackendWakeBanner from "../components/BackendWakeBanner"
import { MobileChromeContext } from "./chrome"
import JoiChat from "../components/JoiChat"

export default function AppLayout({ children }) {
  const [moreOpen, setMoreOpen] = useState(false)
  const [topBarActions, setTopBarActions] = useState(null)
  const location = useLocation()

  const chromeValue = useMemo(() => ({ setTopBarActions }), [setTopBarActions])

  const hideBottomNav = useMemo(() => {
    const p = location.pathname
    return p.startsWith("/applications/")
  }, [location.pathname])

  const navGroups = useMemo(() => [
    {
      title: "Core",
      items: [
        { to: "/dashboard", label: "Dashboard", icon: <Home className="w-6 h-6" /> },
        { to: "/job-tracker", label: "Job Tracker", icon: <BriefcaseBusiness className="w-6 h-6" /> },
        { to: "/todos", label: "To-dos", icon: <CheckSquare className="w-6 h-6" /> },
      ]
    },
    {
      title: "Focus & Plan",
      items: [
        { to: "/pomodoro", label: "Pomodoro", icon: <Timer className="w-6 h-6" /> },
        { to: "/habits", label: "Habits", icon: <CheckCircle2 className="w-6 h-6" /> },
        { to: "/curator", label: "Curator", icon: <Bookmark className="w-6 h-6" /> },
      ]
    },
    {
      title: "Career Tools",
      items: [
        { to: "/analytics", label: "Analytics", icon: <BarChart3 className="w-6 h-6" /> },
        { to: "/ai", label: "AI Tools", icon: <Sparkles className="w-6 h-6" /> },
      ]
    },
    {
      title: "Account",
      items: [
        { to: "/settings", label: "Settings", icon: <Settings className="w-6 h-6" /> },
      ]
    }
  ], [])

  // Close the sheet on navigation.
  useEffect(() => {
    // Intentionally closing UI state on navigation.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setMoreOpen(false)
  }, [location.pathname])

  // Escape closes the sheet.
  useEffect(() => {
    if (typeof window === "undefined") return
    if (!moreOpen) return
    const onKeyDown = (e) => {
      if (e.key === "Escape") setMoreOpen(false)
    }
    window.addEventListener("keydown", onKeyDown)
    return () => window.removeEventListener("keydown", onKeyDown)
  }, [moreOpen])

  // Prevent background scroll when the sheet is open.
  useEffect(() => {
    if (typeof document === "undefined") return
    if (!moreOpen) return

    const prevBodyOverflow = document.body.style.overflow
    const prevHtmlOverflow = document.documentElement.style.overflow
    document.body.style.overflow = "hidden"
    document.documentElement.style.overflow = "hidden"

    return () => {
      document.body.style.overflow = prevBodyOverflow
      document.documentElement.style.overflow = prevHtmlOverflow
    }
  }, [moreOpen])

  return (
    <MobileChromeContext.Provider value={chromeValue}>
      <div className="min-h-dvh bg-transparent overflow-x-hidden relative flex flex-col">
        <TopBar onOpenMenu={() => setMoreOpen(true)} actions={topBarActions} />
        <main className="w-full mx-auto px-4 pb-28 flex-1">
          <BackendWakeBanner />
          <div className="w-full">{children}</div>
        </main>

        {!hideBottomNav ? (
          <nav className="fixed bottom-0 inset-x-0 z-40 bg-[#0a0a0a]/90 backdrop-blur-xl border-t border-white/5 pb-safe shadow-[0_-10px_40px_rgba(0,0,0,0.8)]">
            <div className="flex items-center justify-around h-16 px-2">
              <NavLink to="/dashboard" className={({isActive}) => `flex flex-col items-center justify-center w-full h-full space-y-1 transition-colors ${isActive ? 'text-primary-400' : 'text-textMuted hover:text-white'}`}>
                <Home className="w-5 h-5" />
                <span className="text-[10px] font-bold tracking-wide">Home</span>
              </NavLink>
              <NavLink to="/job-tracker" className={({isActive}) => `flex flex-col items-center justify-center w-full h-full space-y-1 transition-colors ${isActive ? 'text-primary-400' : 'text-textMuted hover:text-white'}`}>
                <BriefcaseBusiness className="w-5 h-5" />
                <span className="text-[10px] font-bold tracking-wide">Career</span>
              </NavLink>
              
              {/* Central Joi AI Button */}
              <button 
                onClick={() => window.dispatchEvent(new CustomEvent('open-joi'))}
                className="relative -top-4 shrink-0 flex flex-col items-center justify-center w-14 h-14 bg-gradient-to-tr from-primary-500 to-accent-600 rounded-full text-white shadow-xl shadow-primary-500/20 hover:scale-105 transition-transform"
                aria-label="Open Joi AI"
              >
                <Sparkles className="w-6 h-6" />
              </button>

              <NavLink to="/todos" className={({isActive}) => `flex flex-col items-center justify-center w-full h-full space-y-1 transition-colors ${isActive ? 'text-primary-400' : 'text-textMuted hover:text-white'}`}>
                <CheckSquare className="w-5 h-5" />
                <span className="text-[10px] font-bold tracking-wide">Focus</span>
              </NavLink>
              <button onClick={() => setMoreOpen(true)} className="flex flex-col items-center justify-center w-full h-full space-y-1 text-textMuted hover:text-white transition-colors">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" /></svg>
                <span className="text-[10px] font-bold tracking-wide">Menu</span>
              </button>
            </div>
          </nav>
        ) : null}

        {/* Mobile Bottom Sheet (Hub) */}
        <div className={`fixed inset-0 z-50 transition-opacity duration-300 ${moreOpen ? 'opacity-100 pointer-events-auto' : 'opacity-0 pointer-events-none'}`}>
          <div className="absolute inset-0 bg-black/70 backdrop-blur-sm" onClick={() => setMoreOpen(false)} />
          <div className={`absolute bottom-0 inset-x-0 bg-[#0c0c0c] border-t border-white/10 rounded-t-3xl shadow-[0_-20px_50px_rgba(0,0,0,0.9)] transform transition-transform duration-300 ease-out ${moreOpen ? 'translate-y-0' : 'translate-y-full'}`}>
            <div className="flex justify-center pt-3 pb-2">
              <div className="w-12 h-1.5 bg-white/20 rounded-full" />
            </div>
            
            <div className="p-6 pt-2 pb-safe max-h-[85vh] overflow-y-auto">
              <div className="flex items-center justify-between mb-8">
                <div>
                  <h2 className="text-xl font-bold text-white tracking-tight">Capsule</h2>
                  <p className="text-xs text-primary-400 font-medium">Keep it together</p>
                </div>
                <button onClick={() => setMoreOpen(false)} className="p-2 bg-white/5 hover:bg-white/10 rounded-full text-white transition-colors">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" /></svg>
                </button>
              </div>

              <div className="space-y-8">
                {navGroups.map(group => (
                  <div key={group.title}>
                    <h3 className="text-[11px] font-bold text-textMuted uppercase tracking-widest mb-4 ml-1">{group.title}</h3>
                    <div className="grid grid-cols-4 gap-4">
                      {group.items.map(item => (
                        <NavLink key={item.to} to={item.to} onClick={() => setMoreOpen(false)} className="flex flex-col items-center gap-2 group">
                          <div className="w-14 h-14 bg-white/5 border border-white/10 rounded-2xl flex items-center justify-center text-textSecondary group-hover:bg-primary-500/20 group-hover:text-primary-400 group-hover:border-primary-500/30 transition-all shadow-neu-flat">
                            {item.icon}
                          </div>
                          <span className="text-[10px] font-semibold text-textSecondary text-center whitespace-nowrap overflow-hidden text-ellipsis w-full px-1">{item.label}</span>
                        </NavLink>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
        <JoiChat />
      </div>
    </MobileChromeContext.Provider>
  )
}
