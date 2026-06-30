import { useState, useRef, useEffect } from 'react';
import api from '../api/axios';
import { Sparkles, X, Loader2, Send } from 'lucide-react';

export default function JoiChat() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([{ role: 'assistant', content: 'Hello! I am Joi. How can I help you today?' }]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);

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
      {/* Floating Action Button (FAB) for triggering the Joi chat overlay */}
      <button
        onClick={() => setIsOpen(true)}
        className={`fixed bottom-6 right-6 p-4 rounded-full bg-gradient-to-r from-primary-500 to-accent-600 text-white shadow-xl shadow-primary-500/20 hover:scale-105 transition-transform z-50 ${isOpen ? 'hidden' : 'flex'} items-center justify-center`}
      >
        <Sparkles className="w-6 h-6" />
      </button>

      {/* Main Chat Interface Overlay */}
      {isOpen && (
        <div className="fixed bottom-6 right-6 w-80 sm:w-96 h-[500px] max-h-[80vh] bg-dark-800/95 backdrop-blur-xl border border-dark-700/60 rounded-2xl shadow-2xl flex flex-col z-50 overflow-hidden">
          {/* Component Header */}
          <div className="p-4 border-b border-dark-700/50 bg-dark-900/50 flex items-center justify-between">
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
          <div className="flex-1 overflow-y-auto p-4 space-y-4">
            {messages.map((msg, i) => (
              <div key={i} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                <div className={`max-w-[85%] rounded-2xl px-4 py-2 text-sm whitespace-pre-wrap ${msg.role === 'user' ? 'bg-primary-600 text-white rounded-br-none' : 'bg-dark-700 text-dark-100 rounded-bl-none'}`}>
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
          <div className="p-4 border-t border-dark-700/50 bg-dark-900/50">
            <form onSubmit={handleSend} className="flex gap-2">
              <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Ask Joi..."
                className="flex-1 bg-dark-800 border border-dark-600 rounded-xl px-4 py-2 text-sm text-white focus:outline-none focus:border-primary-500 transition-colors"
              />
              <button
                type="submit"
                disabled={!input.trim() || isLoading}
                className="p-2 bg-primary-600 text-white rounded-xl hover:bg-primary-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                <Send className="w-4 h-4" />
              </button>
            </form>
          </div>
        </div>
      )}
    </>
  );
}
