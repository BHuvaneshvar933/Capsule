import React, { useState, useRef, useEffect } from 'react';
import EmojiPicker, { Theme } from 'emoji-picker-react';

const HabitIconPicker = ({ icon, onIconChange, className, placeholder = "Icon" }) => {
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef(null);

  // Close when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (containerRef.current && !containerRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const onEmojiClick = (emojiObject) => {
    onIconChange(emojiObject.emoji);
    setIsOpen(false);
  };

  return (
    <div className={`relative ${className || ''}`} ref={containerRef}>
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        className="input-field w-full h-full min-w-[64px] flex items-center justify-center text-xl bg-[#0a0a0a] hover:bg-dark-900 transition-colors"
        aria-label="Select Habit Icon"
      >
        {icon ? icon : <span className="text-dark-500 text-sm">{placeholder}</span>}
      </button>

      {isOpen && (
        <div className="absolute z-50 mt-2 left-0 shadow-neu-elevated border border-white/5 rounded-2xl bg-[#0a0a0a] overflow-hidden animate-scale-in">
          <EmojiPicker
            onEmojiClick={onEmojiClick}
            autoFocusSearch={false}
            theme={Theme.DARK}
            searchDisabled={false}
            skinTonesDisabled
            width={320}
            height={400}
            previewConfig={{ showPreview: false }}
          />
        </div>
      )}
    </div>
  );
};

export default HabitIconPicker;
