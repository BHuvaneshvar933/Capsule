import React, { forwardRef } from 'react';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import { format, parseISO, isValid } from 'date-fns';

const CustomInput = forwardRef(({ value, onClick, placeholder, className, disabled }, ref) => (
  <button
    type="button"
    className={`input-field flex items-center justify-between cursor-pointer w-full text-left bg-[#0a0a0a] disabled:opacity-50 disabled:cursor-not-allowed ${className || ''}`}
    onClick={onClick}
    disabled={disabled}
    ref={ref}
  >
    <span className={value ? "text-textPrimary" : "text-textMuted"}>
      {value || placeholder || "Select date..."}
    </span>
    <svg className="w-5 h-5 text-dark-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
    </svg>
  </button>
));

const CustomDatePicker = ({ 
  value, 
  onChange, 
  showTime = false, 
  name, 
  className,
  placeholder,
  required,
  disabled,
  popperPlacement
}) => {
  // Convert string value to Date object for react-datepicker
  const parseValueToDate = (val) => {
    if (!val) return null;
    
    // Sometimes values come as YYYY-MM-DD or YYYY-MM-DDTHH:mm. 
    // parseISO handles these well.
    let parsed;
    try {
      parsed = typeof val === 'string' ? parseISO(val) : val;
    } catch (e) {
      return null;
    }
    
    // Check if it's a valid date
    if (parsed instanceof Date && !isNaN(parsed)) {
      return parsed;
    }
    return null;
  };

  const selectedDate = parseValueToDate(value);

  const handleChange = (date) => {
    if (!date) {
      onChange({ target: { name, value: '' } });
      return;
    }
    
    // Format back to string to act as a drop-in replacement for native inputs
    // type="date" expects 'yyyy-MM-dd'
    // type="datetime-local" expects 'yyyy-MM-ddTHH:mm'
    const formatString = showTime ? "yyyy-MM-dd'T'HH:mm" : "yyyy-MM-dd";
    const stringValue = format(date, formatString);
    
    // Simulate a native event object so parent forms don't need to change their onChange handlers
    onChange({
      target: {
        name,
        value: stringValue
      }
    });
  };

  return (
    <div className="w-full relative">
      <DatePicker
        selected={selectedDate}
        onChange={handleChange}
        showTimeSelect={showTime}
        timeFormat="HH:mm"
        timeIntervals={15}
        timeCaption="Time"
        dateFormat={showTime ? "MMM d, yyyy h:mm aa" : "MMMM d, yyyy"}
        placeholderText={placeholder || "Select a date..."}
        required={required}
        disabled={disabled}
        customInput={<CustomInput className={className} />}
        calendarClassName="custom-calendar-theme"
        popperPlacement={popperPlacement}
      />
    </div>
  );
};

export default CustomDatePicker;
