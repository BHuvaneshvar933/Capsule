import { useState, useRef, useEffect } from 'react';
import api from '../api/axios';
import { Sparkles, X, Loader2, Send } from 'lucide-react';

export default function JoiChat() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([{ role: 'assistant', content: 'Hello! I am Joi. How can I help you today?' }]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);
  const chatContainerRef = useRef(null);

  useEffect(() => {
    const handleOpen = () => setIsOpen(true);
    window.addEventListener('open-joi', handleOpen);
    
    const handleClickOutside = (event) => {
      // Don't close if they clicked the toggle button
      if (chatContainerRef.current && !chatContainerRef.current.contains(event.target) && event.target.closest('button')?.getAttribute('aria-label') !== 'Open Joi AI') {
        setIsOpen(false);
      }
    };
    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => {
      window.removeEventListener('open-joi', handleOpen);
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    if (isOpen) scrollToBottom();
  }, [messages, isOpen]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMsg = input.trim();
    setInput('');
    setMessages(prev => [...prev, { role: 'user', content: userMsg }]);
    setIsLoading(true);

    try {
      const response = await api.post('/api/Joi/chat', { message: userMsg });
      
      setMessages(prev => [...prev, { role: 'assistant', content: response.data.response }]);
    } catch (error) {
      setMessages(prev => [...prev, { role: 'assistant', content: 'Sorry, I encountered an error. Is the AI provider online?' }]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      {/* Floating Action Button (FAB) for Desktop only */}
      <button
        onClick={() => setIsOpen(true)}
        aria-label="Open Joi AI"
        className={`fixed bottom-6 right-6 p-4 rounded-full bg-gradient-to-r from-primary-500 to-accent-600 text-white shadow-xl shadow-primary-500/20 hover:scale-105 transition-transform z-50 ${isOpen ? 'hidden' : 'hidden md:flex'} items-center justify-center`}
      >
        <Sparkles className="w-6 h-6" />
      </button>

      {/* Main Chat Interface Overlay */}
      {isOpen && (
        <div 
          ref={chatContainerRef} 
          className="fixed inset-0 md:inset-auto md:bottom-6 md:right-6 w-full md:w-96 h-dvh md:h-[500px] md:max-h-[70vh] bg-[#050505] md:bg-dark-800/95 md:backdrop-blur-xl md:border md:border-dark-700/60 md:rounded-2xl shadow-2xl flex flex-col z-[100] md:z-50 overflow-hidden animate-fade-in-up md:animate-scale-in"
        >
          {/* Component Header */}
          <div className="p-4 pt-[calc(env(safe-area-inset-top)+1rem)] md:pt-4 border-b border-dark-700/50 bg-[#0a0a0a] md:bg-dark-900/50 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="p-1.5 bg-primary-500/20 rounded-lg text-primary-400">
                <Sparkles className="w-5 h-5" />
              </div>
              <h3 className="font-bold text-white">Joi</h3>
            </div>
            <button onClick={() => setIsOpen(false)} className="text-dark-400 hover:text-white transition-colors">
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Messages */}
          <div className="flex-1 overflow-y-auto p-4 md:p-6 space-y-6">
            {messages.map((msg, i) => (
              <div key={i} className={`flex gap-3 ${msg.role === 'user' ? 'flex-row-reverse' : 'flex-row'}`}>
                {msg.role === 'assistant' && (
                  <div className="w-8 h-8 rounded-full bg-primary-500/20 text-primary-400 flex items-center justify-center shrink-0 mt-1">
                    <Sparkles className="w-4 h-4" />
                  </div>
                )}
                <div className={`max-w-[85%] rounded-2xl px-4 py-3 text-[15px] leading-relaxed whitespace-pre-wrap shadow-sm ${msg.role === 'user' ? 'bg-primary-600 text-white rounded-tr-sm' : 'bg-dark-800 border border-white/5 text-dark-100 rounded-tl-sm'}`}>
                  {msg.content.replace(/\\n/g, '\n')}
                </div>
              </div>
            ))}
            {isLoading && (
              <div className="flex justify-start">
                <div className="bg-dark-700 text-dark-300 rounded-2xl rounded-bl-none px-4 py-2">
                  <Loader2 className="w-5 h-5 animate-spin" />
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Input Area */}
          <div className="p-4 pb-[calc(env(safe-area-inset-bottom)+1rem)] md:pb-4 border-t border-dark-700/50 bg-[#0a0a0a] md:bg-dark-900/50">
            <form onSubmit={handleSend} className="flex gap-2">
              <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Message Joi..."
                className="flex-1 bg-dark-800 border border-dark-600 rounded-xl px-4 py-3 min-h-[48px] text-[15px] text-white focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500/50 transition-all shadow-neu-pressed"
              />
              <button
                type="submit"
                disabled={!input.trim() || isLoading}
                className="p-3 bg-primary-600 text-white rounded-xl min-h-[48px] min-w-[48px] flex items-center justify-center hover:bg-primary-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors shadow-glow"
              >
                <Send className="w-5 h-5" />
              </button>
            </form>
          </div>
        </div>
      )}
    </>
  );
}
